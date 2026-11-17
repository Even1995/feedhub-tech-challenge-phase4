package com.feedhub.notification.presentation;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.feedhub.notification.NotificationServiceApplication;
import com.feedhub.notification.application.dto.CriticalFeedbackEvent;
import com.feedhub.notification.application.usecase.SendCriticalNotificationUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Ponto de entrada da funcao Lambda, acionada automaticamente pela AWS a cada
 * mensagem publicada no topico SNS de notificacoes criticas.
 */
public class NotificationHandler implements RequestHandler<SNSEvent, Void> {

    private static final Logger log = LoggerFactory.getLogger(NotificationHandler.class);

    private static final ConfigurableApplicationContext CONTEXT = new SpringApplicationBuilder(NotificationServiceApplication.class)
            .web(WebApplicationType.NONE)
            .run();

    private final SendCriticalNotificationUseCase useCase = CONTEXT.getBean(SendCriticalNotificationUseCase.class);
    private final ObjectMapper objectMapper = CONTEXT.getBean(ObjectMapper.class);

    @Override
    public Void handleRequest(SNSEvent event, Context context) {
        for (SNSEvent.SNSRecord record : event.getRecords()) {
            try {
                String message = record.getSNS().getMessage();
                CriticalFeedbackEvent feedbackEvent = objectMapper.readValue(message, CriticalFeedbackEvent.class);
                useCase.execute(feedbackEvent);
            } catch (Exception e) {
                log.error("Erro ao processar mensagem SNS de notificacao critica", e);
            }
        }
        return null;
    }
}
