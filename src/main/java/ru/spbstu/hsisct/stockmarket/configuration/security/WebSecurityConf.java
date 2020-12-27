package ru.spbstu.hsisct.stockmarket.configuration.security;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.spbstu.hsisct.stockmarket.configuration.security.filter.JwtAuthenticationFilter;
import ru.spbstu.hsisct.stockmarket.configuration.security.filter.JwtAuthorizationFilter;
import ru.spbstu.hsisct.stockmarket.configuration.security.service.CustomUser;

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConf extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                    .cors()
                .and()
                    .csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/api/**").hasAnyAuthority("USER", "BROKER", "COMPANY")
                    //.antMatchers("/login").permitAll()
                    //.anyRequest().authenticated()
                    .antMatchers("/**").permitAll()
                .and()
                    .addFilterBefore(new JwtAuthenticationFilter("/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                    .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /*@Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("admin").password("{noop}password").authorities("USER", "ADMIN")
                .and()
                .withUser("user").password("{noop}password").authorities("USER");
    }*/

 /*   @Bean
    public UserDetailsService userDetailsService() {
        //@formatter:off
        final var brokerUserDetails = new CustomUser.CustomUserBuilder(
            User.withUsername("broker")
                .password("{noop}password")
                .authorities("BROKER")
                .build())
        .buildWithId(111);
        final var companyUserDetails = new CustomUser.CustomUserBuilder(
            User.withUsername("company")
                .password("{noop}password")
                .authorities("COMPANY")
                .build())
        .buildWithId(222);
        final var userUserDetails = new CustomUser.CustomUserBuilder(
            User.withUsername("user")
                .password("{noop}password")
                .authorities("USER")
                .build())
        .buildWithId(333);
        //@formatter:on

        return new InMemoryUserDetailsManager(brokerUserDetails, companyUserDetails, userUserDetails);
    }*/

    /*@Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("http://localhost:8080"));
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }*/
}

