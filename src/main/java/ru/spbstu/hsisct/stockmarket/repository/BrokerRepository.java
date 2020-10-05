package ru.spbstu.hsisct.stockmarket.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.spbstu.hsisct.stockmarket.model.Broker;
import ru.spbstu.hsisct.stockmarket.model.Stock;

import java.beans.Transient;

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
}

