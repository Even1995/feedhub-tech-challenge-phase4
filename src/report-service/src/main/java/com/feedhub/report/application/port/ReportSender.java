package com.feedhub.report.application.port;

import com.feedhub.report.domain.entity.RelatorioSemanal;

/**
 * Porta de saida para envio do relatorio semanal por email.
 */
public interface ReportSender {

    void enviar(RelatorioSemanal relatorio);
}
