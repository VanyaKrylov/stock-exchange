package ru.spbstu.hsisct.stockmarket.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.hsisct.stockmarket.configuration.security.model.CustomUser;
import ru.spbstu.hsisct.stockmarket.configuration.security.repository.CustomUserRepository;
import ru.spbstu.hsisct.stockmarket.model.Broker;
import ru.spbstu.hsisct.stockmarket.repository.BrokerRepository;
import ru.spbstu.hsisct.stockmarket.repository.CompanyRepository;
import ru.spbstu.hsisct.stockmarket.repository.IndividualRepository;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/sign-up")
@RequiredArgsConstructor
public class SignUpController {
    private final CustomUserRepository customUserRepository;
    private final BrokerRepository brokerRepository;
    private final IndividualRepository individualRepository;
    private final CompanyRepository companyRepository;

    @PostMapping("/broker")
    public ResponseEntity<Void> createNewBroker(@RequestBody @Valid BrokerDto broker) {
        log.info(broker.getName());
        if (customUserRepository.findByUsername(broker.getName()).isPresent()) {
            throw new IllegalArgumentException(String.format("Username %s for broker is already taken", broker.getName()));
        }
        var brokerEntity = brokerRepository.save(new Broker(broker.getName(), broker.getFee(), broker.getCapital()));
        customUserRepository.save(new CustomUser(broker.getName(), "{noop}"+broker.getPassword(),"BROKER", brokerEntity.getId()));

        return ResponseEntity
                .created(URI.create("broker/lk/")) //TODO change url
                .build();
    }

}

@Data
@NoArgsConstructor
@AllArgsConstructor
class BrokerDto {
    @NotBlank(message = "Broker name can not be empty")
    private String name;
    @NotNull(message = "Fee can't be empty")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal fee;
    @NotNull(message = "Capital can't be empty")
    @DecimalMin(value = "0.0")
    private BigDecimal capital;
    @NotBlank(message = "Password can't be empty")
    private String password;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class CompanyDto {
    @NonNull
    @NotBlank(message = "Company name can't be empty")
    private String name;
    @NotNull
    @NonNull
    @DecimalMin(value = "0")
    private BigDecimal capital;
    @NotBlank(message = "Password can't be empty")
    private String password;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class IndividualDto {
    @NotBlank(message = "Name can't be empty")
    private String name;
    @NotBlank(message = "Surname can't be empty")
    private String surname;
    @NotNull(message = "Broker can't be empty")
    private Broker broker;
    @NotNull(message = "Capital can't be empty")
    @PositiveOrZero
    private BigDecimal capital;
    @NotBlank(message = "Password can't be empty")
    private String password;
}

