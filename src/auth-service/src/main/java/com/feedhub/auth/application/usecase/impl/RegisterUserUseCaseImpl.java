package com.feedhub.auth.application.usecase.impl;

import com.feedhub.auth.application.dto.RegisterRequest;
import com.feedhub.auth.domain.entity.Perfil;
import com.feedhub.auth.domain.entity.Usuario;
import com.feedhub.auth.domain.repository.UsuarioRepository;
import com.feedhub.auth.application.usecase.RegisterUserUseCase;
import com.feedhub.common.exception.BusinessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserUseCaseImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario execute(RegisterRequest request) {
        String email = request.email().toLowerCase();
        if (usuarioRepository.existePorEmail(email)) {
            throw new BusinessException("Ja existe um usuario cadastrado com este email");
        }

        Usuario usuario = Usuario.novo(request.nome(), email, passwordEncoder.encode(request.senha()), Perfil.ALUNO);
        return usuarioRepository.salvar(usuario);
    }
}
