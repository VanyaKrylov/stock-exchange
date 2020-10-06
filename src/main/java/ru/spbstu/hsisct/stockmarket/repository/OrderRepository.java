package ru.spbstu.hsisct.stockmarket.repository;

import ru.spbstu.hsisct.stockmarket.model.Order;

import java.util.Optional;

public interface OrderRepository {

    Order save(Order order,
               Long broker,
               Long company,
               Long individual);

    Optional<Order> findById(long id);
}
