package ru.spbstu.hsisct.stockmarket.configuration.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.spbstu.hsisct.stockmarket.configuration.security.service.CustomUser;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.spbstu.hsisct.stockmarket.configuration.security.filter.JwtAuthenticationFilter.AUTHORIZATION_HEADER;
import static ru.spbstu.hsisct.stockmarket.configuration.security.filter.JwtAuthenticationFilter.ROLE_CLAIM;
import static ru.spbstu.hsisct.stockmarket.configuration.security.filter.JwtAuthenticationFilter.SECRET;

public class JwtAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            final String token = httpServletRequest.getHeader(AUTHORIZATION_HEADER);
            if (Objects.isNull(token) || !token.startsWith("Bearer ")) {
                httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            } else {
                final DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SECRET))
                        .build()
                        .verify(token.replace("Bearer ", ""));
                final String username = decodedJWT.getSubject();
                final List<String> role = decodedJWT.getClaim(ROLE_CLAIM).asList(String.class);
                final Long id = decodedJWT.getClaim("Id").asLong();
                final List<GrantedAuthority> grantedAuthorities = role.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                        new CustomUser(username, "", role.get(0), id), null, grantedAuthorities));
                filterChain.doFilter(httpServletRequest, httpServletResponse);
            }
        } catch (Exception e) {
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
        }
    }
}
