package com.feedhub.feedback.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Dados de entrada para o cadastro de uma avaliacao. O email do aluno e extraido
 * do token JWT autenticado, nao do corpo da requisicao.
 */
public record AvaliacaoRequest(
        @NotNull(message = "Nota e obrigatoria") @Min(value = 0, message = "Nota minima e 0") @Max(value = 10, message = "Nota maxima e 10") Integer nota,
        @NotBlank(message = "Comentario e obrigatorio") String comentario
) {
}
