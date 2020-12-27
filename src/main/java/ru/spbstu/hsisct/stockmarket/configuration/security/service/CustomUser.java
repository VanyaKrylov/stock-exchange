package ru.spbstu.hsisct.stockmarket.configuration.security.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CustomUser implements UserDetails {
    public CustomUser(String username, String password, String role, Long id) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.id = id;
    }

    @Id
    private String username;
    private String password;
    private String role;
    private Long id;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;
    @Transient
    private Set<GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public boolean equals(Object rhs) {
        if (rhs instanceof CustomUser) {
            return username.equals(((CustomUser) rhs).username);
        }
        return false;
    }
}
