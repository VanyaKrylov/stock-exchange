package ru.spbstu.hsisct.stockmarket.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import ru.spbstu.hsisct.stockmarket.dto.StockDto;
import ru.spbstu.hsisct.stockmarket.model.enums.OrderStatus;
import ru.spbstu.hsisct.stockmarket.repository.BrokerRepository;
import ru.spbstu.hsisct.stockmarket.repository.CompanyRepository;
import ru.spbstu.hsisct.stockmarket.repository.IndividualRepository;
import ru.spbstu.hsisct.stockmarket.repository.OrderRepository;
import ru.spbstu.hsisct.stockmarket.repository.StockRepository;
import ru.spbstu.hsisct.stockmarket.service.PaymentService;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.spbstu.hsisct.stockmarket.model.enums.OrderStatus.CLOSED;

@Data
@Entity
@Table(name = "broker")
@NoArgsConstructor
public class Broker {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "broker_gen")
    @SequenceGenerator(name = "broker_gen", sequenceName = "broker_id_seq", allocationSize = 1)
    private Long id;
    @NotBlank(message = "Broker name can not be empty")
    private String name;
    @Nullable
    private UUID bankAccountId;
    @NotNull(message = "Fee can't be empty")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal fee;
    @NotNull(message = "Capital can't be empty")
    @DecimalMin(value = "0.0")
    private BigDecimal capital;

    public Broker(@NotBlank(message = "Broker name can not be empty") String name,
                  @NotNull(message = "Fee can't be empty") @DecimalMin(value = "0.0", inclusive = false) BigDecimal fee,
                  @NotNull(message = "Capital can't be empty") @DecimalMin(value = "0.0") BigDecimal capital) {
        this.name = name;
        this.fee = fee;
        this.capital = capital;
    }

    @PrePersist
    private void onCreate() {
        bankAccountId = UUID.randomUUID();
    }

    public List<Order> getClientsOrders(final OrderRepository orderRepository) {
        assert Objects.nonNull(id);

        return orderRepository.findClientsOrdersForBroker(this.id);
    }

    public List<Order> getSelfOrders(final OrderRepository orderRepository) {
        assert Objects.nonNull(id);

        return orderRepository.findOrdersForBroker(this.id);
    }

    public List<Order> getPurchasableCompaniesOrders(final OrderRepository orderRepository) {
        assert Objects.nonNull(id);

        return orderRepository.findPurchasableCompanyOrders();
    }

    public void buyCompanyStocks(final long amount,
                                 final Long orderId,
                                 final BrokerRepository brokerRepository,
                                 final OrderRepository orderRepository,
                                 final CompanyRepository companyRepository,
                                 final StockRepository stockRepository,
                                 final PaymentService paymentService) {
        assert amount > 0;
        assert orderId >= 0;
        var order = orderRepository.findById(orderId).orElseThrow();
        assert Objects.nonNull(order.getMinPrice());
        assert order.isPublishedByCompany();
        assert order.getSize() >= amount;

        var totalPrice = order.getMinPrice().multiply(BigDecimal.valueOf(amount));
        var company = companyRepository.findById(order.getCompanyId()).orElseThrow();
        var stocks = stockRepository.findNotOwnedStocks(company.getId()).subList(0, ((int) amount));

        paymentService.brokerToCompanyPayment(this.bankAccountId, company.getBankAccountId(), totalPrice);
        stocks.forEach(stock -> brokerRepository.addStock(this.id, stock.getId()));
        if (order.getSize() - amount == 0) {
            order.setOrderStatus(CLOSED);
        } else {
            order.setSize(order.getSize() - amount);
        }
        orderRepository.save(order);
    }

    public List<StockDto> getAllOwnedStocksGroupedByCompanies(final StockRepository stockRepository,
                                                              final CompanyRepository companyRepository) {
        //@formatter:off
        return stockRepository.findAllBrokersStocksGrouped(this.id)
                .stream()
                .map(s -> new StockDto(s.getAmount(),
                                       companyRepository.findById(s.getCompanyId()).orElseThrow(),
                                       s.getType()))
                .collect(Collectors.toList());
        //@formatter:on
    }

    public void buyClientStocks(final Long orderId,
                                final Long amount,
                                final BigDecimal price,
                                final PaymentService paymentService,
                                final IndividualRepository individualRepository,
                                final StockRepository stockRepository,
                                final OrderRepository orderRepository) {
        assert orderId >= 0;
        assert amount > 0;
        assert price.signum() >= 0;
        var order = orderRepository.findById(orderId).orElseThrow();
        if (Objects.nonNull(order.getMinPrice()) && Objects.compare(order.getMinPrice(), price, BigDecimal::compareTo) >= 0 ) {
            throw new IllegalArgumentException("Suggested price can't be lower than minimal order price");
        }
        assert order.getIndividualId() != null;

        var individual = individualRepository.findById(order.getIndividualId()).orElseThrow();
        var totalSum =
                price
                        .multiply(BigDecimal.valueOf(amount))
                        .subtract(this.fee
                                .multiply(BigDecimal.valueOf(amount))); // price * amount - (fee * amount)
        paymentService.brokerToIndividualTransfer(this.bankAccountId, individual.getBankAccountId(), totalSum);
        stockRepository.findAllIndividualsStocksForCompany(order.getIndividualId(), order.getCompanyId())
                .subList(0, amount.intValue())
                .forEach(stock -> individualRepository.deleteStock(individual.getId(), stock.getId()));
        order.setSize(order.getSize() - amount);
        orderRepository.save(order);
    }

    public void sellStocksToClient(final Long orderId,
                                   final Long amount,
                                   final BigDecimal price,
                                   final PaymentService paymentService,
                                   final IndividualRepository individualRepository,
                                   final StockRepository stockRepository,
                                   final OrderRepository orderRepository) {
        assert orderId >= 0;
        assert amount > 0;
        assert price.signum() >= 0;
        var order = orderRepository.findById(orderId).orElseThrow();
        assert order.getBrokerId() != null;
        var brokerStocksForGivenCompany = stockRepository.findAllBrokerStocksForCompany(order.getBrokerId(), order.getCompanyId());
        if (brokerStocksForGivenCompany.size() < amount) {
            throw new IllegalArgumentException("Not enough stocks of that type are owned by broker");
        }
        assert order.getIndividualId() != null;

        var individual = individualRepository.findById(order.getIndividualId()).orElseThrow();
        var totalSum =
                price
                        .multiply(BigDecimal.valueOf(amount))
                        .add(this.fee
                                .multiply(BigDecimal.valueOf(amount))); // price * amount + (fee * amount)
        paymentService.individualToBrokerTransfer(individual.getBankAccountId(), this.bankAccountId, totalSum);
        brokerStocksForGivenCompany.subList(0, amount.intValue()).forEach(stock -> individualRepository.addStock(individual.getId(), stock.getId()));
        order.setSize(order.getSize() - amount);
        if (order.getSize() == 0) {
            orderRepository.deleteById(order.getId());
        } else {
            orderRepository.save(order);
        }
    }

    public void publishOrder(final Long orderId, final OrderRepository orderRepository) {
        assert  orderId >= 0;
        var order = orderRepository.findById(orderId).orElseThrow();
        validateOrder(order);
        order.setPublic(true);
        orderRepository.save(order);
    }

    private void validateOrder(final Order order) {
        if (!Objects.equals(order.getBrokerId(), this.id)) {
            throw new IllegalArgumentException(String.format("Order %d doesn't belong to this broker", order.getId()));
        }
        if (!Objects.equals(order.getOrderStatus(), OrderStatus.ACTIVE)) {
            throw new IllegalArgumentException(String.format("The order %d is CLOSED, you can't change it", order.getId()));
        }
    }
}
