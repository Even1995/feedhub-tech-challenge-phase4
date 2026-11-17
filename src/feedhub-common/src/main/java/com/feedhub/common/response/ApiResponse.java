package com.feedhub.common.response;

import java.time.Instant;

/**
 * Envelope padrao de resposta de sucesso utilizado pelas APIs do FeedHub.
 */
public record ApiResponse<T>(
        boolean success,
        T data,
        String message,
        Instant timestamp
) {

    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>(true, data, null, Instant.now());
    }

    public static <T> ApiResponse<T> of(T data, String message) {
        return new ApiResponse<>(true, data, message, Instant.now());
    }
}
