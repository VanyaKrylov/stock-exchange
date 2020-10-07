package ru.spbstu.hsisct.stockmarket.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.spbstu.hsisct.stockmarket.facade.BrokerFacade;
import ru.spbstu.hsisct.stockmarket.facade.IndividualFacade;
import ru.spbstu.hsisct.stockmarket.model.Broker;
import ru.spbstu.hsisct.stockmarket.model.Individual;
import ru.spbstu.hsisct.stockmarket.repository.BrokerRepository;
import ru.spbstu.hsisct.stockmarket.repository.IndividualRepository;
import ru.spbstu.hsisct.stockmarket.repository.StockRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class IndividualController {

    private final BrokerFacade brokerFacade;
    private final IndividualRepository individualRepository;
    private final IndividualFacade individualFacade;
    private final StockRepository stockRepository;

    @GetMapping("/new-user")
    public String createNewUser(Model model) {
        model.addAttribute("Individual", new Individual());
        model.addAttribute("brokers", brokerFacade.getAllBrokers());

        return "user/new-user";
    }

    @PostMapping("/add-user")
    public String addNewUser(@ModelAttribute("individual") @RequestBody Individual user) {
        var individual = individualRepository.save(user);

        return "redirect:/user/lk/" + individual.getId();
    }

    @GetMapping("lk/{userId}")
    public String getUserHomePage(@PathVariable("userId") @NonNull Long userId, Model model) {
        model.addAttribute("Individual", individualRepository.findById(userId).orElseThrow());
        model.addAttribute("stocks", stockRepository.getAllUniqueStocks());

        return "user/lk";
    }

    @PostMapping(value = "lk/{userId}/add-money", consumes = "application/x-www-form-urlencoded")
    public String addMoneyToUser(@PathVariable("userId") @NonNull Long userId, BigDecimal capital, Model model) {
        var individual = individualRepository.findById(userId).orElseThrow();
        individual = individualFacade.addMoney(capital, individual);
        model.addAttribute("Individual", individual);

        return "redirect:/user/lk/" + individual.getId();
    }
}
