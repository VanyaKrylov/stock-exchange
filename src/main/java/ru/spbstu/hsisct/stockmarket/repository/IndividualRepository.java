package ru.spbstu.hsisct.stockmarket.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.spbstu.hsisct.stockmarket.model.Individual;

@Repository
public interface IndividualRepository extends CrudRepository<Individual, Long> {
/*
    @Query("SELECT i from Investor i where i.id in (select s.id from Stock s where s.company.id = :companyId)")
    List<T> getInvestorsByCompanyId(@Param("companyId") Long companyId);*/
}
