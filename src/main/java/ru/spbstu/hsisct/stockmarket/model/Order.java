package ru.spbstu.hsisct.stockmarket.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.spbstu.hsisct.stockmarket.enums.OrderOperationType;
import ru.spbstu.hsisct.stockmarket.enums.OrderStatus;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "stock_order")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
public abstract class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Enumerated(EnumType.STRING)
    @NonNull protected OrderStatus orderStatus;

    @ManyToOne
    @JoinColumn(name = "investor_id")
    @NonNull protected Investor issuer;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "stocks_orders",
            joinColumns = @JoinColumn(name = "stock_order_id"),
            inverseJoinColumns = @JoinColumn(name = "stock_id"))
    protected List<Stock> stocks;

    @Enumerated(EnumType.STRING)
    @NonNull protected OrderOperationType operationType;

}
