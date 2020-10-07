package ru.spbstu.hsisct.stockmarket.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.spbstu.hsisct.stockmarket.dto.OrderDto;
import ru.spbstu.hsisct.stockmarket.facade.BrokerFacade;
import ru.spbstu.hsisct.stockmarket.facade.IndividualFacade;
import ru.spbstu.hsisct.stockmarket.model.Individual;
import ru.spbstu.hsisct.stockmarket.repository.CompanyRepository;
import ru.spbstu.hsisct.stockmarket.repository.IndividualRepository;
import ru.spbstu.hsisct.stockmarket.repository.StockRepository;

import java.math.BigDecimal;

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
        model.addAttribute("individual", new Individual());
        model.addAttribute("brokers", brokerFacade.getAllBrokers());

        return "user/new-user";
    }

    @PostMapping(value = "/add-user", consumes = "application/x-www-form-urlencoded")
    public String addNewUser(Individual user) {
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
        model.addAttribute("ownedStocks", individualFacade.getOwnedStocks(userId));

        return "user/lk";
    }

    @PostMapping(value = "lk/{userId}/add-money", consumes = "application/x-www-form-urlencoded")
    public String addMoneyToUser(@PathVariable("userId") @NonNull Long userId, BigDecimal capital) {
        individualFacade.addMoney(userId, capital);

        return "redirect:/user/lk/" + userId;
    }

    @PostMapping(value = "lk/{userId}/create-order", consumes = "application/x-www-form-urlencoded", params = "buy")
    public String createBuyOrder(@PathVariable("userId") @NonNull Long userId, OrderDto orderDto) {
        individualFacade.createBuyOrder(userId, orderDto);
        return "redirect:/user/lk/" + userId;
    }

    @PostMapping(value = "lk/{userId}/create-order", consumes = "application/x-www-form-urlencoded", params = "sell")
    public String createSellOrder(@PathVariable("userId") @NonNull Long userId, OrderDto orderDto) {
        try {
            individualFacade.createSellOrder(userId, orderDto);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return "redirect:/user/lk/" + userId;
    }

    @PostMapping(value = "lk/{userId}/delete-order", consumes = "application/x-www-form-urlencoded")
    public String deleteOrder(@PathVariable("userId") @NonNull Long userId, Long id) {
        individualFacade.deleteOrder(userId, id);
        return "redirect:/user/lk/" + userId;
    }

    @SuppressWarnings("SpringMVCViewInspection")
    @PostMapping("lk/{userId}/delete-account")
    public String deleteAccount(@PathVariable("userId") @NonNull Long userId) {
        log.info(userId.toString());
        individualFacade.deleteAccount(userId);
        return "redirect:/";
    }

}
