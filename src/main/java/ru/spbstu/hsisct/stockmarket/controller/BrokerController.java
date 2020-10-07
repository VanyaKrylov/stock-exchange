package ru.spbstu.hsisct.stockmarket.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.spbstu.hsisct.stockmarket.facade.BrokerFacade;
import ru.spbstu.hsisct.stockmarket.model.Broker;
import ru.spbstu.hsisct.stockmarket.repository.BrokerRepository;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/broker")
public class BrokerController {

    private final BrokerRepository brokerRepository;
    private final BrokerFacade brokerFacade;

    @GetMapping("/new-broker")
    public String createNewBroker(Model model) {
        model.addAttribute("broker", new Broker());

        return "broker/new-broker";
    }

    @PostMapping(value = "/add-broker", consumes = "application/x-www-form-urlencoded")
    public String addNewBroker(Broker broker) {
        var brokerEntity = brokerRepository.save(broker);

        return "redirect:/broker/lk/" + brokerEntity.getId();
    }

    @GetMapping("/lk/{brokerId}")
    public String getBrokerHomePage(@PathVariable("brokerId") @NonNull Long brokerId, Model model) {
        model.addAttribute("broker", brokerRepository.findById(brokerId).orElseThrow());
        model.addAttribute("availCompStocks", brokerFacade.getAvailableCompanyStocks(brokerId));

        return "broker/lk";
    }
}
