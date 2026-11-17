package com.feedhub.common.util;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilitarios de data/hora compartilhados entre os servicos.
 */
public final class DateUtil {

    public static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private DateUtil() {
    }

    public static String nowAsIsoString() {
        return ZonedDateTime.now(ZoneOffset.UTC).format(ISO_FORMATTER);
    }
}
