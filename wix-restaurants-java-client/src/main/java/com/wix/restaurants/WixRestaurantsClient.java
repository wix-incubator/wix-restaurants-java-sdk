package com.wix.restaurants;

import com.openrest.v1_1.*;
import com.wix.restaurants.authentication.WixRestaurantsAuthenticationClient;
import com.wix.restaurants.authorization.Role;
import com.wix.restaurants.i18n.Locale;
import com.wix.restaurants.reservations.Reservation;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface WixRestaurantsClient {
    WixRestaurantsAuthenticationClient getAuthenticationClient();

    // Business info
    RestaurantFullInfo retrieveRestaurantInfo(String restaurantId);
    Organization setOrganization(String accessToken, Organization organization);
    List<SearchResult> search(Filter filter, int limit);
    void changeOrganizationLocale(String accessToken, String organizationId, Locale locale);

    // Authorization
    Role getRole(String accessToken, String organizationId);

    // Orders
    Order submitOrder(String accessToken, Order order);
    Order retrieveOrderAsOwner(String orderId, String ownerToken);
    Order retrieveOrderAsRestaurant(String accessToken, String orderId);
    List<Order> retrieveNewOrders(String accessToken, String restaurantId);

    /**
     * Retrieves a batch of orders associated with the given customer phone number.
     *
     * Orders are returned in ascending order, by modification date. Paging can be done by setting a limit (say, 100),
     * starting with a null modifiedSince, and iteratively setting modifiedSince to last returned order's modification
     * date + epsilon, as long as the number of results equals the limit.
     *
     * @param accessToken    Access token with permissions to the restaurant.
     * @param restaurantId   The restaurant's identifier.
     * @param phone          Customer phone number in standard E.164 format.
     * @param modifiedSince  Minimum modification date to return, or null for oldest.
     * @param limit          Maximum number of orders to return, or null for no limit.
     * @return a list of orders.
     */
    List<Order> retrieveOrdersByPhone(String accessToken, String restaurantId, String phone, Date modifiedSince, Integer limit);

    /**
     * Retrieves a batch of orders associated with the given customer email.
     *
     * Orders are returned in ascending order, by modification date. Paging can be done by setting a limit (say, 100),
     * starting with a null modifiedSince, and iteratively setting modifiedSince to last returned order's modification
     * date + epsilon, as long as the number of results equals the limit.
     *
     * @param accessToken    Access token with permissions to the restaurant.
     * @param restaurantId   The restaurant's identifier.
     * @param email          Customer email.
     * @param modifiedSince  Minimum modification date to return, or null for oldest.
     * @param limit          Maximum number of orders to return, or null for no limit.
     * @return a list of orders.
     */
    List<Order> retrieveOrdersByEmail(String accessToken, String restaurantId, String email, Date modifiedSince, Integer limit);

    Order acceptOrder(String accessToken, String orderId, Map<String, String> externalIds);
    Order rejectOrder(String accessToken, String orderId, String comment);

    // Reservations
    Reservation submitReservation(String accessToken, Reservation reservation);
    Reservation retrieveReservationAsOwner(String ownerToken, String reservationId);
    Reservation retrieveReservationAsRestaurant(String accessToken, String reservationId);
    List<Reservation> retrieveUnhandledReservations(String accessToken, String restaurantId);
    Reservation setReservationStatusAsRestaurant(String accessToken, String reservationId, String status, String comment);
    Reservation setReservationStatusAsOwner(String ownerToken, String reservationId, String status, String comment);

    // Wix integration
    void mapInstance(String accessToken, String instanceId, String organizationId);
    Organization retrieveOrganizationForInstance(String instanceId);
    Organization retrieveOrganizationForMetasite(String metasiteId);

    // GDPR
    void deleteOrganization(String accessToken, String organizationId);
}
