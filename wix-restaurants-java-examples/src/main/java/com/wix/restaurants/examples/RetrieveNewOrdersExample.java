package com.wix.restaurants.examples;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.openrest.v1_1.Order;
import com.wix.restaurants.DefaultWixRestaurantsClient;
import com.wix.restaurants.WixRestaurantsClient;
import com.wix.restaurants.authentication.WixRestaurantsAuthenticationClient;
import com.wix.restaurants.exceptions.NoPermissionException;

import java.util.List;

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

        // This example uses arbitrary values that will fail authorization.
        // Real values should be used in a live setting.
        final String testUsername = "example@example.org";
        final String testPassword = "changeme";

        // 1. Login with username and password to get an access token
        System.out.print("Authenticating...");
        final WixRestaurantsAuthenticationClient authentication = wixRestaurants.getAuthenticationClient();
        final String accessToken = authentication.loginWithOpenrest(testUsername, testPassword).accessToken;
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
            wixRestaurants.acceptOrder(accessToken, newOrder.id);
        }
    }

    public static void main(String[] args) throws Exception {
        final HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
        final WixRestaurantsClient wixRestaurants = new DefaultWixRestaurantsClient(requestFactory, 10000, 30000, 1);

        new RetrieveNewOrdersExample(wixRestaurants).runExample();
    }
}
