package ru.spbstu.hsisct.stockmarket.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Data
public class FinancialReport {

    private final double earnings;
    private final double expenses;
    private final double capital;


}
