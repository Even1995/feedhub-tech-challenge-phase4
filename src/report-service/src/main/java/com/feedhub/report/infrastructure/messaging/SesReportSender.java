package com.feedhub.report.infrastructure.messaging;

import com.feedhub.report.application.port.ReportSender;
import com.feedhub.report.domain.entity.RelatorioSemanal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.time.format.DateTimeFormatter;

/**
 * Envia o relatorio semanal por email via Amazon SES.
 */
@Component
public class SesReportSender implements ReportSender {

    private static final Logger log = LoggerFactory.getLogger(SesReportSender.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_INSTANT;

    private final SesClient sesClient;
    private final String senderEmail;
    private final String recipientEmail;

    public SesReportSender(SesClient sesClient,
                            @Value("${feedhub.notification.sender-email}") String senderEmail,
                            @Value("${feedhub.notification.recipient-email}") String recipientEmail) {
        this.sesClient = sesClient;
        this.senderEmail = senderEmail;
        this.recipientEmail = recipientEmail;
    }

    @Override
    public void enviar(RelatorioSemanal relatorio) {
        String assunto = "FeedHub - Relatorio semanal de avaliacoes";
        String mensagem = """
                Relatorio semanal de avaliacoes do FeedHub

                Periodo: %s a %s
                Total de avaliacoes: %d
                Nota media: %.2f
                Urgencia ALTA: %d
                Urgencia MEDIA: %d
                Urgencia BAIXA: %d
                """.formatted(
                FORMATTER.format(relatorio.periodoInicio()),
                FORMATTER.format(relatorio.periodoFim()),
                relatorio.totalAvaliacoes(),
                relatorio.mediaNota(),
                relatorio.quantidadeAlta(),
                relatorio.quantidadeMedia(),
                relatorio.quantidadeBaixa()
        );

        try {
            SendEmailRequest request = SendEmailRequest.builder()
                    .source(senderEmail)
                    .destination(Destination.builder().toAddresses(recipientEmail).build())
                    .message(Message.builder()
                            .subject(Content.builder().data(assunto).build())
                            .body(Body.builder().text(Content.builder().data(mensagem).build()).build())
                            .build())
                    .build();

            sesClient.sendEmail(request);
            log.info("Relatorio semanal enviado para {}", recipientEmail);
        } catch (Exception e) {
            log.error("Falha ao enviar relatorio semanal via SES", e);
        }
    }
}
