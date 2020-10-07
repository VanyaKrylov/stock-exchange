package ru.spbstu.hsisct.stockmarket.dto;

import lombok.Value;
import ru.spbstu.hsisct.stockmarket.model.Company;

@Value
public class StockDto {
    Long amount;
    Company company;
    String type;
}
