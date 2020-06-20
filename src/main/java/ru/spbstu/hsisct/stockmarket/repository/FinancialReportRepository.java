package ru.spbstu.hsisct.stockmarket.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.spbstu.hsisct.stockmarket.model.FinancialReport;

@Repository
public interface FinancialReportRepository
        extends CrudRepository<FinancialReport, Long> {
}
