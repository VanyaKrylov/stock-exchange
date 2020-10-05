package ru.spbstu.hsisct.stockmarket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import ru.spbstu.hsisct.stockmarket.model.Broker;
import ru.spbstu.hsisct.stockmarket.model.Individual;
import ru.spbstu.hsisct.stockmarket.model.Order;
import ru.spbstu.hsisct.stockmarket.model.enums.OrderOperationType;
import ru.spbstu.hsisct.stockmarket.model.enums.OrderStatus;
import ru.spbstu.hsisct.stockmarket.model.enums.StockType;
import ru.spbstu.hsisct.stockmarket.model.Company;
import ru.spbstu.hsisct.stockmarket.model.Stock;
import ru.spbstu.hsisct.stockmarket.repository.BrokerRepository;
import ru.spbstu.hsisct.stockmarket.repository.CompanyRepository;
import ru.spbstu.hsisct.stockmarket.repository.IndividualRepository;
import ru.spbstu.hsisct.stockmarket.repository.OrderRepository;
import ru.spbstu.hsisct.stockmarket.repository.StockRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@EnableJms
@SpringBootApplication
@RequiredArgsConstructor
@EnableTransactionManagement
public class StockMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockMarketApplication.class, args);
    }

//    @Bean
//    public JmsTransactionManager jmsTransactionManager(CachingConnectionFactory connectionFactory) {
//        return new JmsTransactionManager(connectionFactory);
//    }

    private final BrokerRepository brokerRepository;
    private final CompanyRepository companyRepository;
    private final StockRepository stockRepository;
    private final IndividualRepository individualRepository;
    private final OrderRepository orderRepository;


    @Bean
    @Transactional
    public CommandLineRunner commandLineRunner() {
        return args -> {
            var broker = addBroker();
            var indiv = addIndividual(broker);
            var company = testSaveCompany();
            var order = addOrder(broker.getId(), company.getId(), indiv.getId());
            log.info(order.toString());

            /*testStock();
            testCompany();*/
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
            //testService.viewAllInvestors();
        };
    }

    private void testStock() {
        var company = companyRepository.findAll().iterator().next();
        var stock = stockRepository.save(new Stock(StockType.COMMON, company));
        log.info(String.valueOf(stockRepository.findById(stock.getId()).orElse(stock)));
    }

    private void testCompany() {
        var company = testSaveCompany();
        var company2 = testFindCompany(company);
        testDeleteCompany(company);
        System.out.println(testFindAllCompany());
    }

    private List<Company> testFindAllCompany() {
        return (List<Company>) companyRepository.findAll();
    }

    public Company testSaveCompany() {
        return companyRepository.save(new Company(new BigDecimal("777.0")));
    }

    public Company testFindCompany(Company company) {
        return companyRepository.findById(company.getId()).get();
    }

    public void testDeleteCompany(Company company) {
        companyRepository.delete(company);
    }

    private Broker addBroker() {
        return brokerRepository.save(new Broker(BigDecimal.valueOf(123), BigDecimal.valueOf(321)));
    }

    private Individual addIndividual(final Broker broker) {
        return individualRepository.save(new Individual(broker, BigDecimal.valueOf(98)));
    }

    private Order addOrder(long brokerId, long companyId, long indivId) {
        var order = Order.builder()
                .size(1L)
                .operationType(OrderOperationType.BUY)
                .orderStatus(OrderStatus.ACTIVE)
                .isPublic(false)
                .timestamp(LocalDateTime.now()).build();
        return orderRepository.save(order, brokerId, companyId, indivId);
    }
}
