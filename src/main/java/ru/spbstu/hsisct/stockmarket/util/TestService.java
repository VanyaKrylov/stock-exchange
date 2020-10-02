/*
package ru.spbstu.hsisct.stockmarket.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.spbstu.hsisct.stockmarket.enums.OrderOperationType;
import ru.spbstu.hsisct.stockmarket.enums.OrderStatus;
import ru.spbstu.hsisct.stockmarket.enums.StockType;
import ru.spbstu.hsisct.stockmarket.model.*;
import ru.spbstu.hsisct.stockmarket.repository.CompanyRepository;
import ru.spbstu.hsisct.stockmarket.repository.IndividualRepository;
import ru.spbstu.hsisct.stockmarket.repository.StockRepository;
import ru.spbstu.hsisct.stockmarket.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TestService {

    @Autowired
    private IndividualRepository individualRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private CompanyRepository companyRepository;

    @Transactional
    public void execute() {
        Individual individual = new Individual();
        individual.setBalance(BigDecimal.valueOf(0.1));
        individual.setBroker((Broker) individualRepository.findById(1L).get());
        individualRepository.save(individual);
        individualRepository.findAll().forEach(System.out::println);
    }

    @Transactional
    public void execute2() {
        MarketOrder marketOrder = new MarketOrder();
        marketOrder.setIssuer((Investor) individualRepository.findById(1L).get());
        marketOrder.setOperationType(OrderOperationType.BUY);
        marketOrder.setStocks((List<Stock>) stockRepository.findAll());
        marketOrder.setOrderStatus(OrderStatus.CLOSED);
        orderRepository.save(marketOrder);
    }

    @Transactional
    public void executeStocks() {
        Stock stock = new Stock();
        stock.setCompany(companyRepository.findById(1L).get());
        stock.setInvestor((Investor) individualRepository.findById(1L).get());
        stock.setPrice(BigDecimal.valueOf(5.55));
        stock.setType(StockType.COMMON);
        stockRepository.save(stock);
    }

    @Transactional
    public void viewAllInvestors() {
        ((Investor) individualRepository.findById(1L).get()).getOrders().forEach(System.out::println);
    }
}
*/
