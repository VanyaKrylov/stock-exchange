package ru.spbstu.hsisct.stockmarket.model;

import java.util.List;

public abstract class Investor {

     List<Stock> stocks;
     List<Order> orders;
     double balance;
}
