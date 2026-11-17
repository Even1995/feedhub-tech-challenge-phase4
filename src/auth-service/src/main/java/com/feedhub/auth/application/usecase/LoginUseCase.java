package com.feedhub.auth.application.usecase;

import com.feedhub.auth.application.dto.AuthResponse;
import com.feedhub.auth.application.dto.LoginRequest;

/**
 * Caso de uso: autenticar um usuario e emitir um token JWT valido por 1 hora.
 */
public interface LoginUseCase {

    AuthResponse execute(LoginRequest request);
}
