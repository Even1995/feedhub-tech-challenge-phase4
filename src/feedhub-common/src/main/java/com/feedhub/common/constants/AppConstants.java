package com.feedhub.common.constants;

/**
 * Constantes de dominio compartilhadas entre todos os servicos do FeedHub.
 */
public final class AppConstants {

    private AppConstants() {
    }

    // Perfis de usuario
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_ALUNO = "ALUNO";

    // Administrador padrao criado na inicializacao do auth-service
    public static final String DEFAULT_ADMIN_EMAIL = "admin@feedhub.com";
    public static final String DEFAULT_ADMIN_PASSWORD = "Admin@123";
    public static final String DEFAULT_ADMIN_NAME = "Administrador FeedHub";

    // Tabelas DynamoDB
    public static final String TABLE_USUARIOS = "usuarios";
    public static final String TABLE_AVALIACOES = "avaliacoes";

    // Regra de urgencia da avaliacao (nota 0-10)
    public static final int URGENCIA_ALTA_NOTA_MAX = 2;
    public static final int URGENCIA_MEDIA_NOTA_MAX = 4;

    // Mensageria
    public static final String SNS_TOPIC_ENV_VAR = "SNS_CRITICAL_TOPIC_ARN";
    public static final String EVENTBRIDGE_BUS_ENV_VAR = "EVENTBRIDGE_BUS_NAME";

    // JWT
    public static final long JWT_EXPIRATION_MILLIS = 3_600_000L; // 1 hora
    public static final String JWT_CLAIM_ROLE = "perfil";
    public static final String JWT_CLAIM_NAME = "nome";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
}
