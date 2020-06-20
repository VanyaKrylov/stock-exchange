package ru.spbstu.hsisct.stockmarket.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.spbstu.hsisct.stockmarket.enums.OrderOperationType;
import ru.spbstu.hsisct.stockmarket.enums.OrderStatus;
import ru.spbstu.hsisct.stockmarket.enums.StockType;
import ru.spbstu.hsisct.stockmarket.model.*;
import ru.spbstu.hsisct.stockmarket.repository.CompanyRepository;
import ru.spbstu.hsisct.stockmarket.repository.InvestorRepository;
import ru.spbstu.hsisct.stockmarket.repository.OrderRepository;
import ru.spbstu.hsisct.stockmarket.repository.StockRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TestService {

    @Autowired
    private InvestorRepository investorRepository;
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
        individual.setBroker((Broker) investorRepository.findById(1L).get());
        investorRepository.save(individual);
        investorRepository.findAll().forEach(System.out::println);
    }

    @Transactional
    public void execute2() {
        MarketOrder marketOrder = new MarketOrder();
        marketOrder.setIssuer((Investor) investorRepository.findById(1L).get());
        marketOrder.setOperationType(OrderOperationType.BUY);
        marketOrder.setStocks((List<Stock>) stockRepository.findAll());
        marketOrder.setOrderStatus(OrderStatus.CLOSED);
        orderRepository.save(marketOrder);
    }

    @Transactional
    public void executeStocks() {
        Stock stock = new Stock();
        stock.setCompany(companyRepository.findById(1L).get());
        stock.setInvestor((Investor) investorRepository.findById(1L).get());
        stock.setPrice(BigDecimal.valueOf(5.55));
        stock.setType(StockType.COMMON);
        stockRepository.save(stock);
    }

    @Transactional
    public void viewAllInvestors() {
        ((Investor) investorRepository.findById(1L).get()).getOrders().forEach(System.out::println);
    }
}
