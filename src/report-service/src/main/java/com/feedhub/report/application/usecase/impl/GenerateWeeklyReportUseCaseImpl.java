package com.feedhub.report.application.usecase.impl;

import com.feedhub.common.util.UrgenciaUtil;
import com.feedhub.report.application.port.ReportSender;
import com.feedhub.report.application.usecase.GenerateWeeklyReportUseCase;
import com.feedhub.report.domain.entity.AvaliacaoResumo;
import com.feedhub.report.domain.entity.RelatorioSemanal;
import com.feedhub.report.domain.repository.AvaliacaoReadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class GenerateWeeklyReportUseCaseImpl implements GenerateWeeklyReportUseCase {

    private static final Logger log = LoggerFactory.getLogger(GenerateWeeklyReportUseCaseImpl.class);

    private final AvaliacaoReadRepository avaliacaoReadRepository;
    private final ReportSender reportSender;

    public GenerateWeeklyReportUseCaseImpl(AvaliacaoReadRepository avaliacaoReadRepository, ReportSender reportSender) {
        this.avaliacaoReadRepository = avaliacaoReadRepository;
        this.reportSender = reportSender;
    }

    @Override
    public RelatorioSemanal execute() {
        Instant fim = Instant.now();
        Instant inicio = fim.minus(7, ChronoUnit.DAYS);

        List<AvaliacaoResumo> avaliacoes = avaliacaoReadRepository.listarCriadasEntre(inicio, fim);

        long total = avaliacoes.size();
        double media = avaliacoes.stream().mapToInt(AvaliacaoResumo::nota).average().orElse(0.0);
        long alta = contarPorUrgencia(avaliacoes, UrgenciaUtil.Urgencia.ALTA);
        long mediaUrg = contarPorUrgencia(avaliacoes, UrgenciaUtil.Urgencia.MEDIA);
        long baixa = contarPorUrgencia(avaliacoes, UrgenciaUtil.Urgencia.BAIXA);

        RelatorioSemanal relatorio = new RelatorioSemanal(inicio, fim, total, media, alta, mediaUrg, baixa);

        log.info("Relatorio semanal gerado: total={}, mediaNota={}, alta={}, media={}, baixa={}",
                total, media, alta, mediaUrg, baixa);

        reportSender.enviar(relatorio);
        return relatorio;
    }

    private long contarPorUrgencia(List<AvaliacaoResumo> avaliacoes, UrgenciaUtil.Urgencia urgencia) {
        return avaliacoes.stream().filter(a -> a.urgencia() == urgencia).count();
    }
}
