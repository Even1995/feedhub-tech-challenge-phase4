package com.feedhub.notification.application.usecase;

import com.feedhub.notification.application.dto.CriticalFeedbackEvent;

public interface SendCriticalNotificationUseCase {

    void execute(CriticalFeedbackEvent event);
}
