package ru.spbstu.hsisct.stockmarket.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import ru.spbstu.hsisct.stockmarket.model.enums.OrderOperationType;
import ru.spbstu.hsisct.stockmarket.model.enums.OrderStatus;
import ru.spbstu.hsisct.stockmarket.model.enums.StockType;
import ru.spbstu.hsisct.stockmarket.repository.OrderRepository;
import ru.spbstu.hsisct.stockmarket.repository.StockRepository;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static ru.spbstu.hsisct.stockmarket.model.enums.OrderOperationType.SELL;

@Data
@Entity
@Table(name = "company")
@NoArgsConstructor
@RequiredArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company_gen")
    @SequenceGenerator(name = "company_gen", sequenceName = "company_id_seq", allocationSize = 1)
    private Long id;
    @Nullable
    private UUID bankAccountId;
    @NonNull
    private BigDecimal capital;

    @PrePersist
    private void onCreate() {
        bankAccountId = UUID.randomUUID();
    }

    public void publishCommonStocks(final long amount, final StockRepository stockRepository, final OrderRepository orderRepository) {
        publishStocks(amount, StockType.COMMON, stockRepository, orderRepository);
    }

    public void publishPreferredStocks(final long amount, final StockRepository stockRepository, final OrderRepository orderRepository) {
        publishStocks(amount, StockType.PREFERRED, stockRepository, orderRepository);
    }

    private void publishStocks(final long amount,
                               StockType stockType,
                               final StockRepository stockRepository,
                               final OrderRepository orderRepository) {
        assert Objects.nonNull(id);

        final var stocks = new ArrayList<Stock>();
        for (int i = 0; i < amount; i++) {
            stocks.add(new Stock(stockType, this));
        }
        stockRepository.saveAll(stocks);
        var order = constructOrder(amount);
        orderRepository.save(order, null, this.id, null);
    }

    private Order constructOrder(final long amount) {
        return Order.builder()
                .size(amount)
                .orderStatus(OrderStatus.ACTIVE)
                .timestamp(LocalDateTime.now())
                .isPublic(true)
                .operationType(SELL)
                .build();
    }
}
