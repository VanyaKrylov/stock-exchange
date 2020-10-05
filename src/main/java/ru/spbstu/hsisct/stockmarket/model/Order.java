package ru.spbstu.hsisct.stockmarket.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.lang.Nullable;
import ru.spbstu.hsisct.stockmarket.model.enums.OrderOperationType;
import ru.spbstu.hsisct.stockmarket.model.enums.OrderStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Entity //TODO Think of immutability in this class (jdbc does provide some)
@Table(name = "order")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_gen")
    @SequenceGenerator(name = "order_gen", sequenceName = "order_id_seq", allocationSize = 1)
    private Long id;
    @NonNull
    private Long size;
    @Nullable
    @ToString.Exclude
    private BigDecimal minPrice;
    @Nullable
    @ToString.Exclude
    private BigDecimal maxPrice;
    @NonNull
    @Enumerated(EnumType.STRING)
    private OrderOperationType operationType;
    @NonNull
    private OrderStatus orderStatus;
    @NonNull
    @Column(name = "public")
    private boolean isPublic;
    @NonNull
    private LocalDateTime timestamp;
    @Nullable
    private Long parentId;

    public Order withId(final Long id) {
        return new Order(id, this.size, this.minPrice, this.maxPrice, this.operationType, this.orderStatus, this.isPublic, this.timestamp, this.parentId);
    }

    public boolean isLimitedOrder() {
        return Objects.nonNull(minPrice) && Objects.nonNull(maxPrice);
    }

    public BigDecimal getMinPrice() {
        if (!isLimitedOrder()) {
            throw new  UnsupportedOperationException("Not a limited order");
        }

        return minPrice;
    }

    public BigDecimal getMaxPrice() {
        if (!isLimitedOrder()) {
            throw new  UnsupportedOperationException("Not a limited order");
        }

        return maxPrice;
    }
}
