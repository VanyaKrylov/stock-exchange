package ru.spbstu.hsisct.stockmarket.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.spbstu.hsisct.stockmarket.model.Broker;
import ru.spbstu.hsisct.stockmarket.model.Individual;
import ru.spbstu.hsisct.stockmarket.repository.BrokerRepository;
import ru.spbstu.hsisct.stockmarket.repository.IndividualRepository;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class IndividualController {

    private final BrokerRepository brokerRepository;
    private final IndividualRepository individualRepository;

    @GetMapping("/new-user")
    public String createNewUser(Model model) {
        model.addAttribute("Individual", new Individual());
        model.addAttribute("brokers", brokerRepository.findAll());
        return "user/new-user";
    }

    @PostMapping("/add-user")
    public String addNewUser(@ModelAttribute("individual") @RequestBody Individual individual) {
        individualRepository.save(individual);
        return "redirect:/";
    }
}
