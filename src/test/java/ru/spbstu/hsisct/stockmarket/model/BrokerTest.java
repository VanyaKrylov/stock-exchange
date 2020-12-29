package ru.spbstu.hsisct.stockmarket.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.util.Assert;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BrokerTest {
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
    Individual individual = new Individual();
    BrokerRepository brokerRepository = Mockito.mock(BrokerRepository.class);
    OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
    CompanyRepository companyRepository = Mockito.mock(CompanyRepository.class);
    StockRepository stockRepository = Mockito.mock(StockRepository.class);
    PaymentService paymentService = Mockito.mock(PaymentService.class);
    IndividualRepository individualRepository = Mockito.mock(IndividualRepository.class);

    @BeforeEach
    void setup() {
        broker.setId(1L);
        broker.setBankAccountId(UUID.randomUUID());
        company.setId(1L);
        company.setBankAccountId(UUID.randomUUID());
    }

    @Test
    void getClientsOrders() {
        var expected = List.of(order);
        var orderRepository = Mockito.mock(OrderRepository.class);
        when(orderRepository.findClientsOrdersForBroker(Mockito.anyLong()))
                .thenReturn(expected);
        var res = broker.getClientsOrders(orderRepository);
        assertIterableEquals(expected, res);
    }

    @Test
    void getSelfOrders() {
        var expected = List.of(order);
        var orderRepository = Mockito.mock(OrderRepository.class);
        when(orderRepository.findOrdersForBroker(Mockito.anyLong()))
                .thenReturn(expected);
        var res = broker.getSelfOrders(orderRepository);
        assertIterableEquals(expected, res);
    }

    @Test
    void getPurchasableCompaniesOrders() {
        var expected = List.of(order);
        var orderRepository = Mockito.mock(OrderRepository.class);
        when(orderRepository.findPurchasableCompanyOrders())
                .thenReturn(expected);
        var res = broker.getPurchasableCompaniesOrders(orderRepository);
        assertIterableEquals(expected, res);
    }

    @Test
    void buyCompanyStocks() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.ofNullable(order));
        when(companyRepository.findById(anyLong())).thenReturn(Optional.ofNullable(company));
        when(stockRepository.findNotOwnedStocks(anyLong())).thenReturn(List.of(stock));
        assertDoesNotThrow(() -> broker.buyCompanyStocks(1,2L, brokerRepository,
                orderRepository, companyRepository, stockRepository, paymentService));
    }

    @Test
    void buyClientStocks() {
        order.setIndividualId(1L);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.ofNullable(order));
        when(companyRepository.findById(anyLong())).thenReturn(Optional.ofNullable(company));
        when(stockRepository.findNotOwnedStocks(anyLong())).thenReturn(List.of(stock));
        when(individualRepository.findById(anyLong())).thenReturn(Optional.ofNullable(individual));
        when(stockRepository.findAllIndividualsStocksForCompany(anyLong(),anyLong())).thenReturn(List.of(stock));
        assertDoesNotThrow(() ->
                broker.buyClientStocks(2L, 1L, new BigDecimal(14), paymentService,
                        individualRepository, stockRepository,orderRepository));
    }

    @Test
    void publishOrder() {
        order.setOrderStatus(OrderStatus.ACTIVE);
        order.setBrokerId(broker.getId());
        when(orderRepository.findById(anyLong())).thenReturn(Optional.ofNullable(order));
        assertDoesNotThrow(() -> broker.publishOrder(2L, orderRepository));
    }
}