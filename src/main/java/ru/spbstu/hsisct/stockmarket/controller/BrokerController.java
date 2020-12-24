package ru.spbstu.hsisct.stockmarket.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.spbstu.hsisct.stockmarket.facade.BrokerFacade;
import ru.spbstu.hsisct.stockmarket.model.Broker;
import ru.spbstu.hsisct.stockmarket.repository.BrokerRepository;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/broker")
public class BrokerController {
    private final BrokerRepository brokerRepository;
    private final BrokerFacade brokerFacade;

    @GetMapping("/new-broker")
    public String createNewBroker(Model model) {
        if (!model.containsAttribute("broker")) {
            model.addAttribute("broker", new Broker());
        }

        return "broker/new-broker";
    }

    @PostMapping(value = "/add-broker", consumes = "application/x-www-form-urlencoded")
    public String addNewBroker(@Valid Broker broker, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.getFlashAttributes().clear();
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.broker", bindingResult);
            redirectAttributes.addFlashAttribute("broker", broker);

            return "redirect:/broker/new-broker";
        }
        var brokerEntity = brokerRepository.save(broker);

        return "redirect:/broker/lk/" + brokerEntity.getId();
    }

    @GetMapping("/lk/{brokerId}")
    public String getBrokerHomePage(@PathVariable("brokerId") @NonNull Long brokerId, Model model) {
        model.addAttribute("broker", brokerRepository.findById(brokerId).orElseThrow());
        model.addAttribute("availCompStocks", brokerFacade.getAvailableCompanyStocks(brokerId));
        model.addAttribute("orderCompany", new OrderIdAndSize());
        model.addAttribute("ownedStocks", brokerFacade.getOwnedStocks(brokerId));
        model.addAttribute("clientsOrders", brokerFacade.getAllClientsOrders(brokerId));
        if (!model.containsAttribute("orderIdAndSize")) {
            model.addAttribute("orderIdAndSize", new OrderIdAndSize());
        }
        if (!model.containsAttribute("orderIdSizeAndPrice")) {
            model.addAttribute("orderIdSizeAndPrice", new OrderIdSizeAndPrice());
        }

        return "broker/lk";
    }

    @PostMapping(value = "/lk/{brokerId}/buy-company-stocks", consumes = "application/x-www-form-urlencoded")
    public String addCompanyStocks(@PathVariable("brokerId") long brokerId,
                                   @Valid final OrderIdAndSize orderIdAndSize,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.getFlashAttributes().clear();
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.orderIdAndSize", bindingResult);
            redirectAttributes.addFlashAttribute("orderIdAndSize", orderIdAndSize);

            return "redirect:/broker/lk/" + brokerId;
        }
        try {
            brokerFacade.addStocks(brokerId, orderIdAndSize.getId(), orderIdAndSize.getSize());
        } catch (Exception e) {
            redirectAttributes.getFlashAttributes().clear();
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/broker/lk/" + brokerId;
    }

    @PostMapping(value = "/lk/{brokerId}/manage-client-orders", consumes = "application/x-www-form-urlencoded", params = "buy")
    public String buyPendingOrdersFromClients(@PathVariable("brokerId") long brokerId,
                                              @Valid OrderIdSizeAndPrice orderIdSizeAndPrice,
                                              BindingResult bindingResult,
                                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.getFlashAttributes().clear();
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.orderIdSizeAndPrice", bindingResult);
            redirectAttributes.addFlashAttribute("orderIdSizeAndPrice", orderIdSizeAndPrice);

            return "redirect:/broker/lk/" + brokerId;
        }
        brokerFacade.buyStocksFromOrder(brokerId, orderIdSizeAndPrice.getOrderId(), orderIdSizeAndPrice.getSize(), orderIdSizeAndPrice.getPrice());

        return "redirect:/broker/lk/" + brokerId;
    }

    @PostMapping(value = "/lk/{brokerId}/manage-client-orders", consumes = "application/x-www-form-urlencoded", params = "sell")
    public String sellToPendingOrdersFromClients(@PathVariable("brokerId") long brokerId,
                                                 @Valid OrderIdSizeAndPrice orderIdSizeAndPrice,
                                                 BindingResult bindingResult,
                                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.getFlashAttributes().clear();
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.orderIdSizeAndPrice", bindingResult);
            redirectAttributes.addFlashAttribute("orderIdSizeAndPrice", orderIdSizeAndPrice);

            return "redirect:/broker/lk/" + brokerId;
        }
        brokerFacade.sellStocksForOrder(brokerId, orderIdSizeAndPrice.getOrderId(), orderIdSizeAndPrice.getSize(), orderIdSizeAndPrice.getPrice());

        return "redirect:/broker/lk/" + brokerId;
    }

    @PostMapping(value = "/lk/{brokerId}/publish-client-orders", consumes = "application/x-www-form-urlencoded")
    public String publishClientOrder(@PathVariable("brokerId") long brokerId, @Min(0) long orderId) {
        brokerFacade.publishOrder(brokerId, orderId);

        return "redirect:/broker/lk/" + brokerId;
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class OrderIdAndSize {
    @NotNull(message = "Order id can't be empty")
    @PositiveOrZero(message = "Value must be positive")
    private Long id;
    @NotNull(message = "Order size can't be empty")
    @Positive(message = "Order size can't be less than 1")
    private Long size;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class OrderIdSizeAndPrice {
    @NotNull(message = "Order id can't be empty")
    @PositiveOrZero(message = "Value must be positive")
    private Long orderId;
    @NotNull(message = "Order size can't be empty")
    @Positive(message = "Order size can't be less than 1")
    private Long size;
    @NotNull(message = "Price can't be empty")
    @DecimalMin(value = "0")
    private BigDecimal price;
}
