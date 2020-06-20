package ru.spbstu.hsisct.stockmarket.listener;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.spbstu.hsisct.stockmarket.model.TestEntity;
import ru.spbstu.hsisct.stockmarket.repository.Test;

import javax.jms.Message;

@Slf4j
@Component
public class ArtemisTestListener {

    @Autowired
    private Test testRepo;
    @Autowired
    private JmsTemplate jmsTemplate;
    private final String yours = "yours";
    private final String notYours = "not yours";

    @Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED)
    @JmsListener(destination = "${jms.queue.destination}", selector = "whos = '" + yours + "'")
    public void onMessage(@NonNull String msg) throws Exception {
        testRepo.save(new TestEntity("XYZ"));
        TestEntity entity = testRepo.findById(7L).orElseThrow();
        entity.setText(null);
        testRepo.save(entity);
    }

    @JmsListener(destination = "${jms.queue.destination}", selector = "whos = '" + notYours + "'")
    public void onMessage1(String msg) {
        log.info("From 1:" + msg);
    }

    @Transactional
    @JmsListener(destination = "DLQ")
    public void logDLQ(String msg,
                       MessageHeaderAccessor messageHeaderAccessor,
                       //@Header(value = "from") String from,
                       //@Header(value = "to") String to,
                       @Header(value = "_AMQ_ORIG_QUEUE") String destination) {
        jmsTemplate.send("DLQ."+destination, session -> {
            Message message = session.createTextMessage(msg);
            message.setStringProperty("id", "123");
            return message;
        });
        String from = (String) messageHeaderAccessor.getHeader("from");
        String to = (String) messageHeaderAccessor.getHeader("to");
        log.error("DLQ: " + msg + " " + destination + from + to);
    }
}
