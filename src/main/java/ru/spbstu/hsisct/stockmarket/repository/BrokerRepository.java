package ru.spbstu.hsisct.stockmarket.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrokerRepository extends CrudRepository<BrokerRepository, Long> {
}

