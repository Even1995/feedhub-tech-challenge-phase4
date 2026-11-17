package com.feedhub.feedback.application.usecase;

import com.feedhub.common.security.SecurityUser;
import com.feedhub.feedback.application.dto.AvaliacaoRequest;
import com.feedhub.feedback.domain.entity.Avaliacao;

public interface CreateAvaliacaoUseCase {

    Avaliacao execute(AvaliacaoRequest request, SecurityUser autor);
}
