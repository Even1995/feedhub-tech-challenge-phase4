package com.feedhub.feedback.application.usecase.impl;

import com.feedhub.common.constants.AppConstants;
import com.feedhub.common.exception.ForbiddenException;
import com.feedhub.common.exception.ResourceNotFoundException;
import com.feedhub.common.security.SecurityUser;
import com.feedhub.feedback.application.usecase.GetAvaliacaoUseCase;
import com.feedhub.feedback.domain.entity.Avaliacao;
import com.feedhub.feedback.domain.repository.AvaliacaoRepository;
import org.springframework.stereotype.Service;

@Service
public class GetAvaliacaoUseCaseImpl implements GetAvaliacaoUseCase {

    private final AvaliacaoRepository avaliacaoRepository;

    public GetAvaliacaoUseCaseImpl(AvaliacaoRepository avaliacaoRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
    }

    @Override
    public Avaliacao execute(String id, SecurityUser solicitante) {
        Avaliacao avaliacao = avaliacaoRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliacao nao encontrada: " + id));

        boolean isAdmin = AppConstants.ROLE_ADMIN.equalsIgnoreCase(solicitante.perfil());
        boolean isDono = avaliacao.getAlunoEmail().equalsIgnoreCase(solicitante.email());

        if (!isAdmin && !isDono) {
            throw new ForbiddenException("Voce nao tem permissao para visualizar esta avaliacao");
        }

        return avaliacao;
    }
}
