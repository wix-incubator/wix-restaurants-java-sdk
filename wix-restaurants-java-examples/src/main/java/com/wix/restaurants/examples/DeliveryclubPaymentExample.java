package com.wix.restaurants.examples;

import com.openrest.v1_1.Item;
import com.openrest.v1_1.Order;
import com.openrest.v1_1.OrderItem;
import com.openrest.v1_1.RestaurantFullInfo;
import com.wix.restaurants.DefaultWixRestaurantsClient;
import com.wix.restaurants.WixRestaurantsClient;
import com.wix.restaurants.authentication.WixRestaurantsAuthenticationClient;
import com.wix.restaurants.builders.ContactBuilder;
import com.wix.restaurants.examples.helpers.MenuHelper;
import com.wix.restaurants.helpers.PriceCalculator;
import com.wix.restaurants.i18n.Locale;
import com.wix.restaurants.orders.builders.OrderBuilder;
import com.wix.restaurants.orders.builders.OrderItemBuilder;
import com.wix.restaurants.orders.builders.PickupBuilder;
import com.wix.restaurants.payments.DeliveryclubPayment;
import scala.concurrent.duration.Duration;

import java.math.BigDecimal;
import java.util.Currency;

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

        // Placeholder value that will fail authentication. Use a real value in your live setting.
        final String wixInstance = "XXX";

        // Some internal reference for the payment, e.g. order ID in delivery-club.ru
        final String reference = "example-reference";

        // 1. Login with Wix Instance to get an access token (required for this payment type)
        System.out.print("Authenticating...");
        final WixRestaurantsAuthenticationClient authentication = wixRestaurants.getAuthenticationClient();
        final String accessToken = authentication.loginWithWixInstance(wixInstance).accessToken;
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
                .setDeveloper("org.example")
                .setSource(portalId)
                .setRestaurant(full.restaurant.id)
                .setLocale(Locale.fromJavaLocale(java.util.Locale.US))
                .setCurrency(Currency.getInstance(full.restaurant.currency))
                .setContact(new ContactBuilder()
                        .setFirstName("John")
                        .setLastName("Doe")
                        .setPhone("+12024561111")
                        .setEmail("johndoe@example.org")
                        .build())
                .setDispatch(new PickupBuilder()
                        .forAsap()
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

    public static void main(String[] args) {
        final WixRestaurantsClient wixRestaurants = new DefaultWixRestaurantsClient(
                "https://api.wixrestaurants.com/v2",
                "https://auth.wixrestaurants.com/v2",
                Duration.Inf());

        new DeliveryclubPaymentExample(wixRestaurants).runExample();
    }
}
