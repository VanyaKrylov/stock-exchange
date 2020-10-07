package ru.spbstu.hsisct.stockmarket.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.spbstu.hsisct.stockmarket.model.Broker;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface BrokerRepository extends CrudRepository<Broker, Long> {

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO stocks_owners (stock_id, broker_id, active)
        VALUES(:stockId, :brokerId, true)
        """, nativeQuery = true)
    void addStock(@Param("brokerId") Long brokerId, @Param("stockId") Long stockId);

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE stocks_owners SET active = false WHERE broker_id = :brokerId AND stock_id = :stockId
        """, nativeQuery = true)
    void deleteStock(@Param("brokerId") Long brokerId, @Param("stockId") Long stockId);

    @Modifying
    @Query(value = """
        UPDATE Broker SET capital = :sum WHERE bankAccountId = :uuid
    """)
    void updateCapital(@Param("uuid") final UUID uuid, @Param("sum") final BigDecimal sum);

    @SuppressWarnings("SpringDataRepositoryMethodReturnTypeInspection")
    @Query(value = """
        SELECT capital FROM broker WHERE bank_account_id = :uuid
    """, nativeQuery = true)
    BigDecimal findCapitalByBankAccountId(@Param("uuid") UUID uuid);
}

