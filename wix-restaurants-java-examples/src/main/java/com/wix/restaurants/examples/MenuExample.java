package com.wix.restaurants.examples;

import com.openrest.v1_1.*;
import com.wix.restaurants.DefaultWixRestaurantsClient;
import com.wix.restaurants.WixRestaurantsClient;
import com.wix.restaurants.examples.helpers.MenuHelper;
import com.wix.restaurants.helpers.Localizer;
import com.wix.restaurants.i18n.Locale;
import scala.concurrent.duration.Duration;

import java.math.BigDecimal;

/**
 * Demonstrates the "Show Menu" flow.
 * 1) Retrieve the test restaurant's menu
 * 2) Pretty-print the menu hierarchy
 *
 * @see <a href="http://www.thetestaurant.com">The Testaurant</a>
 */
public class MenuExample {
    private final WixRestaurantsClient wixRestaurants;

    public MenuExample(WixRestaurantsClient wixRestaurants) {
        this.wixRestaurants = wixRestaurants;
    }

    public void runExample() {
        final String restaurantId = "8830975305376234"; // "The Testaurant"

        // 1. Retrieve Menu
        System.out.print("Retrieving menu...");
        final RestaurantFullInfo full = wixRestaurants.retrieveRestaurantInfo(restaurantId);
        System.out.println(" done (menus: " + full.menu.sections.size() +
                ", items: " + full.menu.items.size() +
                ", currency: " + full.restaurant.currency + ").");

        // 2. Pretty-print the menu
        final Localizer l = new Localizer(full.restaurant.locale, Locale.fromJavaLocale(java.util.Locale.US));
        final MenuHelper menuHelper = new MenuHelper(full.menu);

        // Menus
        for (MenuSection menu : full.menu.sections) {
            // Menu title
            System.out.println();
            System.out.println(l.localize(menu.title));

            // Sections
            for (MenuSection section : menu.children) {
                System.out.println("\t" + l.localize(section.title));

                // Items
                for (String itemId : section.itemIds) {
                    // Item title
                    final Item item = menuHelper.getItem(itemId);
                    System.out.print("\t\t" + l.localize(item.title));

                    // Item price
                    final BigDecimal basePrice = BigDecimal.valueOf(item.price).movePointLeft(2);
                    if (!isZero(basePrice)) {
                        System.out.print(" [" + basePrice + "]");
                    }

                    // Item image, resized to 100 pixels. For additional transformations,
                    // @see <a href="https://cloud.google.com/appengine/docs/java/images/">Images Java API Overview</a>
                    final String imageUrl = item.media.get(BlobTypes.BLOB_TYPE_LOGO);
                    if (imageUrl != null) {
                        System.out.print(" - " + resize(imageUrl, 100));
                    }

                    System.out.println();

                    // Options
                    for (Variation variation : item.variations) {
                        // Option title and limitations
                        System.out.println("\t\t\t" + l.localize(variation.title) +
                                " [min: " + variation.minNumAllowed + ", max: " + variation.maxNumAllowed + "]");

                        // Choices
                        for (String choiceId : variation.itemIds) {
                            // Choice title
                            final Item choice = menuHelper.getItem(choiceId);
                            System.out.print("\t\t\t\t" + l.localize(choice.title));

                            // Choice price
                            final Integer extraPriceInt = variation.prices.get(choiceId);
                            final BigDecimal extraPrice =
                                    BigDecimal.valueOf((extraPriceInt != null) ? extraPriceInt : 0).movePointLeft(2);
                            if (!isZero(extraPrice)) {
                                System.out.print(" [+" + extraPrice + "]");
                            }

                            System.out.println();
                        }
                    }
                }
            }
        }
    }

    private boolean isZero(BigDecimal num) {
        return (BigDecimal.ZERO.compareTo(num) == 0);
    }

    private String resize(String imageUrl, int size) {
        return String.format("%s=s%d", imageUrl, size);
    }

    public static void main(String[] args) {
        final WixRestaurantsClient wixRestaurants = new DefaultWixRestaurantsClient(
                "https://api.wixrestaurants.com/v2",
                "https://auth.wixrestaurants.com/v2",
                Duration.Inf());

        new MenuExample(wixRestaurants).runExample();
    }
}
