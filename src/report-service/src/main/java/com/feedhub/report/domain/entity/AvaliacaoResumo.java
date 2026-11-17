package com.feedhub.report.domain.entity;

import com.feedhub.common.util.UrgenciaUtil;

import java.time.Instant;

/**
 * Projecao somente leitura de uma avaliacao, utilizada pelo report-service para
 * calcular as estatisticas do relatorio semanal.
 */
public record AvaliacaoResumo(String id, int nota, UrgenciaUtil.Urgencia urgencia, Instant dataCriacao) {
}
