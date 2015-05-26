package com.wix.restaurants.helpers;

import java.util.Locale;
import java.util.Map;

public class Localizer {
    private final String defaultLocale;
    private final String locale;

    public Localizer(Locale defaultLocale, Locale locale) {
        this(defaultLocale.toString(), locale.toString());
    }

    public Localizer(String defaultLocale, String locale) {
        this.defaultLocale = defaultLocale;
        this.locale = locale;
    }

    public String localize(Map<String, String> multiLocaleString) {
        String localized = multiLocaleString.get(locale);
        if (localized == null) {
            localized = multiLocaleString.get(defaultLocale);
        }
        return ((localized != null) ? localized : "");
    }
}
