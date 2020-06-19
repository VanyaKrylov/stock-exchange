package ru.spbstu.hsisct.stockmarket.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.spbstu.hsisct.stockmarket.model.Company;
import ru.spbstu.hsisct.stockmarket.model.Investor;

import javax.jms.Message;

@Slf4j
@Service
public class PaymentService {

    @Value("${jms.queue.destination}")
    private String destination;

    @Autowired
    private JmsTemplate jmsTemplate;


    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void pay(Company from, Investor to) {
        log.info(eventPublisher.toString());
    }

    public void publish(@NonNull String msg) {}

    @Transactional//(transactionManager = "jmsTransactionManager")
    public void send(@NonNull String text) {
        jmsTemplate.send(destination, session -> {
            Message msg = session.createTextMessage(text);
            msg.setStringProperty("whos", "your");
            return msg;
        });
    }

//    public static void pay(Company from, Investor to) {
//        pay(null, null, );
//    }
}
