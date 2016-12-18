package com.wix.restaurants.helpers;

import com.wix.restaurants.i18n.Locale;
import com.wix.restaurants.i18n.LocalizedString;

public class Localizer {
    private final Locale defaultLocale;
    private final Locale locale;

    public Localizer(Locale defaultLocale, Locale locale) {
        this.defaultLocale = defaultLocale;
        this.locale = locale;
    }

    public String localize(LocalizedString localizedString) {
        String localized = localizedString.get(locale);
        if (localized == null) {
            localized = localizedString.get(defaultLocale);
        }
        return ((localized != null) ? localized : "");
    }
}
