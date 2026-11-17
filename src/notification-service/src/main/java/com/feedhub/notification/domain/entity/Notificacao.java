package com.feedhub.notification.domain.entity;

/**
 * Representa uma notificacao por email a ser enviada.
 */
public record Notificacao(String destinatario, String assunto, String mensagem) {
}
