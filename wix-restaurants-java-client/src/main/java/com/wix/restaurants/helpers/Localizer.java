package com.wix.restaurants.helpers;

import java.util.Locale;
import java.util.Map;

public class Localizer {
    private final Locale defaultLocale;
    private final Locale locale;

    public Localizer(Locale defaultLocale, Locale locale) {
        this.defaultLocale = defaultLocale;
        this.locale = locale;
    }

    public String localize(Map<Locale, String> multiLocaleString) {
        String localized = multiLocaleString.get(locale);
        if (localized == null) {
            localized = multiLocaleString.get(defaultLocale);
        }
        return ((localized != null) ? localized : "");
    }
}
