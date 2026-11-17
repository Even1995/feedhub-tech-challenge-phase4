package com.feedhub.auth.presentation.controller;

import com.feedhub.auth.application.dto.AuthResponse;
import com.feedhub.auth.application.dto.LoginRequest;
import com.feedhub.auth.application.dto.RegisterRequest;
import com.feedhub.auth.application.dto.UsuarioResponse;
import com.feedhub.auth.application.usecase.LoginUseCase;
import com.feedhub.auth.application.usecase.RegisterUserUseCase;
import com.feedhub.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Camada de apresentacao (REST) do auth-service.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase, LoginUseCase loginUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UsuarioResponse>> register(@Valid @RequestBody RegisterRequest request) {
        var usuario = registerUserUseCase.execute(request);
        var body = ApiResponse.of(UsuarioResponse.from(usuario), "Usuario cadastrado com sucesso");
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = loginUseCase.execute(request);
        return ResponseEntity.ok(ApiResponse.of(response, "Login realizado com sucesso"));
    }
}
