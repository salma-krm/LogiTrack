package com.smartusers.logitrackapi.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
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
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("adminPass"))
                .roles("ADMIN")
                .build();

        UserDetails wm = User.builder()
                .username("wm")
                .password(passwordEncoder.encode("wmPass"))
                .roles("MANAGER")
                .build();

        UserDetails client = User.builder()
                .username("client")
                .password(passwordEncoder.encode("clientPass"))
                .roles("CLIENT")
                .build();

        return new InMemoryUserDetailsManager(admin, wm, client);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())

                .httpBasic(Customizer.withDefaults())

                .logout(logout -> logout
                        .logoutUrl("/api/Auth/Logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setHeader("WWW-Authenticate", "Basic realm=\"SmartSuppl\"");
                            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Logged out");
                        })
                        .permitAll()
                )


                .authorizeHttpRequests(auth -> auth



                        .requestMatchers("/api/products/**").hasRole("ADMIN")

                        .requestMatchers("/api/inventory/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/api/shipments/**").hasAnyRole("MANAGER", "ADMIN")

                        .requestMatchers("/api/sales-orders/**").hasAnyRole("CLIENT", "ADMIN")

                        .requestMatchers("/api/**").authenticated()
                )


                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendError(HttpStatus.FORBIDDEN.value(), "Access Denied"))
                );

        return http.build();
    }
}
