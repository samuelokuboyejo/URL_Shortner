package com.url_shortner.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;

public class UrlUtils {
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_CODE_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static boolean isValid(String url) {
        if (url == null || url.isBlank()) return false;

        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static String generateShortCode() {
        StringBuilder stringBuilder = new StringBuilder(SHORT_CODE_LENGTH);
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            int index = RANDOM.nextInt(ALPHANUMERIC.length());
            stringBuilder.append(ALPHANUMERIC.charAt(index));
        }
        return stringBuilder.toString();
    }
}
