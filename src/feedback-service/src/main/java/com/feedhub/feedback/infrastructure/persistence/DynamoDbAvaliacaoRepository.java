package com.feedhub.feedback.infrastructure.persistence;

import com.feedhub.common.exception.ResourceNotFoundException;
import com.feedhub.feedback.domain.entity.Avaliacao;
import com.feedhub.feedback.domain.repository.AvaliacaoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.Optional;

@Repository
public class DynamoDbAvaliacaoRepository implements AvaliacaoRepository {

    private final DynamoDbTable<AvaliacaoItem> table;
    private final DynamoDbIndex<AvaliacaoItem> alunoEmailIndex;

    public DynamoDbAvaliacaoRepository(DynamoDbEnhancedClient enhancedClient,
                                        @Value("${feedhub.dynamodb.avaliacoes-table}") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(AvaliacaoItem.class));
        this.alunoEmailIndex = table.index("alunoEmail-index");
    }

    @Override
    public Avaliacao salvar(Avaliacao avaliacao) {
        table.putItem(AvaliacaoMapper.toItem(avaliacao));
        return avaliacao;
    }

    @Override
    public Optional<Avaliacao> buscarPorId(String id) {
        AvaliacaoItem item = table.getItem(Key.builder().partitionValue(id).build());
        return Optional.ofNullable(item).map(AvaliacaoMapper::toDomain);
    }

    @Override
    public List<Avaliacao> listarTodas() {
        return table.scan().items().stream()
                .map(AvaliacaoMapper::toDomain)
                .toList();
    }

    @Override
    public List<Avaliacao> listarPorAluno(String alunoEmail) {
        return alunoEmailIndex.query(QueryConditional.keyEqualTo(Key.builder().partitionValue(alunoEmail).build()))
                .stream()
                .flatMap(page -> page.items().stream())
                .map(AvaliacaoMapper::toDomain)
                .toList();
    }
}
