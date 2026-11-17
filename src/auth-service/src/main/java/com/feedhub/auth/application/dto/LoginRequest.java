package com.feedhub.auth.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Dados de entrada para autenticacao.
 */
public record LoginRequest(
        @NotBlank(message = "Email e obrigatorio") @Email(message = "Email invalido") String email,
        @NotBlank(message = "Senha e obrigatoria") String senha
) {
}
