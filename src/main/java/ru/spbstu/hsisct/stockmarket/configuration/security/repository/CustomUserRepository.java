package ru.spbstu.hsisct.stockmarket.configuration.security.repository;


import org.springframework.data.repository.Repository;
import ru.spbstu.hsisct.stockmarket.configuration.security.model.CustomUser;

import java.util.Optional;

public interface CustomUserRepository extends Repository<CustomUser, String> {

    Optional<CustomUser> findByUsername(final String username);
}
