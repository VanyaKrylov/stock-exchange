package ru.spbstu.hsisct.stockmarket.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.spbstu.hsisct.stockmarket.model.Individual;
import ru.spbstu.hsisct.stockmarket.repository.IndividualRepository;

import javax.persistence.criteria.CriteriaBuilder;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class IndividualFacade {

    private final IndividualRepository individualRepository;

    public Individual addMoney(final BigDecimal sum, final Individual individual) {
        return individual.addMoney(sum, individualRepository);
    }
}
