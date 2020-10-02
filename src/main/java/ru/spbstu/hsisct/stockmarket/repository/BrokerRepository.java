package ru.spbstu.hsisct.stockmarket.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.spbstu.hsisct.stockmarket.model.Broker;

@Repository
public interface BrokerRepository extends CrudRepository<Broker, Long> {
}

