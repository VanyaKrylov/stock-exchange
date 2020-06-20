package ru.spbstu.hsisct.stockmarket.model;

import lombok.NonNull;
import ru.spbstu.hsisct.stockmarket.enums.OrderOperationType;

import javax.persistence.*;

@Entity
@Table(name = "stock_order")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "investor_id")
    @NonNull private Investor issuer;

    @Enumerated(EnumType.STRING)
    @NonNull private OrderOperationType operationType;

}
