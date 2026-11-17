package com.feedhub.notification.application.usecase.impl;

import com.feedhub.notification.application.dto.CriticalFeedbackEvent;
import com.feedhub.notification.application.port.EmailSender;
import com.feedhub.notification.application.usecase.SendCriticalNotificationUseCase;
import com.feedhub.notification.domain.entity.Notificacao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SendCriticalNotificationUseCaseImpl implements SendCriticalNotificationUseCase {

    private final EmailSender emailSender;
    private final String recipientEmail;

    public SendCriticalNotificationUseCaseImpl(EmailSender emailSender,
                                                @Value("${feedhub.notification.recipient-email}") String recipientEmail) {
        this.emailSender = emailSender;
        this.recipientEmail = recipientEmail;
    }

    @Override
    public void execute(CriticalFeedbackEvent event) {
        String assunto = "FeedHub - Avaliacao critica recebida (urgencia ALTA)";
        String mensagem = """
                Uma nova avaliacao com urgencia ALTA foi registrada no FeedHub.

                ID da avaliacao: %s
                Aluno: %s
                Nota: %d
                Comentario: %s

                Favor verificar o caso com prioridade.
                """.formatted(event.avaliacaoId(), event.alunoEmail(), event.nota(), event.comentario());

        emailSender.enviar(new Notificacao(recipientEmail, assunto, mensagem));
    }
}
