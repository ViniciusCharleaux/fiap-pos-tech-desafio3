package br.com.fiap.desafio.postech.service;

import br.com.fiap.desafio.postech.dto.AuthRequest;
import br.com.fiap.desafio.postech.dto.AuthResponse;
import br.com.fiap.desafio.postech.repository.UserRepository;
import br.com.fiap.desafio.postech.security.JwtService;
import br.com.fiap.desafio.postech.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(token, user.getRole().name(), user.getUsername());
    }
}