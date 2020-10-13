package ru.spbstu.hsisct.stockmarket.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
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
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static ru.spbstu.hsisct.stockmarket.model.enums.OrderOperationType.SELL;

@Data
@Entity
@Table(name = "company")
@NoArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company_gen")
    @SequenceGenerator(name = "company_gen", sequenceName = "company_id_seq", allocationSize = 1)
    private Long id;
    @NonNull
    @NotBlank(message = "Company name can't be empty")
    private String name;
    @Nullable
    private UUID bankAccountId;
    @NotNull
    @NonNull
    @DecimalMin(value = "0")
    private BigDecimal capital;

    public Company(@NotBlank(message = "Company name can't be empty") String name,
                   @NotNull @DecimalMin(value = "0") BigDecimal capital) {
        this.name = name;
        this.capital = capital;
    }

    @PrePersist
    private void onCreate() {
        bankAccountId = UUID.randomUUID();
    }

    public void publishStocks(final String type,
                              final long amount,
                              final BigDecimal price,
                              final StockRepository stockRepository,
                              final OrderRepository orderRepository) {
        if (type.equalsIgnoreCase("common")) {
            publishCommonStocks(amount, price, stockRepository, orderRepository);
        } else {
            publishPreferredStocks(amount, price, stockRepository, orderRepository);
        }
    }

    private void publishCommonStocks(final long amount,
                                    final BigDecimal price,
                                    final StockRepository stockRepository,
                                    final OrderRepository orderRepository) {
        assert amount > 0;
        assert price.signum() >= 0;

        publishStocks(amount, price, StockType.COMMON, stockRepository, orderRepository);
    }

    private void publishPreferredStocks(final long amount,
                                       final BigDecimal price,
                                       final StockRepository stockRepository,
                                       final OrderRepository orderRepository) {
        assert amount > 0;
        assert price.signum() >= 0;

        publishStocks(amount, price, StockType.PREFERRED, stockRepository, orderRepository);
    }

    private void publishStocks(final long amount,
                               final BigDecimal price,
                               StockType stockType,
                               final StockRepository stockRepository,
                               final OrderRepository orderRepository) {
        assert amount > 0;
        assert price.signum() >= 0;
        assert Objects.nonNull(id);

        final var stocks = new ArrayList<Stock>();
        for (int i = 0; i < amount; i++) {
            stocks.add(new Stock(stockType, this));
        }
        stockRepository.saveAll(stocks);
        var order = constructOrder(amount, price);
        orderRepository.save(order);
    }

    private Order constructOrder(final long amount, final BigDecimal price) {
        assert amount > 0;
        assert price.signum() >= 0;

        return Order.builder()
                .size(amount)
                .companyId(this.id)
                .minPrice(price)
                .maxPrice(price)
                .orderStatus(OrderStatus.ACTIVE)
                .timestamp(LocalDateTime.now())
                .isPublic(true)
                .operationType(SELL)
                .build();
    }

    public List<StockRepository.StockInfoWithoutCompany> getAllStocks(final StockRepository stockRepository) {
        return stockRepository.findAllStocksForCompanyGroupedByType(this.id);
    }
}
