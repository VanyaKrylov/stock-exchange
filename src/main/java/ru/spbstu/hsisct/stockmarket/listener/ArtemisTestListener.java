package ru.spbstu.hsisct.stockmarket.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.spbstu.hsisct.stockmarket.model.TestEntity;
import ru.spbstu.hsisct.stockmarket.repository.Test;

@Slf4j
@Component
public class ArtemisTestListener {

    @Autowired
    private Test testRepo;
    private final String yours = "your";
    private final String notYours = "not yours";

    @Transactional
    @JmsListener(destination = "${jms.queue.destination}", selector = "whos = '" + yours + "'")
    public void onMessage(String msg) {
        testRepo.save(new TestEntity(13l, msg));
        log.info("From 0:" + msg);
    }

    @JmsListener(destination = "${jms.queue.destination}", selector = "whos = '" + notYours + "'")
    public void onMessage1(String msg) {
        log.info("From 1:" + msg);
    }
}
