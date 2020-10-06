package ru.spbstu.hsisct.stockmarket.repository;

import org.springframework.lang.Nullable;
import ru.spbstu.hsisct.stockmarket.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order,
               @Nullable Long broker,
               Long company,
               @Nullable Long individual);

    Optional<Order> findById(long id);

    List<Order> findClientsOrdersForBroker(long brokerId);

    List<Order> findOrdersForBroker(long brokerId);
}
