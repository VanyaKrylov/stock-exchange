package ru.spbstu.hsisct.stockmarket.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.spbstu.hsisct.stockmarket.dto.OrderDto;
import ru.spbstu.hsisct.stockmarket.dto.OrderInfoDto;
import ru.spbstu.hsisct.stockmarket.dto.StockDto;
import ru.spbstu.hsisct.stockmarket.repository.CompanyRepository;
import ru.spbstu.hsisct.stockmarket.repository.IndividualRepository;
import ru.spbstu.hsisct.stockmarket.repository.OrderRepository;
import ru.spbstu.hsisct.stockmarket.repository.StockRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IndividualFacade {
    private final IndividualRepository individualRepository;
    private final OrderRepository orderRepository;
    private final CompanyRepository companyRepository;
    private final StockRepository stockRepository;

    public void addMoney(final Long individualId, final BigDecimal sum) {
        var individual = individualRepository.findById(individualId).orElseThrow();
        individual.addMoney(sum, individualRepository);
    }

    public void createBuyOrder(final long individualId, final OrderDto orderDto) {
        var individual = individualRepository.findById(individualId).orElseThrow();
        individual.createBuyOrder(orderDto.getId(), orderDto.getSize(), orderDto.getMinPrice(), orderDto.getMaxPrice(), orderRepository);
    }

    public void createSellOrder(final long individualId, final OrderDto orderDto) {
        var individual = individualRepository.findById(individualId).orElseThrow();
        individual.createSellOrder(orderDto.getId(), orderDto.getSize(), orderDto.getMinPrice(), orderDto.getMaxPrice(), orderRepository, stockRepository);
    }

    public List<OrderInfoDto> getOwnedOrders(final long individualId) {
        var individual = individualRepository.findById(individualId).orElseThrow();
        return individual.getAllOrders(orderRepository)
                .stream()
                .map(order -> OrderInfoDto.of(order, companyRepository.findById(order.getCompanyId()).orElseThrow()))
                .collect(Collectors.toList());
    }

    public void deleteOrder(Long individualId, Long orderId) {
        var individual = individualRepository.findById(individualId).orElseThrow();
        individual.deleteOrder(orderId, orderRepository);
    }

    public void deleteAccount(final Long individualId) {
        var individual = individualRepository.findById(individualId).orElseThrow();
        individual.deleteAccount(individualId, individualRepository, orderRepository);
    }

    public List<StockDto> getOwnedStocks(final Long individualId) {
        var individual = individualRepository.findById(individualId).orElseThrow();

        return individual.getAllOwnedStocksGroupedByCompanies(stockRepository, companyRepository);
    }
}
