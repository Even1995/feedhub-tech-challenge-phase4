package com.feedhub.feedback.application.port;

import com.feedhub.feedback.domain.entity.Avaliacao;

/**
 * Porta de saida para publicacao de notificacoes criticas (urgencia ALTA).
 * A implementacao concreta (SNS) vive na camada de infraestrutura.
 */
public interface CriticalNotificationPublisher {

    void publicar(Avaliacao avaliacao);
}
