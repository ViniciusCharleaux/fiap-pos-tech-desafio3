package br.com.fiap.desafio.postech.controller;

import br.com.fiap.desafio.postech.dto.AuthRequest;
import br.com.fiap.desafio.postech.dto.AuthResponse;
import br.com.fiap.desafio.postech.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}