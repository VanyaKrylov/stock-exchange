package ru.spbstu.hsisct.stockmarket.controller.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.hsisct.stockmarket.configuration.security.model.CustomUser;
import ru.spbstu.hsisct.stockmarket.facade.CompanyFacade;
import ru.spbstu.hsisct.stockmarket.repository.StockRepository;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v0/company")
@RequiredArgsConstructor
public class RestCompanyController {
    private final CompanyFacade companyFacade;

    @GetMapping("/lk/capital")
    public BigDecimal getCapital(final Authentication authentication) {
        return companyFacade.getCapital(getId(authentication));
    }

    @GetMapping("/lk/published-stocks")
    public List<StockRepository.StockInfoWithoutCompany> publishedStocks(final Authentication authentication) {
        return companyFacade.getStocks(getId(authentication));
    }

    @PostMapping("/lk/company-stocks")
    public ResponseEntity<Void> publishNewStocks(final Authentication authentication, @RequestBody @Valid final StockFormDto stockFormDto) {
        companyFacade.createNewStocks(getId(authentication), stockFormDto.getType(), stockFormDto.getAmount(), stockFormDto.getPrice());

        return ResponseEntity
                .created(URI.create("company/lk/published-stocks"))
                .build();
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
