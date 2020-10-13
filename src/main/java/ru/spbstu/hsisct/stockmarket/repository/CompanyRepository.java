package ru.spbstu.hsisct.stockmarket.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Repository;
import ru.spbstu.hsisct.stockmarket.model.Company;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyRepository extends CrudRepository<Company, Long> {

    @Modifying
    @Query(value = """
        UPDATE Company SET capital = :sum WHERE bankAccountId = :uuid
    """)
    void updateCapital(@Param("uuid") final UUID uuid, @Param("sum") final BigDecimal sum);

    @SuppressWarnings("SpringDataRepositoryMethodReturnTypeInspection")
    @Query(value = """
        SELECT capital FROM company WHERE bank_account_id = :uuid
    """, nativeQuery = true)
    Optional<BigDecimal> findCapitalByBankAccountId(@Param("uuid") UUID companyAccount);
}
