package com.feedhub.report.infrastructure.persistence;

import com.feedhub.common.util.UrgenciaUtil;
import com.feedhub.report.domain.entity.AvaliacaoResumo;
import com.feedhub.report.domain.repository.AvaliacaoReadRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.time.Instant;
import java.util.List;

/**
 * Le a tabela "avaliacoes" e filtra as criadas dentro do periodo solicitado.
 * Para o volume academico do projeto um scan completo e suficiente; em producao
 * recomenda-se um GSI particionado por semana para evitar scans completos.
 */
@Repository
public class DynamoDbAvaliacaoReadRepository implements AvaliacaoReadRepository {

    private final DynamoDbTable<AvaliacaoReadItem> table;

    public DynamoDbAvaliacaoReadRepository(DynamoDbEnhancedClient enhancedClient,
                                            @Value("${feedhub.dynamodb.avaliacoes-table}") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(AvaliacaoReadItem.class));
    }

    @Override
    public List<AvaliacaoResumo> listarCriadasEntre(Instant inicio, Instant fim) {
        return table.scan().items().stream()
                .map(this::toResumo)
                .filter(resumo -> !resumo.dataCriacao().isBefore(inicio) && !resumo.dataCriacao().isAfter(fim))
                .toList();
    }

    private AvaliacaoResumo toResumo(AvaliacaoReadItem item) {
        return new AvaliacaoResumo(
                item.getId(),
                item.getNota(),
                UrgenciaUtil.Urgencia.valueOf(item.getUrgencia()),
                Instant.parse(item.getDataCriacao())
        );
    }
}
