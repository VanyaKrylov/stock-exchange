package ru.spbstu.hsisct.stockmarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import ru.spbstu.hsisct.stockmarket.model.Company;
import ru.spbstu.hsisct.stockmarket.model.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfoDto {
    @NonNull
    private Long id;
    @NonNull
    private Long size;
    @Nullable
    private BigDecimal minPrice;
    @Nullable
    private BigDecimal maxPrice;
    @NonNull
    private String type;
    @NonNull
    private Company company;
    @NonNull
    private LocalDateTime timestamp;

    public static OrderInfoDto of(final Order order, final Company company) {
        return OrderInfoDto.builder()
                .id(order.getId())
                .company(company)
                .minPrice(order.getMinPrice())
                .maxPrice(order.getMaxPrice())
                .type(order.getOperationType().name())
                .size(order.getSize())
                .timestamp(order.getTimestamp())
                .build();
    }
}
