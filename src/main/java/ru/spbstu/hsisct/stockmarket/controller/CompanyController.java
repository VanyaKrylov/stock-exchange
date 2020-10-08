package ru.spbstu.hsisct.stockmarket.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.spbstu.hsisct.stockmarket.model.Company;
import ru.spbstu.hsisct.stockmarket.repository.CompanyRepository;

@Controller
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyRepository companyRepository;

    @GetMapping("/new-company")
    public String createNewCompany(Model model) {
        model.addAttribute("company", new Company());

        return "company/new-company";
    }

    @PostMapping("/add-company", consumes = "application/x-www-form-urlencoded")
    public String addNewCompany(Company company) {
        var companyEntity = companyRepository.save(company);

        return "redirect:/company/lk/" + companyEntity.getId();
    }
}
