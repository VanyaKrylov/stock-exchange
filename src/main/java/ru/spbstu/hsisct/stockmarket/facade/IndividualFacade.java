package ru.spbstu.hsisct.stockmarket.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.spbstu.hsisct.stockmarket.dto.OrderDto;
import ru.spbstu.hsisct.stockmarket.dto.OrderInfoDto;
import ru.spbstu.hsisct.stockmarket.model.Individual;
import ru.spbstu.hsisct.stockmarket.repository.CompanyRepository;
import ru.spbstu.hsisct.stockmarket.repository.IndividualRepository;
import ru.spbstu.hsisct.stockmarket.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IndividualFacade {

    private final IndividualRepository individualRepository;
    private final OrderRepository orderRepository;
    private final CompanyRepository companyRepository;

    public Individual addMoney(final Long individualId, final BigDecimal sum) {
        var individual = individualRepository.findById(individualId).orElseThrow();

        return individual.addMoney(sum, individualRepository);
    }

    public void createBuyOrder(final long individualId, final OrderDto orderDto) {
        var individual = individualRepository.findById(individualId).orElseThrow();
        if (orderDto.isLimitedOrder()) {
            individual.createLimitedBuyOrder(orderDto.getId(), orderDto.getSize(), orderDto.getMinPrice(), orderDto.getMaxPrice(), orderRepository);
        } else {
            individual.createBuyOrder(orderDto.getId(), orderDto.getSize(), orderRepository);
        }
    }

    public void createSellOrder(final long individualId, final OrderDto orderDto) {
        var individual = individualRepository.findById(individualId).orElseThrow();
        if (orderDto.isLimitedOrder()) {
            individual.createLimitedSellOrder(orderDto.getId(), orderDto.getSize(), orderDto.getMinPrice(), orderDto.getMaxPrice(), orderRepository);
        } else {
            individual.createSellOrder(orderDto.getId(), orderDto.getSize(), orderRepository);
        }
    }

    public List<OrderInfoDto> getOwnedOrders(final long individualId) {
        var individual = individualRepository.findById(individualId).orElseThrow();
        return individual.getAllOrders(orderRepository)
                .stream()
                .map(order -> OrderInfoDto.builder()
                        .id(order.getId())
                        .company(companyRepository.findById(order.getCompanyId()).orElseThrow())
                        .minPrice(order.getMinPrice())
                        .maxPrice(order.getMaxPrice())
                        .type(order.getOperationType().name())
                        .size(order.getSize())
                        .timestamp(order.getTimestamp())
                        .build())
                .collect(Collectors.toList());
    }

    public void deleteOrder(Long individualId, Long orderId) {
        var individual = individualRepository.findById(individualId).orElseThrow();
        individual.deleteOrder(orderId, orderRepository);
    }
}
