package com.feedhub.auth.infrastructure.persistence;

import com.feedhub.auth.domain.entity.Perfil;
import com.feedhub.auth.domain.entity.Usuario;

import java.time.Instant;

/**
 * Conversor entre a entidade de dominio Usuario e o item de persistencia DynamoDB.
 */
final class UsuarioMapper {

    private UsuarioMapper() {
    }

    static UsuarioItem toItem(Usuario usuario) {
        UsuarioItem item = new UsuarioItem();
        item.setId(usuario.getId());
        item.setEmail(usuario.getEmail());
        item.setNome(usuario.getNome());
        item.setSenhaHash(usuario.getSenhaHash());
        item.setPerfil(usuario.getPerfil().name());
        item.setDataCriacao(usuario.getDataCriacao().toString());
        return item;
    }

    static Usuario toDomain(UsuarioItem item) {
        return new Usuario(
                item.getId(),
                item.getNome(),
                item.getEmail(),
                item.getSenhaHash(),
                Perfil.valueOf(item.getPerfil()),
                Instant.parse(item.getDataCriacao())
        );
    }
}
