package com.wix.restaurants;

import com.openrest.v1_1.Filter;
import com.openrest.v1_1.Order;
import com.openrest.v1_1.RestaurantFullInfo;
import com.openrest.v1_1.SearchResult;

import java.util.List;

public interface WixRestaurantsClient {
    RestaurantFullInfo retrieveRestaurantInfo(String restaurantId);

    Order submitOrder(Order order);

    Order retrieveOrderAsOwner(String orderId, String ownerToken);

    List<SearchResult> search(Filter filter, int limit);
}
