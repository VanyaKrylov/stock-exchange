package ru.spbstu.hsisct.stockmarket.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.spbstu.hsisct.stockmarket.dto.OrderInfoDto;
import ru.spbstu.hsisct.stockmarket.dto.StockDto;
import ru.spbstu.hsisct.stockmarket.model.Broker;
import ru.spbstu.hsisct.stockmarket.repository.BrokerRepository;
import ru.spbstu.hsisct.stockmarket.repository.CompanyRepository;
import ru.spbstu.hsisct.stockmarket.repository.IndividualRepository;
import ru.spbstu.hsisct.stockmarket.repository.OrderRepository;
import ru.spbstu.hsisct.stockmarket.repository.StockRepository;
import ru.spbstu.hsisct.stockmarket.service.PaymentService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrokerFacade {
    private final BrokerRepository brokerRepository;
    private final IndividualRepository individualRepository;
    private final StockRepository stockRepository;
    private final OrderRepository orderRepository;
    private final CompanyRepository companyRepository;
    private final PaymentService paymentService;

    public List<Broker> getAllBrokers() {
        return (List<Broker>) brokerRepository.findAll();
    }

    public List<OrderInfoDto> getAvailableCompanyStocks(final long brokerId) {
        var broker = brokerRepository.findById(brokerId).orElseThrow();

        return broker.getPurchasableCompaniesOrders(orderRepository)
                .stream()
                .map(order -> OrderInfoDto.of(order, companyRepository.findById(order.getCompanyId()).orElseThrow()))
                .collect(Collectors.toList());
    }

    public void addStocks(final Long brokerId, final Long orderId, final Long size) {
        var broker = brokerRepository.findById(brokerId).orElseThrow();
        broker.buyCompanyStocks(size, orderId, brokerRepository, orderRepository, companyRepository, stockRepository, paymentService);
    }

    public List<StockDto> getOwnedStocks(final long brokerId) {
        var broker = brokerRepository.findById(brokerId).orElseThrow();

        return broker.getAllOwnedStocksGroupedByCompanies(stockRepository, companyRepository);
    }

    public List<OrderInfoDto> getAllClientsOrders(final Long brokerId) {
        var broker = brokerRepository.findById(brokerId).orElseThrow();

        return broker.getClientsOrders(orderRepository)
                .stream()
                .map(order -> OrderInfoDto.of(order, companyRepository.findById(order.getCompanyId()).orElseThrow()))
                .collect(Collectors.toList());
    }

    public void buyStocksFromOrder(final Long brokerId, final Long orderId, final Long amount, final BigDecimal sum) {
        var broker = brokerRepository.findById(brokerId).orElseThrow();
        try {
            broker.buyClientStocks(orderId, amount, sum, paymentService, individualRepository, stockRepository, orderRepository);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void sellStocksForOrder(final Long brokerId, final Long orderId, final Long amount, final BigDecimal sum) {
        var broker = brokerRepository.findById(brokerId).orElseThrow();
        try {
            broker.sellStocksToClient(orderId, amount, sum, paymentService, individualRepository, stockRepository, orderRepository);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void publishOrder(final Long brokerId, final Long orderId) {
        var broker = brokerRepository.findById(brokerId).orElseThrow();
        broker.publishOrder(orderId, orderRepository);
    }
}
