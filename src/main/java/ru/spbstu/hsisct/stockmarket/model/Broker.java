package ru.spbstu.hsisct.stockmarket.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import ru.spbstu.hsisct.stockmarket.repository.OrderRepository;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
@Entity
@Table(name = "broker")
@NoArgsConstructor
@RequiredArgsConstructor
public class Broker {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "broker_gen")
    @SequenceGenerator(name = "broker_gen", sequenceName = "broker_id_seq", allocationSize = 1)
    private Long id;
    @Nullable
    private UUID bankAccountId;
    @NonNull
    private BigDecimal fee;
    @NonNull
    private BigDecimal capital;

    @PrePersist
    private void onCreate() {
        bankAccountId = UUID.randomUUID();
    }

    public List<Order> getClientsOrders(final OrderRepository orderRepository) {
        assert Objects.nonNull(id);

        return orderRepository.findClientsOrdersForBroker(this.id);
    }

    public List<Order> getSelfOrders(final OrderRepository orderRepository) {
        assert Objects.nonNull(id);

        return orderRepository.findOrdersForBroker(this.id);
    }
}
