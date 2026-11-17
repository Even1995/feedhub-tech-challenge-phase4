package com.feedhub.feedback.application.dto;

import com.feedhub.feedback.domain.entity.Avaliacao;

import java.time.Instant;

/**
 * Representacao publica de uma avaliacao.
 */
public record AvaliacaoResponse(
        String id,
        String alunoEmail,
        int nota,
        String comentario,
        String urgencia,
        Instant dataCriacao
) {

    public static AvaliacaoResponse from(Avaliacao avaliacao) {
        return new AvaliacaoResponse(
                avaliacao.getId(),
                avaliacao.getAlunoEmail(),
                avaliacao.getNota(),
                avaliacao.getComentario(),
                avaliacao.getUrgencia().name(),
                avaliacao.getDataCriacao()
        );
    }
}
