package ru.spbstu.hsisct.stockmarket.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.spbstu.hsisct.stockmarket.model.enums.StockType;
import ru.spbstu.hsisct.stockmarket.repository.CompanyRepository;
import ru.spbstu.hsisct.stockmarket.repository.OrderRepository;
import ru.spbstu.hsisct.stockmarket.repository.StockRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CompanyTest {
    OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
    CompanyRepository companyRepository = Mockito.mock(CompanyRepository.class);
    StockRepository stockRepository = Mockito.mock(StockRepository.class);
    Company company = new Company("company", new BigDecimal(660));

    @Test
    void publishStocks() {
        company.setId(1L);
        company.setBankAccountId(UUID.randomUUID());
        assertDoesNotThrow(() -> company.publishStocks("preferred", 10, new BigDecimal(12), stockRepository, orderRepository));
        assertDoesNotThrow(() -> company.publishStocks("common", 10, new BigDecimal(12), stockRepository, orderRepository));
    }

    @Test
    void getAllStocks() {
        company.setId(1L);
        company.setBankAccountId(UUID.randomUUID());
        when(stockRepository.findAllStocksForCompanyGroupedByType(anyLong())).thenReturn(Collections.emptyList());
        assertEquals(company.getAllStocks(stockRepository).size(), 0);
    }
}