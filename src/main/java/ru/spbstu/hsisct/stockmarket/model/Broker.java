package ru.spbstu.hsisct.stockmarket.model;

import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class Broker extends Investor {
    @NonNull private double fee;
    private List<Individual> clients;

}
