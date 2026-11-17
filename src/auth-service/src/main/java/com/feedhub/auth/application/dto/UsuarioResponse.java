package com.feedhub.auth.application.dto;

import com.feedhub.auth.domain.entity.Usuario;

/**
 * Representacao publica de um usuario, sem dados sensiveis (sem hash de senha).
 */
public record UsuarioResponse(String id, String nome, String email, String perfil) {

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getPerfil().name());
    }
}
