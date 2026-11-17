package com.feedhub.feedback.application.usecase.impl;

import com.feedhub.common.constants.AppConstants;
import com.feedhub.common.security.SecurityUser;
import com.feedhub.feedback.application.usecase.ListAvaliacoesUseCase;
import com.feedhub.feedback.domain.entity.Avaliacao;
import com.feedhub.feedback.domain.repository.AvaliacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ADMIN visualiza todas as avaliacoes; ALUNO visualiza somente as proprias.
 */
@Service
public class ListAvaliacoesUseCaseImpl implements ListAvaliacoesUseCase {

    private final AvaliacaoRepository avaliacaoRepository;

    public ListAvaliacoesUseCaseImpl(AvaliacaoRepository avaliacaoRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
    }

    @Override
    public List<Avaliacao> execute(SecurityUser solicitante) {
        if (AppConstants.ROLE_ADMIN.equalsIgnoreCase(solicitante.perfil())) {
            return avaliacaoRepository.listarTodas();
        }
        return avaliacaoRepository.listarPorAluno(solicitante.email());
    }
}
