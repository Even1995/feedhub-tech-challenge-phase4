package com.feedhub.auth.infrastructure.persistence;

import com.feedhub.auth.domain.entity.Usuario;
import com.feedhub.auth.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.Optional;

/**
 * Implementacao da porta UsuarioRepository utilizando o DynamoDB Enhanced Client.
 * A busca por email usa um GSI (email-index) para evitar scans completos na tabela.
 */
@Repository
public class DynamoDbUsuarioRepository implements UsuarioRepository {

    private final DynamoDbTable<UsuarioItem> table;
    private final DynamoDbIndex<UsuarioItem> emailIndex;

    public DynamoDbUsuarioRepository(DynamoDbEnhancedClient enhancedClient,
                                      @Value("${feedhub.dynamodb.usuarios-table}") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(UsuarioItem.class));
        this.emailIndex = table.index("email-index");
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        table.putItem(UsuarioMapper.toItem(usuario));
        return usuario;
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return emailIndex.query(QueryConditional.keyEqualTo(Key.builder().partitionValue(email).build()))
                .stream()
                .flatMap(page -> page.items().stream())
                .findFirst()
                .map(UsuarioMapper::toDomain);
    }

    @Override
    public boolean existePorEmail(String email) {
        return buscarPorEmail(email).isPresent();
    }
}
