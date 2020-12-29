package ru.spbstu.hsisct.stockmarket.model;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.spbstu.hsisct.stockmarket.model.enums.OrderOperationType;
import ru.spbstu.hsisct.stockmarket.model.enums.OrderStatus;
import ru.spbstu.hsisct.stockmarket.model.enums.StockType;
import ru.spbstu.hsisct.stockmarket.repository.BrokerRepository;
import ru.spbstu.hsisct.stockmarket.repository.CompanyRepository;
import ru.spbstu.hsisct.stockmarket.repository.IndividualRepository;
import ru.spbstu.hsisct.stockmarket.repository.OrderRepository;
import ru.spbstu.hsisct.stockmarket.repository.StockRepository;
import ru.spbstu.hsisct.stockmarket.service.PaymentService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class IndividualTest {
    Order order = Order.builder()
            .id(2L)
            .orderStatus(OrderStatus.ACTIVE)
            .companyId(1L)
            .isPublic(true)
            .maxPrice(new BigDecimal(34))
            .minPrice(new BigDecimal(12))
            .operationType(OrderOperationType.SELL)
            .timestamp(LocalDateTime.now())
            .size(23L).build();
    Order order2= Order.builder()
            .id(2L)
            .orderStatus(OrderStatus.ACTIVE)
            .companyId(1L)
            .isPublic(true)
            .maxPrice(new BigDecimal(34))
            .minPrice(new BigDecimal(12))
            .operationType(OrderOperationType.BUY)
            .timestamp(LocalDateTime.now())
            .size(23L).build();
    Broker broker = new Broker("broker", new BigDecimal(12), new BigDecimal(777));
    Company company = new Company("company", new BigDecimal(660));
    Stock stock = new Stock(StockType.COMMON, company);
    Individual individual = new Individual("a", "b", broker, new BigDecimal(888));
    BrokerRepository brokerRepository = Mockito.mock(BrokerRepository.class);
    OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
    CompanyRepository companyRepository = Mockito.mock(CompanyRepository.class);
    StockRepository stockRepository = Mockito.mock(StockRepository.class);
    PaymentService paymentService = Mockito.mock(PaymentService.class);
    IndividualRepository individualRepository = Mockito.mock(IndividualRepository.class);

    @Test
    void addMoney() {
        assertDoesNotThrow(() -> individual.addMoney(new BigDecimal(23), individualRepository));
    }

    @Test
    void createBuyOrder() {
        broker.setId(2L);
        assertDoesNotThrow(() -> individual.createBuyOrder(1L, 1L, null, null, orderRepository));
        assertDoesNotThrow(() -> individual.createBuyOrder(1L, 1L, new BigDecimal(23), new BigDecimal(28), orderRepository));
    }

    @Test
    void createSellOrder() {
        broker.setId(2L);
        individual.setId(1L);
        when(stockRepository.countIndividualsStocksOfCompany(anyLong(), anyLong())).thenReturn(100L);
        assertDoesNotThrow(() -> individual.createSellOrder(1L, 1L, null, null, orderRepository, stockRepository));
        assertDoesNotThrow(() -> individual.createSellOrder(1L, 1L, new BigDecimal(23), new BigDecimal(28), orderRepository, stockRepository));
    }

    @Test
    void getAllOwnedStocks() {
        var res = List.of(stock);
        individual.setId(1L);
        when(stockRepository.findAllIndividualsStocks(anyLong())).thenReturn(res);
        assertIterableEquals(res, individual.getAllOwnedStocks(stockRepository));
    }
}