package br.com.fiap.desafio.postech.config;

import br.com.fiap.desafio.postech.entity.Role;
import br.com.fiap.desafio.postech.entity.User;
import br.com.fiap.desafio.postech.repository.UserRepository;
import br.com.fiap.desafio.postech.security.JwtAuthenticationFilter;
import br.com.fiap.desafio.postech.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/api/auth/register/staff").hasAnyAuthority("ROLE_DOCTOR", "ROLE_NURSE")
                        .requestMatchers("/h2-console/**", "/graphiql/**").permitAll()
                        .requestMatchers("/graphql").authenticated()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Não autenticado\",\"path\":\""
                                            + request.getRequestURI()
                                            + "\",\"timestamp\":\""
                                            + java.time.LocalDateTime.now()
                                            + "\"}"
                            );
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"status\":403,\"error\":\"Forbidden\",\"message\":\"Sem permissão para acessar este recurso\",\"path\":\""
                                            + request.getRequestURI()
                                            + "\",\"timestamp\":\""
                                            + java.time.LocalDateTime.now()
                                            + "\"}"
                            );
                        })
                )
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            if (userRepository.findByUsername("doctor1").isEmpty()) {
                userRepository.save(User.builder()
                        .username("doctor1").password(encoder.encode("doctor123")).role(Role.DOCTOR).build());
                userRepository.save(User.builder()
                        .username("nurse1").password(encoder.encode("nurse123")).role(Role.NURSE).build());
                userRepository.save(User.builder()
                        .username("patient1").password(encoder.encode("patient123")).role(Role.PATIENT).build());
            }
        };
    }
}