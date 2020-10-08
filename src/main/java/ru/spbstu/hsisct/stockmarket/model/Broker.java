package ru.spbstu.hsisct.stockmarket.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import ru.spbstu.hsisct.stockmarket.dto.OrderInfoDto;
import ru.spbstu.hsisct.stockmarket.dto.StockDto;
import ru.spbstu.hsisct.stockmarket.model.enums.OrderStatus;
import ru.spbstu.hsisct.stockmarket.repository.BrokerRepository;
import ru.spbstu.hsisct.stockmarket.repository.CompanyRepository;
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
@RequiredArgsConstructor
public class Broker {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "broker_gen")
    @SequenceGenerator(name = "broker_gen", sequenceName = "broker_id_seq", allocationSize = 1)
    private Long id;
    @NonNull
    private String name;
    @Nullable
    private UUID bankAccountId;
    @NonNull
    private BigDecimal fee;
    @NonNull
    private BigDecimal capital;

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
                                 final Order order,
                                 final BrokerRepository brokerRepository,
                                 final OrderRepository orderRepository,
                                 final CompanyRepository companyRepository,
                                 final StockRepository stockRepository,
                                 final PaymentService paymentService) {
        assert order.isPublishedByCompany();
        assert order.getSize() >= amount;

        var totalPrice = order.getMinPrice().multiply(BigDecimal.valueOf(amount));
        var company = companyRepository.findById(order.getCompanyId());
        var stocks = stockRepository.findNotOwnedStocks().subList(0, ((int) amount));

        paymentService.brokerToCompanyPayment(this.bankAccountId, company.orElseThrow().getBankAccountId(), totalPrice);
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
        return stockRepository.getAllBrokersStocksGrouped(this.id)
                .stream()
                .map(s -> new StockDto(s.getAmount(),
                                       companyRepository.findById(s.getCompanyId()).orElseThrow(),
                                       s.getType()))
                .collect(Collectors.toList());
        //@formatter:on
    }

    public void buyClientStocks(final Order order, final Long amount, final BigDecimal sum) {

    }
}
