package com.feedhub.auth.infrastructure.config;

import com.feedhub.auth.domain.entity.Perfil;
import com.feedhub.auth.domain.entity.Usuario;
import com.feedhub.auth.domain.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Garante, na inicializacao do servico, que o usuario administrador padrao
 * (admin@feedhub.com) exista na base de usuarios.
 */
@Component
public class AdminSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;
    private final String adminNome;

    public AdminSeeder(UsuarioRepository usuarioRepository,
                        PasswordEncoder passwordEncoder,
                        @Value("${feedhub.admin.email}") String adminEmail,
                        @Value("${feedhub.admin.password}") String adminPassword,
                        @Value("${feedhub.admin.nome}") String adminNome) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.adminNome = adminNome;
    }

    @Override
    public void run(ApplicationArguments args) {
        String email = adminEmail.toLowerCase();
        if (usuarioRepository.existePorEmail(email)) {
            log.info("Usuario administrador padrao ja existe, seeding ignorado.");
            return;
        }

        Usuario admin = Usuario.novo(adminNome, email, passwordEncoder.encode(adminPassword), Perfil.ADMIN);
        usuarioRepository.salvar(admin);
        log.info("Usuario administrador padrao criado: {}", email);
    }
}
