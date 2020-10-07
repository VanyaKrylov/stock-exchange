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


   /* @Bean
    @Transactional
    public CommandLineRunner commandLineRunner() {
        return args -> {
            *//*var broker = addBroker();
            var indiv = addIndividual(broker);
            var company = testSaveCompany();
            var order = addOrder(33L, company.getId(), indiv.getId());
//            var order = addOrder(null, company.getId(), null);
            log.info(order.toString());
            var stock = addStock();

            individualRepository.addStock(indiv.getId(), stock.getId());
            //log.info(stockRepository.getAllUniqueStocks().toString());
            log.info(stockRepository.getAllIndividualsStocks(24).toString());
            log.info(orderRepository.findClientsOrdersForBroker(31L).toString());
            log.info(orderRepository.findOrdersForBroker(33L).toString());*//*

            var broker = addBroker();
            log.info(brokerRepository.findCapitalByBankAccountId(broker.getBankAccountId()).toEngineeringString());
        };
    }*/

    private Stock addStock() {
        var company = companyRepository.findAll().iterator().next();
        var stock = stockRepository.save(new Stock(StockType.COMMON, company));
        log.info(String.valueOf(stockRepository.findById(stock.getId()).orElse(stock)));

        return stock;
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
        return brokerRepository.save(new Broker("Best broker", BigDecimal.valueOf(123), BigDecimal.valueOf(321)));
    }

    private Individual addIndividual(final Broker broker) {
        return individualRepository.save(new Individual("John", "Johny", broker, BigDecimal.valueOf(98)));
    }

    private Order addOrder(Long brokerId, long companyId, Long indivId) {
        var order = Order.builder()
                .size(1L)
                .brokerId(brokerId)
                .individualId(indivId)
                .companyId(companyId)
                .operationType(OrderOperationType.BUY)
                .orderStatus(OrderStatus.ACTIVE)
                .minPrice(BigDecimal.valueOf(40L))
                .maxPrice(BigDecimal.valueOf(60L))
                .isPublic(false)
                .timestamp(LocalDateTime.now())
                .build();
        var resOrder = orderRepository.save(order);
        var updatedOrder = orderRepository.save(resOrder);

        return resOrder;
    }
}
