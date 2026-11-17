package com.feedhub.auth.application.usecase;

import com.feedhub.auth.application.dto.RegisterRequest;
import com.feedhub.auth.domain.entity.Usuario;

/**
 * Caso de uso: registrar um novo usuario (sempre criado com perfil ALUNO).
 */
public interface RegisterUserUseCase {

    Usuario execute(RegisterRequest request);
}
