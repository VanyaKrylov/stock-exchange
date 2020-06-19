package ru.spbstu.hsisct.stockmarket;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.spbstu.hsisct.stockmarket.service.PaymentService;

import javax.jms.ConnectionFactory;

@SpringBootApplication
@EnableTransactionManagement
@EnableJms
public class StockMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockMarketApplication.class, args);
    }

//    @Bean
//    public JmsTransactionManager jmsTransactionManager(CachingConnectionFactory connectionFactory) {
//        return new JmsTransactionManager(connectionFactory);
//    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(@Qualifier("nonXaJmsConnectionFactory") ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory defaultJmsListenerContainerFactory = new DefaultJmsListenerContainerFactory();
        defaultJmsListenerContainerFactory.setConnectionFactory(connectionFactory);
        defaultJmsListenerContainerFactory.setSessionTransacted(true);
        defaultJmsListenerContainerFactory.setErrorHandler(Throwable::printStackTrace);
        return defaultJmsListenerContainerFactory;
    }

    @Bean
    public CommandLineRunner commandLineRunner(PaymentService paymentService) {
        return args -> {
            //paymentService.pay(null, null);
            paymentService.send("Hello again, Artemis!");
//            jmsTemplate.send(destination, session -> {
//                Message msg = session.createTextMessage("Hello, Artemis");
//                msg.setStringProperty("whos", "your");
//                return msg;
//            });
//            jmsTemplate.send(destination, session -> {
//                Message msg = session.createTextMessage("Hello again, Artemis");
//                msg.setStringProperty("whos", "yours");
//                return msg;
//            });
//            jmsTemplate.send(destination, session -> {
//                Message msg = session.createTextMessage("Hello, Artemis");
//                msg.setStringProperty("whos", "not yours");
//                return msg;
//            });
        };
    }

}
