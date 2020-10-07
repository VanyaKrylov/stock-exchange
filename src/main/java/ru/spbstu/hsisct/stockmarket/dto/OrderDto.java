package ru.spbstu.hsisct.stockmarket.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.Nullable;
import ru.spbstu.hsisct.stockmarket.model.Company;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class OrderDto {

    @NonNull
    private Long size;
    @Nullable
    private BigDecimal minPrice;
    @Nullable
    private BigDecimal maxPrice;
    @NonNull
    private Long id;
}
