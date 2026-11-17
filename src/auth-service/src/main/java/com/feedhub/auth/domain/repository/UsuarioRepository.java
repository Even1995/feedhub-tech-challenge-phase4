package com.feedhub.auth.domain.repository;

import com.feedhub.auth.domain.entity.Usuario;

import java.util.Optional;

/**
 * Porta (interface) de persistencia de usuarios. A implementacao concreta
 * (DynamoDB) vive na camada de infraestrutura, respeitando a inversao de dependencia.
 */
public interface UsuarioRepository {

    Usuario salvar(Usuario usuario);

    Optional<Usuario> buscarPorEmail(String email);

    boolean existePorEmail(String email);
}
