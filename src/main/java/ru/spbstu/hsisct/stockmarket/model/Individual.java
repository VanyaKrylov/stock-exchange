package ru.spbstu.hsisct.stockmarket.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Immutable;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Table(name = "individual")
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
public class Individual {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "individual_gen")
    @SequenceGenerator(name = "individual_gen", sequenceName = "individual_id_seq", allocationSize = 1)
    private Long id;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "broker_id", referencedColumnName = "id")
    private Broker broker;
    @Nullable
    private UUID bankAccountId;
    @NonNull
    private BigDecimal capital;

    @PrePersist
    private void onCreate() {
        bankAccountId = UUID.randomUUID();
    }
}
