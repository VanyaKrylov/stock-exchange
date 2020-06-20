package ru.spbstu.hsisct.stockmarket.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "broker")
@NoArgsConstructor
public class Broker extends Investor {

    @NonNull private BigDecimal fee;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "broker")
    private List<Individual> clients;

}
