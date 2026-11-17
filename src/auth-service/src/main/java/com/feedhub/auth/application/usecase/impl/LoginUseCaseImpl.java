package com.feedhub.auth.application.usecase.impl;

import com.feedhub.auth.application.dto.AuthResponse;
import com.feedhub.auth.application.dto.LoginRequest;
import com.feedhub.auth.application.dto.UsuarioResponse;
import com.feedhub.auth.application.usecase.LoginUseCase;
import com.feedhub.auth.domain.entity.Usuario;
import com.feedhub.auth.domain.repository.UsuarioRepository;
import com.feedhub.common.exception.UnauthorizedException;
import com.feedhub.common.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginUseCaseImpl implements LoginUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginUseCaseImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public AuthResponse execute(LoginRequest request) {
        Usuario usuario = usuarioRepository.buscarPorEmail(request.email().toLowerCase())
                .orElseThrow(() -> new UnauthorizedException("Email ou senha invalidos"));

        if (!passwordEncoder.matches(request.senha(), usuario.getSenhaHash())) {
            throw new UnauthorizedException("Email ou senha invalidos");
        }

        String token = jwtTokenProvider.generateToken(usuario.getEmail(), usuario.getNome(), usuario.getPerfil().name());
        return AuthResponse.of(token, jwtTokenProvider.getExpirationSeconds(), UsuarioResponse.from(usuario));
    }
}
