package ru.spbstu.hsisct.stockmarket.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.spbstu.hsisct.stockmarket.model.Stock;

import java.util.List;

@Repository
public interface StockRepository extends CrudRepository<Stock, Long> {

    @Query(value = """
        SELECT DISTINCT Stock FROM Stock 
    """)
    List<Stock> getAllUniqueStocks();
}
