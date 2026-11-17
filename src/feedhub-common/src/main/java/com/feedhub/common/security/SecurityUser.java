package com.feedhub.common.security;

/**
 * Representa o usuario autenticado extraido do token JWT, independente de camada de persistencia.
 */
public record SecurityUser(String email, String nome, String perfil) {

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(perfil);
    }
}
