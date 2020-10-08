package ru.spbstu.hsisct.stockmarket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.spbstu.hsisct.stockmarket.model.Payment;
import ru.spbstu.hsisct.stockmarket.repository.BrokerRepository;
import ru.spbstu.hsisct.stockmarket.repository.CompanyRepository;
import ru.spbstu.hsisct.stockmarket.repository.IndividualRepository;
import ru.spbstu.hsisct.stockmarket.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final BrokerRepository brokerRepository;
    private final CompanyRepository companyRepository;
    private final IndividualRepository individualRepository;
    private final PaymentRepository paymentRepository;

    @SuppressWarnings("DuplicatedCode")
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

    @SuppressWarnings("DuplicatedCode")
    @Transactional
    public void brokerToIndividualTransfer(final UUID brokerAccount, final UUID individualAccount, final BigDecimal sum) {
        var brokerCapital = brokerRepository.findCapitalByBankAccountId(brokerAccount);
        var individualCapital = individualRepository.findCapitalByBankAccountId(individualAccount);
        if (brokerCapital.compareTo(sum) < 0) {
            throw new IllegalArgumentException("Not enough money on broker account");
        }
        individualRepository.updateCapital(individualAccount, individualCapital.add(sum));
        brokerRepository.updateCapital(brokerAccount, brokerCapital.subtract(sum));
        paymentRepository.save(new Payment(brokerAccount, individualAccount, sum));
    }

    @Transactional
    public void individualToBrokerTransfer(final UUID individualAccount, final UUID brokerAccount, final BigDecimal sum) {
        var brokerCapital = brokerRepository.findCapitalByBankAccountId(brokerAccount);
        var individualCapital = individualRepository.findCapitalByBankAccountId(individualAccount);
        if (individualCapital.compareTo(sum) < 0) {
            throw new IllegalArgumentException("Not enough money on broker account");
        }
        individualRepository.updateCapital(individualAccount, individualCapital.subtract(sum));
        brokerRepository.updateCapital(brokerAccount, brokerCapital.add(sum));
        paymentRepository.save(new Payment(individualAccount, brokerAccount, sum));
    }
}