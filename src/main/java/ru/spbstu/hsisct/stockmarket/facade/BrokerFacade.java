package ru.spbstu.hsisct.stockmarket.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.spbstu.hsisct.stockmarket.dto.OrderInfoDto;
import ru.spbstu.hsisct.stockmarket.dto.StockDto;
import ru.spbstu.hsisct.stockmarket.model.Broker;
import ru.spbstu.hsisct.stockmarket.repository.BrokerRepository;
import ru.spbstu.hsisct.stockmarket.repository.CompanyRepository;
import ru.spbstu.hsisct.stockmarket.repository.OrderRepository;
import ru.spbstu.hsisct.stockmarket.repository.StockRepository;
import ru.spbstu.hsisct.stockmarket.service.PaymentService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrokerFacade {

    private final BrokerRepository brokerRepository;
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
        var order = orderRepository.findById(orderId).orElseThrow();
        broker.buyCompanyStocks(size, order, brokerRepository, orderRepository, companyRepository, stockRepository, paymentService);
    }
}
