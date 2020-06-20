package ru.spbstu.hsisct.stockmarket.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "market_order")
public class MarketOrder extends Order {
}
