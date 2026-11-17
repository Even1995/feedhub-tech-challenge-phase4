package com.feedhub.report.domain.repository;

import com.feedhub.report.domain.entity.AvaliacaoResumo;

import java.time.Instant;
import java.util.List;

/**
 * Porta de leitura das avaliacoes criadas em um determinado periodo.
 */
public interface AvaliacaoReadRepository {

    List<AvaliacaoResumo> listarCriadasEntre(Instant inicio, Instant fim);
}
