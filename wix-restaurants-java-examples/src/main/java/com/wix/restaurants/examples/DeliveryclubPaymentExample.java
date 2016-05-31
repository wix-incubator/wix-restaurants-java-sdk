package com.wix.restaurants.examples;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.openrest.olo.payments.DeliveryclubPayment;
import com.openrest.v1_1.Item;
import com.openrest.v1_1.Order;
import com.openrest.v1_1.OrderItem;
import com.openrest.v1_1.RestaurantFullInfo;
import com.wix.restaurants.DefaultWixRestaurantsClient;
import com.wix.restaurants.WixRestaurantsClient;
import com.wix.restaurants.authentication.WixRestaurantsAuthenticationClient;
import com.wix.restaurants.builders.ContactBuilder;
import com.wix.restaurants.builders.OrderBuilder;
import com.wix.restaurants.builders.OrderItemBuilder;
import com.wix.restaurants.builders.PickupBuilder;
import com.wix.restaurants.examples.helpers.MenuHelper;
import com.wix.restaurants.helpers.PriceCalculator;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

/**
 * Demonstrates submitting an order received via a 3rd-party portal (delivery-club.ru).
 * The emphasis in this example is on the payment, not on building the order or submitting it.
 *
 * In orders of this kind, the customer pays the portal - not the restaurant. The restaurant and the portal maintain an
 * off-system balance which is settled every once in a while. Orders sent to the restaurant simply say "paid with portal
 * credit" and include some kind of reference number.
 *
 * @see SubmitOrderExample
 * @see <a href="http://www.thetestaurant.com">The Testaurant</a>
 * @see <a href="http://www.delivery-club.ru">Delivery Club</a>
 */
public class DeliveryclubPaymentExample {
    private final WixRestaurantsClient wixRestaurants;

    public DeliveryclubPaymentExample(WixRestaurantsClient wixRestaurants) {
        this.wixRestaurants = wixRestaurants;
    }

    public void runExample() {
        final String restaurantId = "8830975305376234"; // "The Testaurant"
        final String portalId = "5360888428251510"; // delivery-club.ru

        // This example uses arbitrary values that will fail authorization.
        // Real values should be used in a live setting.
        final String testUsername = "example@example.org";
        final String testPassword = "changeme";

        // Some internal reference for the payment, e.g. order ID in delivery-club.ru
        final String reference = "example-reference";

        // 1. Login with username and password to get an access token (required for this payment type)
        System.out.print("Authenticating...");
        final WixRestaurantsAuthenticationClient authentication = wixRestaurants.getAuthenticationClient();
        final String accessToken = authentication.loginWithOpenrest(testUsername, testPassword).accessToken;
        System.out.println(" done (accessToken: " + accessToken + ").");

        // 2. Retrieve Menu
        System.out.print("Retrieving menu...");
        final RestaurantFullInfo full = wixRestaurants.retrieveRestaurantInfo(restaurantId);
        System.out.println(" done (menus: " + full.menu.sections.size() + ", items: " + full.menu.items.size() + ").");

        // 3. Build Order
        final Order order = buildSomeOrder(full, portalId, reference);

        // 4. Submit Order
        System.out.print("Submitting order...");
        final Order submittedOrder = wixRestaurants.submitOrder(accessToken, order);
        System.out.println(" done (order ID: " + submittedOrder.id + ", status: " + submittedOrder.status +
                ", ownerToken: " + submittedOrder.ownerToken + ").");
    }

    private Order buildSomeOrder(RestaurantFullInfo full, String portalId, String reference) {
        final MenuHelper menuHelper = new MenuHelper(full.menu);
        final PriceCalculator calculator = new PriceCalculator();

        // Create OrderItems (in a real scenario, the customer would be making these choices in the UI)
        final Item carpaccio = menuHelper.getItem("7285589409963911");
        final OrderItem carpaccioOrderItem = new OrderItemBuilder(carpaccio)
                .build();

        // Calculate OrderItems total price
        final double orderItemsPrice = calculator.price(carpaccioOrderItem);

        return new OrderBuilder()
                .developer("org.example")
                .source(portalId)
                .restaurant(full.restaurant.id)
                .locale(Locale.US)
                .currency(Currency.getInstance(full.restaurant.currency))
                .contact(new ContactBuilder()
                        .firstName("John")
                        .lastName("Doe")
                        .phone("+12024561111")
                        .email("johndoe@example.org")
                        .build())
                .dispatch(new PickupBuilder()
                        .build())
                .addItem(carpaccioOrderItem)
                .addPayment(new DeliveryclubPaymentBuilder()
                        .amount(orderItemsPrice)
                        .reference(reference)
                        .build())
                .build();
    }

    private static class DeliveryclubPaymentBuilder {
        private final DeliveryclubPayment payment = new DeliveryclubPayment();

        public DeliveryclubPaymentBuilder amount(double amount) {
            payment.amount = BigDecimal.valueOf(amount).movePointRight(2).intValueExact();
            return this;
        }

        public DeliveryclubPaymentBuilder reference(String reference) {
            payment.externalIds.put(DeliveryclubPayment.TYPE, reference);
            return this;
        }

        public DeliveryclubPayment build() {
            return payment;
        }
    }

    public static void main(String[] args) throws Exception {
        final HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
        final WixRestaurantsClient wixRestaurants = new DefaultWixRestaurantsClient(requestFactory, 10000, 30000, 1);

        new DeliveryclubPaymentExample(wixRestaurants).runExample();
    }
}
