package com.feedhub.feedback.domain.entity;

import com.feedhub.common.util.UrgenciaUtil;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade de dominio Avaliacao (feedback de aluno). A urgencia e sempre derivada
 * da nota no momento da criacao, conforme a regra de negocio do FeedHub.
 */
public class Avaliacao {

    private final String id;
    private final String alunoEmail;
    private final int nota;
    private final String comentario;
    private final UrgenciaUtil.Urgencia urgencia;
    private final Instant dataCriacao;

    public Avaliacao(String id, String alunoEmail, int nota, String comentario,
                      UrgenciaUtil.Urgencia urgencia, Instant dataCriacao) {
        this.id = Objects.requireNonNull(id);
        this.alunoEmail = Objects.requireNonNull(alunoEmail);
        this.nota = nota;
        this.comentario = comentario;
        this.urgencia = Objects.requireNonNull(urgencia);
        this.dataCriacao = Objects.requireNonNull(dataCriacao);
    }

    public static Avaliacao novo(String alunoEmail, int nota, String comentario) {
        return new Avaliacao(UUID.randomUUID().toString(), alunoEmail, nota, comentario,
                UrgenciaUtil.calcular(nota), Instant.now());
    }

    public String getId() {
        return id;
    }

    public String getAlunoEmail() {
        return alunoEmail;
    }

    public int getNota() {
        return nota;
    }

    public String getComentario() {
        return comentario;
    }

    public UrgenciaUtil.Urgencia getUrgencia() {
        return urgencia;
    }

    public Instant getDataCriacao() {
        return dataCriacao;
    }

    public boolean isUrgenciaAlta() {
        return urgencia == UrgenciaUtil.Urgencia.ALTA;
    }
}
