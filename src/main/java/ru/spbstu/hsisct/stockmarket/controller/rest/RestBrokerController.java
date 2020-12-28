package ru.spbstu.hsisct.stockmarket.controller.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.hsisct.stockmarket.configuration.security.model.CustomUser;
import ru.spbstu.hsisct.stockmarket.dto.OrderInfoDto;
import ru.spbstu.hsisct.stockmarket.dto.StockDto;
import ru.spbstu.hsisct.stockmarket.facade.BrokerFacade;
import ru.spbstu.hsisct.stockmarket.model.Broker;
import ru.spbstu.hsisct.stockmarket.repository.BrokerRepository;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v0/broker")
@RequiredArgsConstructor
public class RestBrokerController {
    private final BrokerFacade brokerFacade;
    private final BrokerRepository brokerRepository;

    @GetMapping("/all")
    public List<Broker> getAllBrokers(final Authentication authentication) {
        return brokerFacade.getAllBrokers();
    }

    @PostMapping("/new")
    public ResponseEntity<Void> createNewBroker(@Valid @RequestBody Broker broker) {
        log.info(broker.getName());
        return ResponseEntity
                .created(URI.create("broker/lk/" + brokerRepository.save(broker).getId())) //TODO change url
                .build();
    }

    @GetMapping("/lk/capital")
    public BigDecimal brokerCapital(final Authentication authentication) {
        return brokerFacade.getBrokerCapital(getId(authentication));
    }

    @GetMapping("/lk/available-stocks")
    public List<OrderInfoDto> availableStocks(final Authentication authentication) {
        return brokerFacade.getAvailableCompanyStocks(getId(authentication));
    }

    @PostMapping("/lk/buy-company-stocks")
    public ResponseEntity<Void> buyCompanyStocks(final Authentication authentication, @RequestBody final OrderIdAndSize orderIdAndSize) {
        brokerFacade.addStocks(getId(authentication), orderIdAndSize.getId(), orderIdAndSize.getSize());

        return ResponseEntity
                .created(URI.create("broker/lk/owned-stocks"))
                .build();
    }

    @GetMapping("/lk/owned-stocks")
    public List<StockDto> ownedStocks(final Authentication authentication) {
        return brokerFacade.getOwnedStocks(getId(authentication));
    }

    @GetMapping("/lk/client-orders")
    public List<OrderInfoDto> clientOrders(final Authentication authentication) {
        return brokerFacade.getAllClientsOrders(getId(authentication));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorMessage exceptionHandler(MethodArgumentNotValidException e) {
        StringBuilder errMsg = new StringBuilder();
        for (var fieldError : e.getBindingResult().getFieldErrors()) {
            errMsg.append(fieldError.getDefaultMessage()).append(";\n");
        }

        return new ErrorMessage(errMsg.toString());
    }

    private Long getId(final Authentication authentication) {
        return ((CustomUser)authentication.getPrincipal()).getId();
    }
}

@Data
class ErrorMessage {
    final String error;
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
