package ru.spbstu.hsisct.stockmarket.listener;

import lombok.NonNull;
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
    private final String yours = "yours";
    private final String notYours = "not yours";

    @Transactional
    @JmsListener(destination = "${jms.queue.destination}", selector = "whos = '" + yours + "'")
    public void onMessage(@NonNull String msg) {
        TestEntity entity = testRepo.findById(7L).orElseThrow();
        entity.setText(msg);
        testRepo.save(entity);
    }


    @JmsListener(destination = "${jms.queue.destination}", selector = "whos = '" + notYours + "'")
    public void onMessage1(String msg) {
        log.info("From 1:" + msg);
    }
}
