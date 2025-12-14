package com.smartusers.logitrackapi.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())

                .httpBasic(Customizer.withDefaults())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                        .requestMatchers("/api/products/**").hasAnyRole("CLIENT","ADMIN")
                        .requestMatchers("/api/inventory/**")
                        .hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/api/shipments/**")
                        .hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/api/sales-orders/**")
                        .hasAnyRole("CLIENT", "ADMIN")
                        .requestMatchers("/api/**").authenticated()
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                        )
                        .accessDeniedHandler((req, res, e) ->
                                res.sendError(HttpStatus.FORBIDDEN.value(), "Access Denied")
                        )
                );

        return http.build();
    }
}
