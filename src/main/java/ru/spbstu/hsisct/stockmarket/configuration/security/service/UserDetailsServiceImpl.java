package ru.spbstu.hsisct.stockmarket.configuration.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.spbstu.hsisct.stockmarket.configuration.security.repository.CustomUserRepository;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final CustomUserRepository customUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return customUserRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("No user found for: " + username));
    }
}
