package br.com.fiap.desafio.postech.service;

import br.com.fiap.desafio.postech.dto.AuthRequest;
import br.com.fiap.desafio.postech.dto.AuthResponse;
import br.com.fiap.desafio.postech.dto.RegisterRequest;
import br.com.fiap.desafio.postech.entity.Role;
import br.com.fiap.desafio.postech.entity.User;
import br.com.fiap.desafio.postech.repository.UserRepository;
import br.com.fiap.desafio.postech.security.JwtService;
import br.com.fiap.desafio.postech.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(user.getId(), token, user.getRole().name(), user.getUsername());
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username já existe: " + request.getUsername());
        }

        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Role inválida. Use: DOCTOR, NURSE ou PATIENT");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        User saved = userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(saved.getUsername());
        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(saved.getId(), token, saved.getRole().name(), saved.getUsername());
    }
}