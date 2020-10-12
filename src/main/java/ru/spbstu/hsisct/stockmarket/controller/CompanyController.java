package ru.spbstu.hsisct.stockmarket.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.spbstu.hsisct.stockmarket.facade.CompanyFacade;
import ru.spbstu.hsisct.stockmarket.model.Company;
import ru.spbstu.hsisct.stockmarket.model.Stock;
import ru.spbstu.hsisct.stockmarket.repository.CompanyRepository;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
    public String addNewCompany(@Valid Company company, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "company/new-company";
        }
        var companyEntity = companyRepository.save(company);

        return "redirect:/company/lk/" + companyEntity.getId();
    }

    @GetMapping("/lk/{companyId}")
    public String getCompanyHomePage(@PathVariable("companyId") long companyId, Model model) {
        model.addAttribute("company", companyRepository.findById(companyId).orElseThrow());
        model.addAttribute("stocks", companyFacade.getStocks(companyId));
        model.addAttribute("stock", new Stock());
        if (!model.containsAttribute("stockDto")) {
            model.addAttribute("stockDto", new StockFormDto());
        }

        return "company/lk";
    }

    @PostMapping(value = "/lk/{companyId}/publish-stocks", consumes = "application/x-www-form-urlencoded")
    public String publishNewStocks(@PathVariable("companyId") long companyId,
                                   @Valid StockFormDto stockFormDto,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.getFlashAttributes().clear();
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.stockDto", bindingResult);
            redirectAttributes.addFlashAttribute("stockDto", stockFormDto);

            return "redirect:/company/lk/" + companyId;
        }
        companyFacade.createNewStocks(companyId, stockFormDto.getType(), stockFormDto.getAmount(), stockFormDto.getPrice());

        return "redirect:/company/lk/" + companyId;
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class StockFormDto {
    @NotBlank(message = "Stock type can't be empty")
    @Pattern(regexp = "COMMON|PREFERRED", message = "Value must be either COMMON or PREFERRED")
    private String type;
    @NotNull(message = "Stocks' size can't be empty")
    @Positive(message = "Size must be greater than 0")
    private Long amount;
    @NotNull(message = "Stocks' price can't be empty")
    @PositiveOrZero(message = "Price must be 0 or greater")
    private BigDecimal price;
}
