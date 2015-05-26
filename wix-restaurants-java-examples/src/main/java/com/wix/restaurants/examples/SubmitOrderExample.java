package com.wix.restaurants.examples;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.openrest.v1_1.*;
import com.wix.restaurants.DefaultWixRestaurantsClient;
import com.wix.restaurants.WixRestaurantsClient;
import com.wix.restaurants.builders.*;
import com.wix.restaurants.examples.helpers.MenuHelper;
import com.wix.restaurants.helpers.PriceCalculator;

import java.util.Locale;

/**
 * Demonstrates the "Submit Order" flow.
 * 1) Retrieve the test restaurant's menu
 * 2) Create an order with 1 simple item, and 1 item with variations
 * 3) Submit the order
 * 4) Query the submitted order's status
 *
 * @see <a href="http://www.thetestaurant.com">The Testaurant</a>
 */
public class SubmitOrderExample {
    private final WixRestaurantsClient wixRestaurants;

    public SubmitOrderExample(WixRestaurantsClient wixRestaurants) {
        this.wixRestaurants = wixRestaurants;
    }

    public void runExample() {
        final String restaurantId = "8830975305376234"; // "The Testaurant"

        // 1. Retrieve Menu
        System.out.print("Retrieving menu...");
        final RestaurantFullInfo full = wixRestaurants.retrieveRestaurantInfo(restaurantId);
        System.out.println(" done (menus: " + full.menu.sections.size() + ", items: " + full.menu.items.size() + ").");

        // 2. Build Order
        final Order order = buildSomeOrder(full);

        // 3. Submit Order
        System.out.print("Submitting order...");
        final Order submittedOrder = wixRestaurants.submitOrder(order);
        System.out.println(" done (order ID: " + submittedOrder.id + ", status: " + submittedOrder.status +
                ", ownerToken: " + submittedOrder.ownerToken + ").");

        // 4. Query Order status
        System.out.print("Retrieving order...");
        final Order retrievedOrder = wixRestaurants.retrieveOrderAsOwner(submittedOrder.id, submittedOrder.ownerToken);
        System.out.println(" done (status: status: " + retrievedOrder.status + ").");
    }

    private Order buildSomeOrder(RestaurantFullInfo full) {
        final MenuHelper menuHelper = new MenuHelper(full.menu);
        final PriceCalculator calculator = new PriceCalculator();

        // Find Items in Menu
        final Item carpaccio = menuHelper.findFirst("carpaccio");
        final Item coke = menuHelper.findFirst("coke");
        final Variation cokeOption = coke.variations.get(0);
        final Item smallCoke = menuHelper.getItem(cokeOption.itemIds.get(0));

        // Create OrderItems
        final OrderItem carpaccioOrderItem = new OrderItemBuilder(carpaccio)
                .comment("Extra cheese please")
                .build();
        final OrderItem cokeOrderItem = new OrderItemBuilder(coke)
                .addChoice(0, new OrderItemBuilder(smallCoke, cokeOption).build())
                .build();

        // Calculate OrderItems total price
        final double orderItemsPrice = calculator.price(carpaccioOrderItem, cokeOrderItem);

        return new OrderBuilder()
                .developer("org.example")
                .restaurant(full.restaurant.id)
                .locale(Locale.US)
                .contact(new ContactBuilder()
                        .firstName("John")
                        .lastName("Doe")
                        .phone("+12024561111")
                        .email("johndoe@example.org")
                        .build())
                .delivery(new TakeoutBuilder()
                        .build())
                .addItem(carpaccioOrderItem)
                .addItem(cokeOrderItem)
                .comment("I'm allergic to nuts.")
                .addPayment(new CashPaymentBuilder()
                        .amount(orderItemsPrice)
                        .build())
                .build();
    }

    public static void main(String[] args) throws Exception {
        final HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
        final WixRestaurantsClient wixRestaurants = new DefaultWixRestaurantsClient(requestFactory, 10000, 30000);

        new SubmitOrderExample(wixRestaurants).runExample();
    }
}
