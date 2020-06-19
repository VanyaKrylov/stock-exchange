package ru.spbstu.hsisct.stockmarket;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.spbstu.hsisct.stockmarket.service.PaymentService;

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

    @Bean
    public CommandLineRunner commandLineRunner(PaymentService paymentService) {
        return args -> {
            for (int i = 0; i < 15; i++) {
                paymentService.send(i + ": Hello again, Atomikos!");
                paymentService.publish("Hi!");
            }
        };
    }

}
