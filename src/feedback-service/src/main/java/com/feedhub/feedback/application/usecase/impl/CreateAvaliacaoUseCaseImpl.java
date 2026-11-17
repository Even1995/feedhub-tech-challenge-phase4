package com.feedhub.feedback.application.usecase.impl;

import com.feedhub.common.security.SecurityUser;
import com.feedhub.feedback.application.dto.AvaliacaoRequest;
import com.feedhub.feedback.application.port.CriticalNotificationPublisher;
import com.feedhub.feedback.application.usecase.CreateAvaliacaoUseCase;
import com.feedhub.feedback.domain.entity.Avaliacao;
import com.feedhub.feedback.domain.repository.AvaliacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CreateAvaliacaoUseCaseImpl implements CreateAvaliacaoUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateAvaliacaoUseCaseImpl.class);

    private final AvaliacaoRepository avaliacaoRepository;
    private final CriticalNotificationPublisher criticalNotificationPublisher;

    public CreateAvaliacaoUseCaseImpl(AvaliacaoRepository avaliacaoRepository,
                                       CriticalNotificationPublisher criticalNotificationPublisher) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.criticalNotificationPublisher = criticalNotificationPublisher;
    }

    @Override
    public Avaliacao execute(AvaliacaoRequest request, SecurityUser autor) {
        Avaliacao avaliacao = Avaliacao.novo(autor.email(), request.nota(), request.comentario());
        Avaliacao salva = avaliacaoRepository.salvar(avaliacao);

        if (salva.isUrgenciaAlta()) {
            log.info("Avaliacao {} com urgencia ALTA, publicando notificacao critica", salva.getId());
            criticalNotificationPublisher.publicar(salva);
        }

        return salva;
    }
}
