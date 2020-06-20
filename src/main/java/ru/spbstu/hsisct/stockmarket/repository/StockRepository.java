package ru.spbstu.hsisct.stockmarket.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.spbstu.hsisct.stockmarket.model.Stock;

@Repository
public interface StockRepository extends CrudRepository<Stock, Long> {
}
