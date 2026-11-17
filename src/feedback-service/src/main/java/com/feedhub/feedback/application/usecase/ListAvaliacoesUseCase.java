package com.feedhub.feedback.application.usecase;

import com.feedhub.common.security.SecurityUser;
import com.feedhub.feedback.domain.entity.Avaliacao;

import java.util.List;

public interface ListAvaliacoesUseCase {

    List<Avaliacao> execute(SecurityUser solicitante);
}
