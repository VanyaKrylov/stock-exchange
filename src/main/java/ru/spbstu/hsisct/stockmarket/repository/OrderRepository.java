package ru.spbstu.hsisct.stockmarket.repository;

import ru.spbstu.hsisct.stockmarket.model.Order;

import java.util.Optional;

public interface OrderRepository {

    Order save(Order order,
               long broker,
               long company,
               long individual);

    Optional<Order> findById(long id);
}
