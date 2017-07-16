package com.wix.restaurants.examples;

import com.openrest.v1_1.LatLng;
import com.openrest.v1_1.SearchResult;
import com.wix.restaurants.DefaultWixRestaurantsClient;
import com.wix.restaurants.WixRestaurantsClient;
import com.wix.restaurants.availability.AvailabilityIterator;
import com.wix.restaurants.availability.Status;
import com.wix.restaurants.builders.FilterBuilder;
import com.wix.restaurants.helpers.Localizer;
import com.wix.restaurants.i18n.Locale;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Demonstrates the "Search" flow.
 * 1) Retrieve all restaurants that are relevant for some address
 * 2) Print each restaurant's general information and current availability
 */
public class SearchExample {
    private final WixRestaurantsClient wixRestaurants;

    public SearchExample(WixRestaurantsClient wixRestaurants) {
        this.wixRestaurants = wixRestaurants;
    }

    public void runExample() {
        final LatLng latLng = new LatLng(36.600106, -121.894286); // "375 Alvarado St, Monterey, CA 93940, USA"

        // 1. Retrieve all restaurants that deliver to address, or offer takeout and are located within 2000m of address.
        //    Note: retrieves ALL restaurants, those that are available, and those that are currently closed.
        System.out.print("Searching for restaurants...");
        final List<SearchResult> results = wixRestaurants.search(
                new FilterBuilder()
                        .latLng(latLng)
                        .radius(2000)
                        .build(),
                100);
        System.out.println(" got " + results.size () + " results.");

        // 2. Print each restaurant's title, address, phone, and current availability
        final Calendar now = Calendar.getInstance();
        for (SearchResult result : results) {
            final Localizer l = new Localizer(result.locale, Locale.fromJavaLocale(java.util.Locale.US));
            final TimeZone tz = TimeZone.getTimeZone(result.timezone);
            now.setTimeZone(tz);

            System.out.println();
            System.out.println(l.localize(result.title));
            System.out.println("• " + result.address.formatted + " (" + result.address.latLng.lat + "," + result.address.latLng.lng + ")");
            System.out.println("• " + result.contact.phone);

            final Status status = new AvailabilityIterator(now, result.openTimes).next();
            System.out.println("• " + status.status + " until " + untilToString(status.until, tz));
        }
    }

    private String untilToString(Long until, TimeZone tz) {
        if (until == null) {
            return "forever";
        }

        final Calendar cal = Calendar.getInstance(tz);
        cal.setTimeInMillis(until);
        return cal.getTime().toString();
    }

    public static void main(String[] args) throws Exception {
        final WixRestaurantsClient wixRestaurants = new DefaultWixRestaurantsClient.Builder().build();

        new SearchExample(wixRestaurants).runExample();
    }
}
