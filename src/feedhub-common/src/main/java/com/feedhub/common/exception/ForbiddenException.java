package com.feedhub.common.exception;

/**
 * Lancada quando o usuario autenticado nao possui o perfil necessario para a operacao.
 * Mapeada para HTTP 403 (Forbidden).
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
