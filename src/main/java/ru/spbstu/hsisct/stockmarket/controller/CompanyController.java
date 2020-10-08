package ru.spbstu.hsisct.stockmarket.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.spbstu.hsisct.stockmarket.facade.CompanyFacade;
import ru.spbstu.hsisct.stockmarket.model.Company;
import ru.spbstu.hsisct.stockmarket.model.Stock;
import ru.spbstu.hsisct.stockmarket.repository.CompanyRepository;

import java.math.BigDecimal;

@Slf4j
@Controller
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyRepository companyRepository;
    private final CompanyFacade companyFacade;

    @GetMapping("/new-company")
    public String createNewCompany(Model model) {
        model.addAttribute("company", new Company());

        return "company/new-company";
    }

    @PostMapping(value = "/add-company", consumes = "application/x-www-form-urlencoded")
    public String addNewCompany(Company company) {
        var companyEntity = companyRepository.save(company);

        return "redirect:/company/lk/" + companyEntity.getId();
    }

    @GetMapping("/lk/{companyId}")
    public String getCompanyHomePage(@PathVariable("companyId") @NonNull Long companyId, Model model) {
        //model.addAttribute("investors", companyFacade.getInvestors(companyId));
        model.addAttribute("company", companyRepository.findById(companyId).orElseThrow());
        model.addAttribute("stocks", companyFacade.getStocks(companyId));
        model.addAttribute("stock", new Stock());

        return "company/lk";
    }

    @PostMapping(value = "/lk/{companyId}/publish-stocks", consumes = "application/x-www-form-urlencoded")
    public String publishNewStocks(@PathVariable("companyId") @NonNull Long companyId, StockFormDto stockFormDto) {
        companyFacade.createNewStocks(companyId, stockFormDto.getType(), stockFormDto.getAmount(), stockFormDto.getPrice());

        return "redirect:/company/lk/" + companyId;
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class StockFormDto {
    private String type;
    private Long amount;
    private BigDecimal price;
}
