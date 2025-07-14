package com.example.bookstore.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired private JwtAuthFilter jwtAuthFilter;
    @Autowired private UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf().disable()
                .authorizeHttpRequests()
                // Cho phép toàn bộ auth (login, register, forgot/reset password)
                .requestMatchers("/api/auth/**").permitAll()

                // Public book data
                .requestMatchers("/api/books/**").permitAll()

                // ADMIN
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // SELLER
                .requestMatchers("/api/books/add").hasRole("SELLER")
                .requestMatchers("/api/orders/seller", "/api/orders/update-status").hasRole("SELLER")
                .requestMatchers("/api/shipping/**").hasRole("SELLER")

                // USER
                .requestMatchers("/api/orders/place", "/api/orders").hasRole("USER")
                .requestMatchers("/api/cart/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/addresses/**").hasRole("USER")
                .requestMatchers("/api/orders/*/choose-address/**").hasRole("USER")

                // Cả ADMIN và SELLER
                .requestMatchers("/api/orders/statistics").hasAnyRole("ADMIN", "SELLER")
                .requestMatchers("/api/categories/**").hasAnyRole("ADMIN", "SELLER")
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and().build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

