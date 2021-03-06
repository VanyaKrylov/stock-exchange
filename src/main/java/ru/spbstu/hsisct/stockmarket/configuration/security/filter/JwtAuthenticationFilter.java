package ru.spbstu.hsisct.stockmarket.configuration.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.spbstu.hsisct.stockmarket.configuration.security.model.CustomUser;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private final AuthenticationManager authenticationManager;
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String SECRET = "secret";
    public static final String ROLE_CLAIM = "Role";
    public static final String ROLE_HEADER = "Role";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public JwtAuthenticationFilter(String url, AuthenticationManager authenticationManager) {
        super(new AntPathRequestMatcher(url));
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            final AppUser appUser = new ObjectMapper().readValue(request.getInputStream(), AppUser.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            appUser.username,
                            appUser.password,
                            new ArrayList<>()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        final String token = JWT.create()
                .withSubject(((CustomUser) authResult.getPrincipal()).getUsername())
                .withClaim("Role", authResult.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .withClaim("Id", ((CustomUser)authResult.getPrincipal()).getId())
                .withExpiresAt(new Date(System.currentTimeMillis() + 10003000))
                .sign(Algorithm.HMAC512(SECRET));
        final var role = authResult.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst().orElseThrow();
        final var responseBody = OBJECT_MAPPER.createObjectNode().put("location", "api/v0/" + role.toLowerCase() + "/lk/");
        response.addHeader(AUTHORIZATION_HEADER, "Bearer " + token);
        response.getWriter().write(OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(responseBody));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class AppUser {
        private String username;
        private String password;
    }
}
