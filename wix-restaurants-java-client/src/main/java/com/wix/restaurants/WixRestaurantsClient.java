package com.wix.restaurants;

import com.openrest.v1_1.*;
import com.wix.restaurants.authentication.WixRestaurantsAuthenticationClient;

import java.util.List;
import java.util.Map;

public interface WixRestaurantsClient {
    WixRestaurantsAuthenticationClient getAuthenticationClient();

    RestaurantFullInfo retrieveRestaurantInfo(String restaurantId);

    Order submitOrder(String accessToken, Order order);

    Order retrieveOrderAsOwner(String orderId, String ownerToken);
    Order retrieveOrderAsRestaurant(String accessToken, String orderId);

    List<SearchResult> search(Filter filter, int limit);

    List<Order> retrieveNewOrders(String accessToken, String restaurantId);

    Order acceptOrder(String accessToken, String orderId, Map<String, String> externalIds);
    Order rejectOrder(String accessToken, String orderId, String comment);
}
