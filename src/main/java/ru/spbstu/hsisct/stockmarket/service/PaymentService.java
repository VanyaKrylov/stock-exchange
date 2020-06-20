package ru.spbstu.hsisct.stockmarket.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.spbstu.hsisct.stockmarket.model.Company;
import ru.spbstu.hsisct.stockmarket.model.Investor;
import ru.spbstu.hsisct.stockmarket.model.TestEntity;
import ru.spbstu.hsisct.stockmarket.repository.Test;

import javax.jms.Message;

@Slf4j
@Service
public class PaymentService {

    @Value("${jms.queue.destination}")
    private String destination;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Test testRepo;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void pay(Company from, Investor to) {
        log.info(eventPublisher.toString());
    }

    @Transactional
    public void publish(@NonNull String msg1) {
        jmsTemplate.send(destination, session -> {
            Message msg = session.createTextMessage(msg1);
            msg.setStringProperty("whos", "not yours");
            return msg;
        });
    }

    @Transactional
    public void send(@NonNull String text) {
        insertInDb();
        jmsTemplate.send(destination, session -> {
            Message msg = session.createTextMessage(text);
            msg.setStringProperty("whos", "yours");
            return msg;
        });
    }

    @Transactional
    public void insertInDb() {
        TestEntity entity = testRepo.findById(1L).get();
        entity.setText("Test Tx logic NEW");
        testRepo.save(entity);
    }

    @JmsListener(destination = "DLQ.myqueue", selector = "id = '123'")
    public void errorListener(String msg) {
        log.error("Handled " + msg);
    }

//    public static void pay(Company from, Investor to) {
//        pay(null, null, );
//    }
}
