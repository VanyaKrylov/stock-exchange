package ru.spbstu.hsisct.stockmarket.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.spbstu.hsisct.stockmarket.model.Investor;

import java.util.List;

@Repository
public interface InvestorRepository<T extends Investor> extends CrudRepository<T, Long> {

    @Query("SELECT i from Investor i where i.id in (select s.id from Stock s where s.company.id = :companyId)")
    List<T> getInvestorsByCompanyId(@Param("companyId") Long companyId);
}
