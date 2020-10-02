package ru.spbstu.hsisct.stockmarket.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import ru.spbstu.hsisct.stockmarket.enums.StockType;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "stock")
@NoArgsConstructor
@RequiredArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stock_gen")
    @SequenceGenerator(name = "stock_gen", sequenceName = "stock_id_seq", allocationSize = 1)
    private Long id;
    @CreationTimestamp
    private LocalDateTime issued;
    @NonNull
    @Enumerated(EnumType.STRING)
    private StockType type;
    @NonNull
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;
}
