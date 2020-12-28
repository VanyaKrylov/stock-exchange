package ru.spbstu.hsisct.stockmarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.bytebuddy.asm.Advice;
import org.springframework.lang.Nullable;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    @NotNull(message = "Size can't be empty")
    @Positive
    private Long size;
    @Nullable
//    @DecimalMin(value = "0")
    private BigDecimal minPrice;
    @Nullable
//    @DecimalMin(value = "0")
    private BigDecimal maxPrice;
    @NotNull
    @PositiveOrZero
    private Long id;

    public boolean isLimitedOrder() {
        return Objects.nonNull(minPrice) && Objects.nonNull(maxPrice);
    }
}
