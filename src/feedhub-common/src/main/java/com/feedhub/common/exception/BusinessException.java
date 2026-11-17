package com.feedhub.common.exception;

/**
 * Excecao base para regras de negocio violadas. Mapeada para HTTP 400 (Bad Request).
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
