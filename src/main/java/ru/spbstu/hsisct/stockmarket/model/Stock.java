package ru.spbstu.hsisct.stockmarket.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class Stock {

    private final int type;
    private final Company company;
    @NonNull
    private double price;
}
