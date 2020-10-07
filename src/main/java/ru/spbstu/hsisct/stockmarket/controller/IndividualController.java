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
import ru.spbstu.hsisct.stockmarket.dto.OrderDto;
import ru.spbstu.hsisct.stockmarket.facade.BrokerFacade;
import ru.spbstu.hsisct.stockmarket.facade.IndividualFacade;
import ru.spbstu.hsisct.stockmarket.model.Broker;
import ru.spbstu.hsisct.stockmarket.model.Individual;
import ru.spbstu.hsisct.stockmarket.repository.BrokerRepository;
import ru.spbstu.hsisct.stockmarket.repository.CompanyRepository;
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
    private final CompanyRepository companyRepository;

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
        model.addAttribute("order", new OrderDto());
        model.addAttribute("companies", companyRepository.findAll());
        model.addAttribute("ownedOrders", individualFacade.getOwnedOrders(userId));

        return "user/lk";
    }

    @PostMapping(value = "lk/{userId}/add-money", consumes = "application/x-www-form-urlencoded")
    public String addMoneyToUser(@PathVariable("userId") @NonNull Long userId, BigDecimal capital, Model model) {
        var individual = individualFacade.addMoney(capital, userId);
        model.addAttribute("Individual", individual);

        return "redirect:/user/lk/" + individual.getId();
    }

    @PostMapping(value = "lk/{userId}/create-order", consumes = "application/x-www-form-urlencoded", params = "buy")
    public String createBuyOrder(@PathVariable("userId") @NonNull Long userId, OrderDto orderDto) {
        individualFacade.createBuyOrder(userId, orderDto);
        return "redirect:/user/lk/" + userId;
    }

    @PostMapping(value = "lk/{userId}/create-order", consumes = "application/x-www-form-urlencoded", params = "sell")
    public String createSellOrder(@PathVariable("userId") @NonNull Long userId, OrderDto orderDto) {
        individualFacade.createSellOrder(userId, orderDto);
        return "redirect:/user/lk/" + userId;
    }

}
