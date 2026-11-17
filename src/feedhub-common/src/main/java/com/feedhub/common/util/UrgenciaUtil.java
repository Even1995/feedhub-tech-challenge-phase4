package com.feedhub.common.util;

/**
 * Regra de urgencia aplicada sobre a nota (0 a 10) de uma avaliacao.
 */
public final class UrgenciaUtil {

    private UrgenciaUtil() {
    }

    public static final int ALTA_NOTA_MAX = 2;
    public static final int MEDIA_NOTA_MAX = 4;

    public enum Urgencia {
        ALTA,
        MEDIA,
        BAIXA
    }

    /**
     * 0 a 2 = ALTA, 3 a 4 = MEDIA, 5 a 10 = BAIXA.
     */
    public static Urgencia calcular(int nota) {
        if (nota < 0 || nota > 10) {
            throw new IllegalArgumentException("Nota deve estar entre 0 e 10");
        }
        if (nota <= ALTA_NOTA_MAX) {
            return Urgencia.ALTA;
        }
        if (nota <= MEDIA_NOTA_MAX) {
            return Urgencia.MEDIA;
        }
        return Urgencia.BAIXA;
    }
}
