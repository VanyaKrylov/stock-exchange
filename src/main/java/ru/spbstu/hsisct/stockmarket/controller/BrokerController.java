package ru.spbstu.hsisct.stockmarket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping(value = "/broker")
public class BrokerController {

    @GetMapping("/new-broker")
    public String createNewBroker() {
        return "broker/new-broker";
    }
}
