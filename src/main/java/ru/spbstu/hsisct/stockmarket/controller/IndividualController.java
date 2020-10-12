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
import ru.spbstu.hsisct.stockmarket.dto.OrderDto;
import ru.spbstu.hsisct.stockmarket.facade.BrokerFacade;
import ru.spbstu.hsisct.stockmarket.facade.IndividualFacade;
import ru.spbstu.hsisct.stockmarket.model.Individual;
import ru.spbstu.hsisct.stockmarket.repository.CompanyRepository;
import ru.spbstu.hsisct.stockmarket.repository.IndividualRepository;
import ru.spbstu.hsisct.stockmarket.repository.StockRepository;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.Objects;

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
        if (!model.containsAttribute("individual")) {
            model.addAttribute("individual", new Individual());
        }
        model.addAttribute("brokers", brokerFacade.getAllBrokers());

        return "user/new-user";
    }

    @PostMapping(value = "/add-user", consumes = "application/x-www-form-urlencoded")
    public String addNewUser(@Valid Individual user, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.getFlashAttributes().clear();
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.individual", bindingResult);
            redirectAttributes.addFlashAttribute("individual", user);

            return "redirect:/user/new-user";
        }
        var individual = individualRepository.save(user);

        return "redirect:/user/lk/" + individual.getId();
    }

    @GetMapping("lk/{userId}")
    public String getUserHomePage(@PathVariable("userId") long userId, Model model) {
        if (!model.containsAttribute("individualCapital")) {
            model.addAttribute("individualCapital", new IndividualCapitalForm());
        }
        model.addAttribute("individual", individualRepository.findById(userId).orElseThrow());
        model.addAttribute("stocks", stockRepository.findAllUniqueStocks());
        if (!model.containsAttribute("order")) {
            model.addAttribute("order", new OrderDto());
        }
        model.addAttribute("companies", companyRepository.findAll());
        model.addAttribute("ownedOrders", individualFacade.getOwnedOrders(userId));
        model.addAttribute("ownedStocks", individualFacade.getOwnedStocks(userId));

        return "user/lk";
    }

    @PostMapping(value = "lk/{userId}/add-money", consumes = "application/x-www-form-urlencoded")
    public String addMoneyToUser(@PathVariable("userId") long userId,
                                 @Valid IndividualCapitalForm capital,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.getFlashAttributes().clear();
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.individualCapital", bindingResult);
            redirectAttributes.addFlashAttribute("individualCapital", capital);

            return "redirect:/user/lk/" + userId;
        }
        individualFacade.addMoney(userId, capital.getCapital());

        return "redirect:/user/lk/" + userId;
    }

    @PostMapping(value = "lk/{userId}/create-order", consumes = "application/x-www-form-urlencoded", params = "buy")
    public String createBuyOrder(@PathVariable("userId") long userId,
                                 @Valid OrderDto orderDto,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (validateOrderInput(orderDto, bindingResult, redirectAttributes)) {
            individualFacade.createBuyOrder(userId, orderDto);
        }

        return "redirect:/user/lk/" + userId;
    }

    @PostMapping(value = "lk/{userId}/create-order", consumes = "application/x-www-form-urlencoded", params = "sell")
    public String createSellOrder(@PathVariable("userId") long userId,
                                  @Valid OrderDto orderDto,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {

        if (validateOrderInput(orderDto, bindingResult, redirectAttributes)) {
            try {
                individualFacade.createSellOrder(userId, orderDto);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return "redirect:/user/lk/" + userId;
    }

    @PostMapping(value = "lk/{userId}/delete-order", consumes = "application/x-www-form-urlencoded")
    public String deleteOrder(@PathVariable("userId") long userId, Long id) {
        individualFacade.deleteOrder(userId, id);
        return "redirect:/user/lk/" + userId;
    }

    @SuppressWarnings("SpringMVCViewInspection")
    @PostMapping("lk/{userId}/delete-account")
    public String deleteAccount(@PathVariable("userId") long userId) {
        individualFacade.deleteAccount(userId);
        return "redirect:/";
    }

    private boolean validateOrderInput(final OrderDto orderDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        redirectAttributes.getFlashAttributes().clear();
        return validateOrderBinding(orderDto, bindingResult, redirectAttributes)
                && validateOrderPriceInput(orderDto, redirectAttributes);
    }

    private boolean validateOrderPriceInput(final OrderDto orderDto, RedirectAttributes redirectAttributes) {
        if (Objects.nonNull(orderDto.getMinPrice())
                && Objects.nonNull(orderDto.getMaxPrice())
                && orderDto.getMinPrice().compareTo(orderDto.getMaxPrice()) > 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Min price can't be larger than max price");

            return false;
        }

        return true;
    }

    private boolean validateOrderBinding(final OrderDto orderDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.order", bindingResult);
            redirectAttributes.addFlashAttribute("order", orderDto);

            return false;
        }

        return true;
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class IndividualCapitalForm {
    @NotNull
    @PositiveOrZero
    private BigDecimal capital;
}
