package ru.spbstu.hsisct.stockmarket.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class Payment<T> {
    @NonNull private final T receiver;
    @NonNull private final double sum;
}
