package ru.spbstu.hsisct.stockmarket.model;

import lombok.*;
import ru.spbstu.hsisct.stockmarket.enums.StockType;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "stock")
@NoArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private StockType type;

    @ManyToOne
    @JoinColumn(name = "investor_id")
    private Investor investor;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @NonNull private BigDecimal price;
}
