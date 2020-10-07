package ru.spbstu.hsisct.stockmarket.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.spbstu.hsisct.stockmarket.model.Broker;
import ru.spbstu.hsisct.stockmarket.repository.BrokerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrokerFacade {


    private final BrokerRepository brokerRepository;

    public List<Broker> getAllBrokers() {
        return (List<Broker>) brokerRepository.findAll();
    }
}
