package ru.spbstu.hsisct.stockmarket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/company")
public class CompanyController {

    @GetMapping("/new-company")
    public String createNewCompany() {
        return "company/new-company";
    }
}
