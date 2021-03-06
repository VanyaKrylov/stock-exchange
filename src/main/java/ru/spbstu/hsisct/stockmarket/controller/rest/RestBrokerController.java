package ru.spbstu.hsisct.stockmarket.controller.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public List<Broker> getAllBrokers() {
        return brokerFacade.getAllBrokers();
    }

    @GetMapping("/lk/capital")
    public BigDecimal brokerCapital(final Authentication authentication) {
        return brokerFacade.getBrokerCapital(getId(authentication));
    }

    @GetMapping("/lk/available-stocks")
    public List<OrderInfoDto> availableStocks(final Authentication authentication) {
        return brokerFacade.getAvailableCompanyStocks(getId(authentication));
    }

    @PostMapping("/lk/company-stocks")
    public ResponseEntity<Void> buyCompanyStocks(final Authentication authentication, @RequestBody @Valid final OrderIdAndSize orderIdAndSize) {
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

    @PatchMapping(path = "/lk/client-orders", params = "buy")
    public ResponseEntity<Void> purchaseClientOrders(final Authentication authentication,
                                                     @RequestBody @Valid final OrderIdSizeAndPrice orderIdSizeAndPrice) {
        brokerFacade.buyStocksFromOrder(getId(authentication), orderIdSizeAndPrice.getOrderId(), orderIdSizeAndPrice.getSize(), orderIdSizeAndPrice.getPrice());

        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "/lk/client-orders", params = "sell")
    public ResponseEntity<Void> sellClientOrders(final Authentication authentication,
                                                 @RequestBody @Valid final OrderIdSizeAndPrice orderIdSizeAndPrice) {
        brokerFacade.sellStocksForOrder(getId(authentication), orderIdSizeAndPrice.getOrderId(), orderIdSizeAndPrice.getSize(), orderIdSizeAndPrice.getPrice());

        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "/lk/client-orders", params = "publish", consumes = "application/json")
    public ResponseEntity<Void> publishClientOrder(final Authentication authentication, @RequestBody @Valid final OrderId orderId) {
        brokerFacade.publishOrder(getId(authentication), orderId.getOrderId());

        return ResponseEntity.ok().build();
    }

    private Long getId(final Authentication authentication) {
        return ((CustomUser)authentication.getPrincipal()).getId();
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

@Data
@NoArgsConstructor
class OrderId {
    @NotNull(message = "Order id can't be empty")
    @PositiveOrZero(message = "Value must be positive")
    private Long orderId;
}
