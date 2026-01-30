package com.smartusers.logitrackapi.config;

import com.smartusers.logitrackapi.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ========== ENDPOINTS PUBLICS ==========
                        .requestMatchers("/api/auth/**").permitAll()

                        // ========== UTILISATEURS ==========
                        // ADMIN: CRUD complet
                        // MANAGER et CLIENT: R/U profil uniquement (géré dans le contrôleur)
                        .requestMatchers(HttpMethod.POST, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").authenticated()
                        .requestMatchers("/api/profile/**").authenticated()

                        // ========== PRODUITS ==========
                        // ADMIN: CRUD, MANAGER: R, CLIENT: R (actifs uniquement - filtré dans contrôleur)
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/products/**")
                                .hasAnyRole("ADMIN", "MANAGER", "CLIENT")

                        // ========== ENTREPÔTS ==========
                        // ADMIN: CRUD, MANAGER: R, CLIENT: R (optionnel)
                        .requestMatchers(HttpMethod.POST, "/api/warehouses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/warehouses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/warehouses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/warehouses/**")
                                .hasAnyRole("ADMIN", "MANAGER", "CLIENT")

                        // ========== INVENTORY ==========
                        // ADMIN: R, MANAGER: R
                        .requestMatchers(HttpMethod.POST, "/api/inventories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/inventories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/inventories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/inventories/**")
                                .hasAnyRole("ADMIN", "MANAGER")

                        // ========== MOUVEMENTS STOCK ==========
                        // MANAGER: CRUD, ADMIN: R
                        .requestMatchers(HttpMethod.POST, "/api/stock-movements/**")
                                .hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/stock-movements/**")
                                .hasRole("MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/stock-movements/**")
                                .hasRole("MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/stock-movements/**")
                                .hasAnyRole("ADMIN", "MANAGER")

                        // ========== FOURNISSEURS ==========
                        // ADMIN: CRUD, MANAGER: R
                        .requestMatchers(HttpMethod.POST, "/api/suppliers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/suppliers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/suppliers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/suppliers/**")
                                .hasAnyRole("ADMIN", "MANAGER")

                        // ========== PURCHASE ORDERS ==========
                        // ADMIN: CRUD, MANAGER: R
                        .requestMatchers(HttpMethod.POST, "/api/purchase-orders/**")
                                .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/purchase-orders/**")
                                .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/purchase-orders/**")
                                .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/purchase-orders/**")
                                .hasAnyRole("ADMIN", "MANAGER")

                        // ========== COMMANDES (SALES ORDERS) ==========
                        // ADMIN: R/U, MANAGER: R/U (réservation, shipping), CLIENT: CRUD (ses commandes)
                        .requestMatchers(HttpMethod.POST, "/api/sales-orders/**")
                                .hasAnyRole("ADMIN", "CLIENT")
                        .requestMatchers(HttpMethod.PUT, "/api/sales-orders/**")
                                .hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/sales-orders/**")
                                .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/sales-orders/**")
                                .hasAnyRole("ADMIN", "MANAGER", "CLIENT")

                        // ========== SHIPMENTS ==========
                        // MANAGER: CRUD, ADMIN: R/U, CLIENT: R (ses shipments)
                        .requestMatchers(HttpMethod.POST, "/api/shipments/**")
                                .hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/shipments/**")
                                .hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/shipments/**")
                                .hasRole("MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/shipments/**")
                                .hasAnyRole("ADMIN", "MANAGER", "CLIENT")

                        // ========== CATEGORIES & CARRIERS ==========
                        .requestMatchers(HttpMethod.POST, "/api/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/categories/**")
                                .hasAnyRole("ADMIN", "MANAGER")

                        .requestMatchers(HttpMethod.POST, "/api/carriers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/carriers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/carriers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/carriers/**")
                                .hasAnyRole("ADMIN", "MANAGER")

                        .requestMatchers("/api/test/**").authenticated()


                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
