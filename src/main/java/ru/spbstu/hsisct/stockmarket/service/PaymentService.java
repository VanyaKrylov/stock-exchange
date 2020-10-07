package ru.spbstu.hsisct.stockmarket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.spbstu.hsisct.stockmarket.repository.BrokerRepository;
import ru.spbstu.hsisct.stockmarket.repository.CompanyRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final BrokerRepository brokerRepository;
    private final CompanyRepository companyRepository;

    @Transactional
    public void brokerToCompanyPayment(final UUID brokerAccount, final UUID companyAccount, final BigDecimal sum) {
        var brokerCapital = brokerRepository.findCapitalByBankAccountId(brokerAccount);
        if (brokerCapital.compareTo(sum) < 0) {
            throw new IllegalArgumentException("Not enough money on broker account");
        }
        companyRepository.updateCapital(companyAccount, brokerCapital.subtract(sum));
    }
}