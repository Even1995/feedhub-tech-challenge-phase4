package com.feedhub.auth.application.dto;

/**
 * Resposta retornada apos login/registro bem-sucedido.
 */
public record AuthResponse(
        String token,
        String tipo,
        long expiraEmSegundos,
        UsuarioResponse usuario
) {

    public static AuthResponse of(String token, long expiraEmSegundos, UsuarioResponse usuario) {
        return new AuthResponse(token, "Bearer", expiraEmSegundos, usuario);
    }
}
