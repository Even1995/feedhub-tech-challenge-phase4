package com.feedhub.common.exception;

/**
 * Lancada quando um recurso solicitado nao e encontrado. Mapeada para HTTP 404 (Not Found).
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
