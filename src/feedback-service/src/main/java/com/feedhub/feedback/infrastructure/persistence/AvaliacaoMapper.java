package com.feedhub.feedback.infrastructure.persistence;

import com.feedhub.common.util.UrgenciaUtil;
import com.feedhub.feedback.domain.entity.Avaliacao;

import java.time.Instant;

final class AvaliacaoMapper {

    private AvaliacaoMapper() {
    }

    static AvaliacaoItem toItem(Avaliacao avaliacao) {
        AvaliacaoItem item = new AvaliacaoItem();
        item.setId(avaliacao.getId());
        item.setAlunoEmail(avaliacao.getAlunoEmail());
        item.setNota(avaliacao.getNota());
        item.setComentario(avaliacao.getComentario());
        item.setUrgencia(avaliacao.getUrgencia().name());
        item.setDataCriacao(avaliacao.getDataCriacao().toString());
        return item;
    }

    static Avaliacao toDomain(AvaliacaoItem item) {
        return new Avaliacao(
                item.getId(),
                item.getAlunoEmail(),
                item.getNota(),
                item.getComentario(),
                UrgenciaUtil.Urgencia.valueOf(item.getUrgencia()),
                Instant.parse(item.getDataCriacao())
        );
    }
}
