package com.feedhub.auth.domain.entity;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade de dominio Usuario. Nao possui nenhuma anotacao de persistencia ou de
 * framework web, mantendo a camada de dominio isolada (Clean Architecture).
 */
public class Usuario {

    private final String id;
    private final String nome;
    private final String email;
    private final String senhaHash;
    private final Perfil perfil;
    private final Instant dataCriacao;

    public Usuario(String id, String nome, String email, String senhaHash, Perfil perfil, Instant dataCriacao) {
        this.id = Objects.requireNonNull(id);
        this.nome = Objects.requireNonNull(nome);
        this.email = Objects.requireNonNull(email).toLowerCase();
        this.senhaHash = Objects.requireNonNull(senhaHash);
        this.perfil = Objects.requireNonNull(perfil);
        this.dataCriacao = Objects.requireNonNull(dataCriacao);
    }

    public static Usuario novo(String nome, String email, String senhaHash, Perfil perfil) {
        return new Usuario(UUID.randomUUID().toString(), nome, email, senhaHash, perfil, Instant.now());
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public Instant getDataCriacao() {
        return dataCriacao;
    }
}
