package ru.spbstu.hsisct.stockmarket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import ru.spbstu.hsisct.stockmarket.repository.CompanyRepository;
import ru.spbstu.hsisct.stockmarket.repository.FinancialReportRepository;
import ru.spbstu.hsisct.stockmarket.repository.InvestorRepository;
import ru.spbstu.hsisct.stockmarket.service.PaymentService;
import ru.spbstu.hsisct.stockmarket.util.TestService;

@EnableJms
@SpringBootApplication
@EnableTransactionManagement
public class StockMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockMarketApplication.class, args);
    }

//    @Bean
//    public JmsTransactionManager jmsTransactionManager(CachingConnectionFactory connectionFactory) {
//        return new JmsTransactionManager(connectionFactory);
//    }

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private FinancialReportRepository financialReportRepository;
    @Autowired
    private InvestorRepository investorRepository;
    @Autowired
    private TestService testService;

    @Bean
    @Transactional
    public CommandLineRunner commandLineRunner(PaymentService paymentService) {
        return args -> {
//            for (int i = 0; i < 1; i++) {
//                paymentService.send(i + ": Hello again, Atomikos! Testing Tx 2");
//                paymentService.publish("Hi!");
//            }
//            Company company = new Company();
//            company.setCapital(BigDecimal.valueOf(1234.56));
//            companyRepository.save(company);
//            FinancialReport financialReport = new FinancialReport();
//            financialReport.setCapital(BigDecimal.valueOf(123.45));
//            financialReport.setCompany(companyRepository.findById(1L).get());
//            financialReport.setEarnings(BigDecimal.valueOf(12));
//            financialReport.setExpenses(BigDecimal.valueOf(43));
//            financialReportRepository.save(financialReport);
//            companyRepository.findById(1L).get().getFinancialReports().forEach(System.out::println);

//            Company company = companyRepository.findById(1L).get();
//            FinancialReport financialReport = new FinancialReport();
//            financialReport.setCapital(BigDecimal.valueOf(7.77));
//            financialReport.setCompany(company);
//            financialReport.setEarnings(BigDecimal.valueOf(77));
//            financialReport.setExpenses(BigDecimal.valueOf(77));
//            company.getFinancialReports().add(financialReport);
//            companyRepository.save(company);
//            Broker broker = new Broker();
//            broker.setBalance(BigDecimal.valueOf(9.9));
//            broker.setFee(BigDecimal.valueOf(98.76));
//            investorRepository.save(broker);
//            Individual individual = new Individual();
//            individual.setBalance(BigDecimal.valueOf(0.1));
//            individual.setBroker((Broker) investorRepository.findById(1L).get());
//            investorRepository.save(individual);
//            investorRepository.findAll().forEach(System.out::println);
            testService.viewAllInvestors();
        };
    }



}
