package ru.spbstu.hsisct.stockmarket.facade;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.spbstu.hsisct.stockmarket.repository.CompanyRepository;
import ru.spbstu.hsisct.stockmarket.repository.OrderRepository;
import ru.spbstu.hsisct.stockmarket.repository.StockRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyFacade {
    private final CompanyRepository companyRepository;
    private final StockRepository stockRepository;
    private final OrderRepository orderRepository;

    public List<StockRepository.StockInfoWithoutCompany> getStocks(final Long companyId) {
        var company = companyRepository.findById(companyId).orElseThrow();
        return company.getAllStocks(stockRepository);
    }

    public void createNewStocks(final Long companyId, final String type, final Long amount, final BigDecimal price) {
        var company = companyRepository.findById(companyId).orElseThrow();
        if (type.equalsIgnoreCase("common")) {
            company.publishCommonStocks(amount, price, stockRepository, orderRepository);
        } else {
            company.publishPreferredStocks(amount, price, stockRepository, orderRepository);
        }
    }

    @Data
    static class Investor {
        private final Long id;
        private final String name;
        private final String type;
    }
}
