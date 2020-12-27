package ru.spbstu.hsisct.stockmarket.controller.rest;

import lombok.Data;
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
import ru.spbstu.hsisct.stockmarket.configuration.security.service.CustomUser;
import ru.spbstu.hsisct.stockmarket.facade.BrokerFacade;
import ru.spbstu.hsisct.stockmarket.model.Broker;
import ru.spbstu.hsisct.stockmarket.repository.BrokerRepository;

import javax.validation.Valid;
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
        log.info(String.valueOf(((CustomUser)authentication.getPrincipal()).getId()));
        return brokerFacade.getAllBrokers();
    }

    @PostMapping("/new")
    public ResponseEntity<Void> createNewBroker(@Valid @RequestBody Broker broker) {
        log.info(broker.getName());
        return ResponseEntity
                .created(URI.create("broker/lk/" + brokerRepository.save(broker).getId())) //TODO change url
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
}

@Data
class ErrorMessage {
    final String error;
}
