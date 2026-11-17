package com.feedhub.report.domain.entity;

import java.time.Instant;

/**
 * Relatorio semanal agregado de avaliacoes, gerado pelo report-service.
 */
public record RelatorioSemanal(
        Instant periodoInicio,
        Instant periodoFim,
        long totalAvaliacoes,
        double mediaNota,
        long quantidadeAlta,
        long quantidadeMedia,
        long quantidadeBaixa
) {
}
