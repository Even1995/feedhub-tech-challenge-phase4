package com.feedhub.feedback.application.usecase;

import com.feedhub.common.security.SecurityUser;
import com.feedhub.feedback.domain.entity.Avaliacao;

public interface GetAvaliacaoUseCase {

    Avaliacao execute(String id, SecurityUser solicitante);
}
