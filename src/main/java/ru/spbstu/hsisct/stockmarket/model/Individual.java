package ru.spbstu.hsisct.stockmarket.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import ru.spbstu.hsisct.stockmarket.dto.StockDto;
import ru.spbstu.hsisct.stockmarket.model.enums.OrderOperationType;
import ru.spbstu.hsisct.stockmarket.model.enums.OrderStatus;
import ru.spbstu.hsisct.stockmarket.repository.CompanyRepository;
import ru.spbstu.hsisct.stockmarket.repository.IndividualRepository;
import ru.spbstu.hsisct.stockmarket.repository.OrderRepository;
import ru.spbstu.hsisct.stockmarket.repository.StockRepository;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Table(name = "individual")
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
public class Individual {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "individual_gen")
    @SequenceGenerator(name = "individual_gen", sequenceName = "individual_id_seq", allocationSize = 1)
    private Long id;
    @NonNull
    private String name;
    @NonNull
    private String surname;
    @NonNull
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "broker_id", referencedColumnName = "id")
    private Broker broker;
    @Nullable
    private UUID bankAccountId;
    @NonNull
    private BigDecimal capital;

    @PrePersist
    private void onCreate() {
        bankAccountId = UUID.randomUUID();
    }

    public void addMoney(final BigDecimal money, IndividualRepository individualRepository) {
        assert money.signum() > 0;

        capital = capital.add(money);
        individualRepository.save(this);
    }

    public List<Stock> viewMarketStocks(final StockRepository stockRepository) {
        return stockRepository.findAllUniqueStocks();
    }

    public void createMarketBuyOrder(final long companyId, final long amount, final OrderRepository orderRepository) {
        assert amount > 0;
        assert companyId >= 0;

        var order = constructOrder(amount, companyId, OrderOperationType.BUY);
        orderRepository.save(order);
    }

    public void createLimitedBuyOrder(final long companyId,
                                      final long amount,
                                      final BigDecimal minPrice,
                                      final BigDecimal maxPrice,
                                      final OrderRepository orderRepository) {
        assert amount > 0;
        assert companyId >= 0;
        assert minPrice.signum() >= 0;
        assert maxPrice.signum() >= 0;
        assert minPrice.compareTo(maxPrice) <= 0;

        var order = constructOrder(amount, companyId, OrderOperationType.BUY);
        order.setMinPrice(minPrice);
        order.setMaxPrice(maxPrice);
        orderRepository.save(order);
    }

    public void createSellOrder(final long companyId, final long amount, final OrderRepository orderRepository) {
        assert amount > 0;
        assert companyId >= 0;

        var order = constructOrder(amount, companyId, OrderOperationType.SELL);
        orderRepository.save(order);
    }

    public void createLimitedSellOrder(final long companyId,
                                       final long amount,
                                       final BigDecimal minPrice,
                                       final BigDecimal maxPrice,
                                       final OrderRepository orderRepository) {
        assert amount > 0;
        assert companyId >= 0;
        assert minPrice.signum() >= 0;
        assert maxPrice.signum() >= 0;
        assert minPrice.compareTo(maxPrice) <= 0;

        var order = constructOrder(amount, companyId, OrderOperationType.SELL);
        order.setMinPrice(minPrice);
        order.setMaxPrice(maxPrice);
        orderRepository.save(order);
    }

    public List<Stock> getAllOwnedStocks(final StockRepository stockRepository) {
        return stockRepository.findAllIndividualsStocks(this.id);
    }

    public List<StockDto> getAllOwnedStocksGroupedByCompanies(final StockRepository stockRepository,
                                                              final CompanyRepository companyRepository) {
        //@formatter:off
        return stockRepository.findAllIndividualsStocksGrouped(this.id)
                .stream()
                .map(s -> new StockDto(s.getAmount(),
                                       companyRepository.findById(s.getCompanyId()).orElseThrow(),
                                       s.getType()))
                .collect(Collectors.toList());
        //@formatter:on
    }

    public List<Order> getAllOrders(final OrderRepository orderRepository) {
        return orderRepository.findClientsOrders(this.id);
    }

    private Order constructOrder(final long amount, final long companyId, final OrderOperationType operationType) {
        assert amount > 0;
        assert companyId >= 0;

        return Order.builder()
                .size(amount)
                .brokerId(this.broker.getId())
                .individualId(this.id)
                .companyId(companyId)
                .operationType(operationType)
                .timestamp(LocalDateTime.now())
                .orderStatus(OrderStatus.ACTIVE)
                .isPublic(false)
                .build();
    }

    public void deleteOrder(final Long orderId, final OrderRepository orderRepository) {
        assert orderId >= 0;

        orderRepository.deleteById(orderId);
    }

    public void deleteAccount(final Long individualId,
                              final IndividualRepository individualRepository,
                              final OrderRepository orderRepository) {
        assert individualId >= 0;

        individualRepository.deleteById(individualId);
        orderRepository.findClientsOrders(individualId)
                .stream()
                .map(Order::getId)
                .forEach(orderRepository::deleteById);
    }
}
