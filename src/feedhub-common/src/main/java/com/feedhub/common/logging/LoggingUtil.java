package com.feedhub.common.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.UUID;

/**
 * Utilitario de logging estruturado. Centraliza a criacao de correlation-ids e
 * a formatacao de mensagens enviadas ao CloudWatch Logs.
 */
public final class LoggingUtil {

    private static final String CORRELATION_ID_KEY = "correlationId";

    private LoggingUtil() {
    }

    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * Gera (ou reaproveita) um correlation-id e o disponibiliza no MDC para que
     * todas as linhas de log da requisicao atual sejam correlacionaveis no CloudWatch.
     */
    public static String startCorrelation(String existingId) {
        String correlationId = (existingId == null || existingId.isBlank()) ? UUID.randomUUID().toString() : existingId;
        MDC.put(CORRELATION_ID_KEY, correlationId);
        return correlationId;
    }

    public static void clear() {
        MDC.clear();
    }
}
