package ru.spbstu.hsisct.stockmarket.model;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Table(name = "individual")
@Entity
public class Individual extends Investor {

    @ManyToOne(cascade = CascadeType.ALL)
    private Broker broker;
}
