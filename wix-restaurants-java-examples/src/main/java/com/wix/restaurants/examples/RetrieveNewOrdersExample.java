package com.wix.restaurants.examples;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.openrest.v1_1.Order;
import com.wix.restaurants.DefaultWixRestaurantsClient;
import com.wix.restaurants.WixAppIds;
import com.wix.restaurants.WixRestaurantsClient;
import com.wix.restaurants.authentication.WixRestaurantsAuthenticationClient;
import com.wix.restaurants.exceptions.NoPermissionException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Demonstrates the "Retrieve Orders" flow.
 * 1) Login with username and password to get an access token
 * 2) Retrieve new orders
 * 3) Mark orders as accepted
 *
 * @see <a href="http://www.thetestaurant.com">The Testaurant</a>
 */
public class RetrieveNewOrdersExample {
    private final WixRestaurantsClient wixRestaurants;

    public RetrieveNewOrdersExample(WixRestaurantsClient wixRestaurants) {
        this.wixRestaurants = wixRestaurants;
    }

    public void runExample() {
        final String restaurantId = "8830975305376234"; // "The Testaurant"

        // Placeholder value that will fail authentication. Use a real value in your live setting.
        final String wixInstance = "XXX";

        // 1. Login with username and password to get an access token
        System.out.print("Authenticating...");
        final WixRestaurantsAuthenticationClient authentication = wixRestaurants.getAuthenticationClient();
        final String accessToken = authentication.loginWithWixInstance(WixAppIds.WIX_RESTAURANTS_ORDERS, wixInstance).accessToken;
        System.out.println(" done (accessToken: " + accessToken + ").");

        // 2. Retrieve new orders
        System.out.print("Retrieving new orders...");
        final List<Order> newOrders;
        try {
            newOrders = wixRestaurants.retrieveNewOrders(accessToken, restaurantId);
            System.out.println(" got " + newOrders.size() + " new orders.");
        } catch (NoPermissionException e) {
            System.out.println(" no permissions (did you change the test credentials to real ones?)");
            throw e;
        }

        // 3. Mark orders as accepted
        for (Order newOrder : newOrders) {
            // A common use-case is to submit orders to an external system, e.g. point-of-sale. If that's the case,
            // users are encouraged to report back the external system's order ID (for reference) via the externalIds
            // argument. Otherwise, an empty map should be used.
            final Map<String, String> externalIds = Collections.singletonMap("org.example.pos", "SOME-POS-ORDER-ID");

            wixRestaurants.acceptOrder(accessToken, newOrder.id, externalIds);
        }
    }

    public static void main(String[] args) throws Exception {
        final HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
        final WixRestaurantsClient wixRestaurants = new DefaultWixRestaurantsClient(requestFactory, 10000, 30000, 1);

        new RetrieveNewOrdersExample(wixRestaurants).runExample();
    }
}
