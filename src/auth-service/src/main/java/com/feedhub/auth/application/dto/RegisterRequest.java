package com.feedhub.auth.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dados de entrada para o registro de um novo usuario (perfil ALUNO).
 */
public record RegisterRequest(
        @NotBlank(message = "Nome e obrigatorio") String nome,
        @NotBlank(message = "Email e obrigatorio") @Email(message = "Email invalido") String email,
        @NotBlank(message = "Senha e obrigatoria") @Size(min = 6, message = "Senha deve ter ao menos 6 caracteres") String senha
) {
}
