package ru.spbstu.hsisct.stockmarket.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.spbstu.hsisct.stockmarket.model.Stock;

import java.util.List;

@Repository
public interface StockRepository extends CrudRepository<Stock, Long> {

    @Query(value = """
        SELECT * FROM stock WHERE id IN (SELECT MIN(id) FROM stock GROUP BY type, company_id)
    """, nativeQuery = true)
    List<Stock> getAllUniqueStocks();


    @Query(value = """
        SELECT * FROM stock where id IN (SELECT stock_id FROM individuals_stocks WHERE individual_id = :indivId)
    """, nativeQuery = true)
    List<Stock> getAllIndividualsStocks(@Param("indivId") final long indivId);
}
