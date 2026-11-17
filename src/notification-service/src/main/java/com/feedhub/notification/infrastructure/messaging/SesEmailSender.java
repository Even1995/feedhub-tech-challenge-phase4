package com.feedhub.notification.infrastructure.messaging;

import com.feedhub.notification.application.port.EmailSender;
import com.feedhub.notification.domain.entity.Notificacao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

/**
 * Envia emails atraves do Amazon SES.
 */
@Component
public class SesEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(SesEmailSender.class);

    private final SesClient sesClient;
    private final String senderEmail;

    public SesEmailSender(SesClient sesClient, @Value("${feedhub.notification.sender-email}") String senderEmail) {
        this.sesClient = sesClient;
        this.senderEmail = senderEmail;
    }

    @Override
    public void enviar(Notificacao notificacao) {
        try {
            SendEmailRequest request = SendEmailRequest.builder()
                    .source(senderEmail)
                    .destination(Destination.builder().toAddresses(notificacao.destinatario()).build())
                    .message(Message.builder()
                            .subject(Content.builder().data(notificacao.assunto()).build())
                            .body(Body.builder().text(Content.builder().data(notificacao.mensagem()).build()).build())
                            .build())
                    .build();

            sesClient.sendEmail(request);
            log.info("Email critico enviado para {}", notificacao.destinatario());
        } catch (Exception e) {
            log.error("Falha ao enviar email via SES para {}", notificacao.destinatario(), e);
        }
    }
}
