package ru.spbstu.hsisct.stockmarket.model;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.spbstu.hsisct.stockmarket.repository.Test;

import javax.persistence.*;

@Data
@Entity
@Table(name = "test")
@Component
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@RequiredArgsConstructor
public class TestEntity {

    @Autowired
    @Transient
    private Test test;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String text;
}
