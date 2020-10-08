package ru.spbstu.hsisct.stockmarket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.spbstu.hsisct.stockmarket.model.Payment;
import ru.spbstu.hsisct.stockmarket.repository.BrokerRepository;
import ru.spbstu.hsisct.stockmarket.repository.CompanyRepository;
import ru.spbstu.hsisct.stockmarket.repository.PaymentRepository;

import javax.naming.ldap.PagedResultsControl;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final BrokerRepository brokerRepository;
    private final CompanyRepository companyRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public void brokerToCompanyPayment(final UUID brokerAccount, final UUID companyAccount, final BigDecimal sum) {
        var brokerCapital = brokerRepository.findCapitalByBankAccountId(brokerAccount);
        var companyCapital = companyRepository.findCapitalByBankAccountId(companyAccount);
        if (brokerCapital.compareTo(sum) < 0) {
            throw new IllegalArgumentException("Not enough money on broker account");
        }
        companyRepository.updateCapital(companyAccount, companyCapital.add(sum));
        brokerRepository.updateCapital(brokerAccount, brokerCapital.subtract(sum));
        paymentRepository.save(new Payment(brokerAccount, companyAccount, sum));
    }
}