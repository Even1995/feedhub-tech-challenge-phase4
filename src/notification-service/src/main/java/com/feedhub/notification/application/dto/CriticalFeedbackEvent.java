package com.feedhub.notification.application.dto;

/**
 * Evento recebido via SNS quando uma avaliacao e criada com urgencia ALTA.
 */
public record CriticalFeedbackEvent(
        String avaliacaoId,
        String alunoEmail,
        int nota,
        String comentario,
        String urgencia
) {
}
