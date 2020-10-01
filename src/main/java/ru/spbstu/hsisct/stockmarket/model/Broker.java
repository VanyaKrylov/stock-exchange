package ru.spbstu.hsisct.stockmarket.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Table(name = "broker")
@NoArgsConstructor
public class Broker {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "broker_seq_gen")
    @SequenceGenerator(name = "broker_seq_gen", sequenceName = "broker_id_seq")
    private Long id;

    private final UUID bankAccountId = UUID.randomUUID();

    @NonNull
    private BigDecimal fee;

    @NonNull
    private BigDecimal capital;
}
