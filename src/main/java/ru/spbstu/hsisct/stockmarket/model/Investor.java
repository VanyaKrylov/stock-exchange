package ru.spbstu.hsisct.stockmarket.model;

import lombok.NonNull;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "investor")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Investor {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

     @NonNull private final UUID bankAccountId = UUID.randomUUID();

     @NonNull BigDecimal balance;

     @OneToMany(cascade = CascadeType.ALL, mappedBy = "investor")
     List<Stock> stocks;

     @OneToMany(cascade = CascadeType.ALL, mappedBy = "issuer")
     List<Order> orders;
}
