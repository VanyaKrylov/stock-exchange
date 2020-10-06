package ru.spbstu.hsisct.stockmarket.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Immutable;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import ru.spbstu.hsisct.stockmarket.model.enums.OrderOperationType;
import ru.spbstu.hsisct.stockmarket.model.enums.OrderStatus;
import ru.spbstu.hsisct.stockmarket.repository.OrderRepository;
import ru.spbstu.hsisct.stockmarket.repository.StockRepository;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    public void addMoney(final BigDecimal money) {
        capital = capital.add(money);
    }

    public List<Stock> viewMarketStocks(final StockRepository stockRepository) {
        return stockRepository.getAllUniqueStocks();
    }

    public void createBuyOrder(final long companyId, final long amount, final OrderRepository orderRepository) {
        var order = constructOrder(amount, OrderOperationType.BUY);
        orderRepository.save(order, this.broker.getId(), companyId, this.id);
    }

    public void createLimitedBuyOrder(final long companyId,
                                      final long amount,
                                      final BigDecimal minPrice,
                                      final BigDecimal maxPrice,
                                      final OrderRepository orderRepository) {
        var order = constructOrder(amount, OrderOperationType.BUY);
        order.setMinPrice(minPrice);
        order.setMaxPrice(maxPrice);
        orderRepository.save(order, this.broker.getId(), companyId, this.id);
    }

    public void createSellOrder(final long companyId, final long amount, final OrderRepository orderRepository) {
        var order = constructOrder(amount, OrderOperationType.SELL);
        orderRepository.save(order, this.broker.getId(), companyId, this.id);
    }

    public void createLimitedSellOrder(final long companyId,
                                       final long amount,
                                       final BigDecimal minPrice,
                                       final BigDecimal maxPrice,
                                       final OrderRepository orderRepository) {
        var order = constructOrder(amount, OrderOperationType.SELL);
        order.setMinPrice(minPrice);
        order.setMaxPrice(maxPrice);
        orderRepository.save(order, this.broker.getId(), companyId, this.id);
    }

    public List<Stock> getAllOwnedStocks(final StockRepository stockRepository) {
        return stockRepository.getAllIndividualsStocks(this.id);
    }

    private Order constructOrder(final long amount, final OrderOperationType operationType) {
        return Order.builder()
                .size(amount)
                .operationType(operationType)
                .timestamp(LocalDateTime.now())
                .orderStatus(OrderStatus.ACTIVE)
                .isPublic(false)
                .build();
    }
}
