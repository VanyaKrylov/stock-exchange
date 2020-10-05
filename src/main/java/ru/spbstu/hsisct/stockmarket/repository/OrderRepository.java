package ru.spbstu.hsisct.stockmarket.repository;

import ru.spbstu.hsisct.stockmarket.model.Order;

public interface OrderRepository {

    Order save(Order order,
               long broker,
               long company,
               long individual);

    Order findById(long id);
}
