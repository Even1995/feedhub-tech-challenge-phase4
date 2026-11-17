package com.feedhub.feedback.infrastructure.messaging;

import com.feedhub.feedback.application.port.CriticalNotificationPublisher;
import com.feedhub.feedback.domain.entity.Avaliacao;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.util.Map;

/**
 * Publica um evento no topico SNS de notificacoes criticas sempre que uma avaliacao
 * com urgencia ALTA e criada. O notification-service esta inscrito neste topico
 * e sera acionado automaticamente pela AWS para enviar o email correspondente.
 */
@Component
public class SnsCriticalNotificationPublisher implements CriticalNotificationPublisher {

    private static final Logger log = LoggerFactory.getLogger(SnsCriticalNotificationPublisher.class);

    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;
    private final String topicArn;

    public SnsCriticalNotificationPublisher(SnsClient snsClient,
                                             ObjectMapper objectMapper,
                                             @Value("${feedhub.sns.critical-topic-arn}") String topicArn) {
        this.snsClient = snsClient;
        this.objectMapper = objectMapper;
        this.topicArn = topicArn;
    }

    @Override
    public void publicar(Avaliacao avaliacao) {
        if (topicArn == null || topicArn.isBlank()) {
            log.warn("SNS_CRITICAL_TOPIC_ARN nao configurado; notificacao critica da avaliacao {} nao enviada", avaliacao.getId());
            return;
        }

        try {
            String message = objectMapper.writeValueAsString(Map.of(
                    "avaliacaoId", avaliacao.getId(),
                    "alunoEmail", avaliacao.getAlunoEmail(),
                    "nota", avaliacao.getNota(),
                    "comentario", avaliacao.getComentario(),
                    "urgencia", avaliacao.getUrgencia().name()
            ));

            snsClient.publish(PublishRequest.builder()
                    .topicArn(topicArn)
                    .subject("FeedHub - Avaliacao com urgencia ALTA")
                    .message(message)
                    .build());

            log.info("Notificacao critica publicada no SNS para a avaliacao {}", avaliacao.getId());
        } catch (Exception e) {
            log.error("Falha ao publicar notificacao critica no SNS para a avaliacao {}", avaliacao.getId(), e);
        }
    }
}
