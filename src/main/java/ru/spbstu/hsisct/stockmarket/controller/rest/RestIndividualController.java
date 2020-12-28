package ru.spbstu.hsisct.stockmarket.controller.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.postgresql.ds.common.BaseDataSource;
import org.springframework.data.mapping.callback.ReactiveEntityCallbacks;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.hsisct.stockmarket.configuration.security.model.CustomUser;
import ru.spbstu.hsisct.stockmarket.dto.OrderDto;
import ru.spbstu.hsisct.stockmarket.dto.OrderInfoDto;
import ru.spbstu.hsisct.stockmarket.dto.StockDto;
import ru.spbstu.hsisct.stockmarket.facade.IndividualFacade;
import ru.spbstu.hsisct.stockmarket.model.Stock;
import ru.spbstu.hsisct.stockmarket.repository.StockRepository;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RequestMapping("/api/v0/user")
@RestController
@RequiredArgsConstructor
public class RestIndividualController {
    private final IndividualFacade individualFacade;

    @GetMapping("/lk/capital")
    public BigDecimal getCapital(final Authentication authentication) {
        return individualFacade.getCapital(getId(authentication));
    }

    @GetMapping("/lk/available-stocks")
    public List<Stock> availableStocks() {
        return individualFacade.getAvailableStocks();
    }

    @GetMapping("/lk/owned-stocks")
    public List<StockDto> ownedStocks(final Authentication authentication) {
        return individualFacade.getOwnedStocks(getId(authentication));
    }

    @GetMapping("/lk/orders")
    public List<OrderInfoDto> ownedOrders(final Authentication authentication) {
        return individualFacade.getOwnedOrders(getId(authentication));
    }

    @PatchMapping("/lk/capital")
    public ResponseEntity<Void> addMoney(final Authentication authentication, @RequestBody @Valid IndividualCapitalForm individualCapitalForm) {
        individualFacade.addMoney(getId(authentication), individualCapitalForm.getCapital());

        return ResponseEntity
                .created(URI.create("user/lk/capital"))
                .build();
    }

    @PatchMapping(path = "/lk/orders", params = "buy")
    public ResponseEntity<Void> createBuyOrder(@Valid @RequestBody OrderDto orderDto, final Authentication authentication) {
        individualFacade.createBuyOrder(getId(authentication), orderDto);

        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "/lk/orders", params = "sell")
    public ResponseEntity<Void> createSellOrder(final Authentication authentication, @RequestBody @Valid OrderDto orderDto) {
        individualFacade.createSellOrder(getId(authentication), orderDto);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lk/orders")
    public ResponseEntity<Void> deteleOrder(final Authentication authentication, @RequestBody @Valid OrderId orderId) {
        individualFacade.deleteOrder(getId(authentication), orderId.getOrderId());

        return ResponseEntity.ok().build();
    }

    private Long getId(final Authentication authentication) {
        return ((CustomUser)authentication.getPrincipal()).getId();
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class IndividualCapitalForm {
    @NotNull
    @DecimalMin("0")
    private BigDecimal capital;
}