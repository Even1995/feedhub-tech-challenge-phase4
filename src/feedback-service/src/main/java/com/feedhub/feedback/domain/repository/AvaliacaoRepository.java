package com.feedhub.feedback.domain.repository;

import com.feedhub.feedback.domain.entity.Avaliacao;

import java.util.List;
import java.util.Optional;

/**
 * Porta (interface) de persistencia de avaliacoes.
 */
public interface AvaliacaoRepository {

    Avaliacao salvar(Avaliacao avaliacao);

    Optional<Avaliacao> buscarPorId(String id);

    List<Avaliacao> listarTodas();

    List<Avaliacao> listarPorAluno(String alunoEmail);
}
