/*package ru.spbstu.hsisct.stockmarket.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.Filter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "investor")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
public abstract class Investor {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     protected Long id;

     @NonNull protected final UUID bankAccountId = UUID.randomUUID();

     @NonNull protected BigDecimal balance;

     @OneToMany(cascade = CascadeType.ALL, mappedBy = "investor", fetch = FetchType.LAZY)
     protected List<Stock> stocks;

     @OneToMany(cascade = CascadeType.ALL, mappedBy = "issuer", fetch = FetchType.LAZY)
     @Filter(name = "status_filter", condition = "order_status = ACTIVE")
     protected List<Order> orders;
}*/
