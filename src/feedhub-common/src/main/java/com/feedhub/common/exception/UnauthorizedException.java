package com.feedhub.common.exception;

/**
 * Lancada quando credenciais sao invalidas ou o usuario nao tem permissao para a operacao.
 * Mapeada para HTTP 401 (Unauthorized).
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
