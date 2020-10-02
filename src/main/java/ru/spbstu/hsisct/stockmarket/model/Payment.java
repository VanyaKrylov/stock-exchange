package ru.spbstu.hsisct.stockmarket.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.Immutable;
import lombok.NonNull;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Immutable
@Table(name = "payment")
@NoArgsConstructor
@RequiredArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_gen")
    @SequenceGenerator(name = "payment_gen", sequenceName = "payment_id_seq", allocationSize = 1)
    private Long id;
    @NonNull
    private UUID payer;
    @NonNull
    private UUID recipient;
    @NonNull
    private BigDecimal amount;
    @CreationTimestamp
    private LocalDateTime timestamp;
}
