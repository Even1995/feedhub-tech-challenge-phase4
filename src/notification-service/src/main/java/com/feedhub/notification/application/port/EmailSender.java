package com.feedhub.notification.application.port;

import com.feedhub.notification.domain.entity.Notificacao;

/**
 * Porta de saida para envio de emails. Implementacao concreta (SES) na camada de infraestrutura.
 */
public interface EmailSender {

    void enviar(Notificacao notificacao);
}
