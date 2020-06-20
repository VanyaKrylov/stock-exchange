package ru.spbstu.hsisct.stockmarket.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "broker")
@NoArgsConstructor
public class Broker extends Investor {

    @NonNull private BigDecimal fee;

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "broker")
    private List<Individual> clients;

}
