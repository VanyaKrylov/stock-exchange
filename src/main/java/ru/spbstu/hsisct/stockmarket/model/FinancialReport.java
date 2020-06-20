package ru.spbstu.hsisct.stockmarket.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Immutable;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "financial_report")
@Immutable
@NoArgsConstructor
public class FinancialReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
    private BigDecimal earnings;
    private BigDecimal expenses;
    private BigDecimal capital;


}
