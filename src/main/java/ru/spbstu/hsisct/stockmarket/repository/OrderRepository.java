package ru.spbstu.hsisct.stockmarket.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.spbstu.hsisct.stockmarket.model.Order;

@Repository
public interface OrderRepository<T extends Order>
        extends CrudRepository<T, Long> {
}
