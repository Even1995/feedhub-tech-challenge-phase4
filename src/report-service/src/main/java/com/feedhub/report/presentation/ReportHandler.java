package com.feedhub.report.presentation;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.feedhub.report.ReportServiceApplication;
import com.feedhub.report.application.usecase.GenerateWeeklyReportUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Ponto de entrada da funcao Lambda, acionada semanalmente por uma regra agendada do EventBridge.
 */
public class ReportHandler implements RequestHandler<ScheduledEvent, Void> {

    private static final Logger log = LoggerFactory.getLogger(ReportHandler.class);

    private static final ConfigurableApplicationContext CONTEXT = new SpringApplicationBuilder(ReportServiceApplication.class)
            .web(WebApplicationType.NONE)
            .run();

    private final GenerateWeeklyReportUseCase useCase = CONTEXT.getBean(GenerateWeeklyReportUseCase.class);

    @Override
    public Void handleRequest(ScheduledEvent event, Context context) {
        log.info("Evento EventBridge recebido, gerando relatorio semanal...");
        useCase.execute();
        return null;
    }
}
