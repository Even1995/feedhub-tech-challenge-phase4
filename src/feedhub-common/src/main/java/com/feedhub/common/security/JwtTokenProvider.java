package com.feedhub.common.security;

import com.feedhub.common.constants.AppConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Responsavel por gerar e validar tokens JWT com validade de 1 hora, utilizados
 * por todos os servicos do FeedHub para autenticacao stateless.
 */
@Component
public class JwtTokenProvider {

    private final SecretKey signingKey;

    public JwtTokenProvider(@Value("${feedhub.security.jwt-secret:feedhub-default-secret-change-me-in-production-0123456789}") String secret) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email, String nome, String perfil) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + AppConstants.JWT_EXPIRATION_MILLIS);

        return Jwts.builder()
                .subject(email)
                .claim(AppConstants.JWT_CLAIM_NAME, nome)
                .claim(AppConstants.JWT_CLAIM_ROLE, perfil)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(signingKey)
                .compact();
    }

    public long getExpirationSeconds() {
        return AppConstants.JWT_EXPIRATION_MILLIS / 1000;
    }

    /**
     * Valida a assinatura/expiracao do token e extrai os dados do usuario.
     *
     * @throws JwtException se o token for invalido ou tiver expirado.
     */
    public SecurityUser parseToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String email = claims.getSubject();
        String nome = claims.get(AppConstants.JWT_CLAIM_NAME, String.class);
        String perfil = claims.get(AppConstants.JWT_CLAIM_ROLE, String.class);
        return new SecurityUser(email, nome, perfil);
    }
}
