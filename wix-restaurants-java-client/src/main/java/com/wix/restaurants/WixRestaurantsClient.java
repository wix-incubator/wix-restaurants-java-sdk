package com.wix.restaurants;

import com.openrest.v1_1.Filter;
import com.openrest.v1_1.Order;
import com.openrest.v1_1.RestaurantFullInfo;
import com.openrest.v1_1.SearchResult;
import com.wix.restaurants.authentication.WixRestaurantsAuthenticationClient;
import com.wix.restaurants.authorization.Role;
import com.wix.restaurants.reservations.Reservation;

import java.util.List;
import java.util.Map;

public interface WixRestaurantsClient {
    WixRestaurantsAuthenticationClient getAuthenticationClient();

    // Business info
    RestaurantFullInfo retrieveRestaurantInfo(String restaurantId);
    List<SearchResult> search(Filter filter, int limit);

    // Authorization
    Role getRole(String accessToken, String organizationId);

    // Orders
    Order submitOrder(String accessToken, Order order);
    Order retrieveOrderAsOwner(String orderId, String ownerToken);
    Order retrieveOrderAsRestaurant(String accessToken, String orderId);
    List<Order> retrieveNewOrders(String accessToken, String restaurantId);
    Order acceptOrder(String accessToken, String orderId, Map<String, String> externalIds);
    Order rejectOrder(String accessToken, String orderId, String comment);

    // Reservations
    Reservation submitReservation(String accessToken, Reservation reservation);
    Reservation retrieveReservationAsOwner(String ownerToken, String reservationId);
    Reservation retrieveReservationAsRestaurant(String accessToken, String reservationId);
    List<Reservation> retrieveUnhandledReservations(String accessToken, String restaurantId);
    Reservation setReservationStatusAsRestaurant(String accessToken, String reservationId, String status, String comment);
    Reservation setReservationStatusAsOwner(String ownerToken, String reservationId, String status, String comment);
}
