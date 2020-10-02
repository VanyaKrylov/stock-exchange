package ru.spbstu.hsisct.stockmarket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import ru.spbstu.hsisct.stockmarket.enums.OrderOperationType;
import ru.spbstu.hsisct.stockmarket.enums.OrderStatus;

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
import java.util.Objects;

@Data
@Entity
@Table(name = "order")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public abstract class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_gen")
    @SequenceGenerator(name = "order_gen", sequenceName = "order_id_seq", allocationSize = 1)
    private Long id;
    @NonNull
    private Long size;
    @Nullable
    private BigDecimal minPrice;
    @Nullable
    private BigDecimal maxPrice;
    @NonNull
    @Enumerated(EnumType.STRING)
    private OrderOperationType operationType;
    @NonNull
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @NonNull
    @Column(name = "public")
    private boolean isPublic;

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
