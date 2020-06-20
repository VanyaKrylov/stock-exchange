package ru.spbstu.hsisct.stockmarket.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "limited_order")
public class LimitedOrder extends Order {

    BigDecimal maxPrice;
    BigDecimal minPrice;
}
