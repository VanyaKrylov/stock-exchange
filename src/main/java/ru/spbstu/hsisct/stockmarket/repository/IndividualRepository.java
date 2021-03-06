package ru.spbstu.hsisct.stockmarket.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.spbstu.hsisct.stockmarket.model.Individual;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IndividualRepository extends CrudRepository<Individual, Long> {

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO individuals_stocks (stock_id, individual_id, active)
        VALUES(:stockId, :indivId, true)
        """, nativeQuery = true)
    void addStock(@Param("indivId") Long indivId, @Param("stockId") Long stockId);

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE individuals_stocks SET active = false WHERE individual_id = :indivId AND stock_id = :stockId
        """, nativeQuery = true)
    void deleteStock(@Param("indivId") Long indivId, @Param("stockId") Long stockId);

    @Modifying
    @Query(value = """
        UPDATE Individual SET capital = :sum WHERE bankAccountId = :uuid
    """)
    void updateCapital(@Param("uuid") final UUID uuid, @Param("sum") final BigDecimal sum);

    @SuppressWarnings("SpringDataRepositoryMethodReturnTypeInspection")
    @Query(value = """
        SELECT capital FROM individual WHERE bank_account_id = :uuid   
    """, nativeQuery = true)
    Optional<BigDecimal> findCapitalByBankAccountId(@Param("uuid") final UUID uuid);
}
