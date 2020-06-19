package ru.spbstu.hsisct.stockmarket.model;

import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class Company {

    @NonNull private double capital;
    private List<Investor> investors;
    private List<FinancialReport> financialReports;

}
