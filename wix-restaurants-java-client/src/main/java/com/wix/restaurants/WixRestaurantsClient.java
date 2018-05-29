package com.wix.restaurants;

import com.openrest.v1_1.*;
import com.wix.restaurants.authentication.WixRestaurantsAuthenticationClient;
import com.wix.restaurants.authorization.AuthorizationClient;
import com.wix.restaurants.i18n.Locale;
import com.wix.restaurants.reservations.Reservation;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface WixRestaurantsClient {
    WixRestaurantsAuthenticationClient getAuthenticationClient();
    AuthorizationClient getAuthorizationClient();

    // Business info
    RestaurantFullInfo retrieveRestaurantInfo(String restaurantId);
    Organization retrieveOrganization(String organizationId);
    Organization setOrganization(String accessToken, Organization organization);
    List<SearchResult> search(Filter filter, int limit);
    void changeOrganizationLocale(String accessToken, String organizationId, Locale locale);

    // Organization properties
    Notifications getNotifications(String accessToken, String organizationId);
    Notifications setNotifications(String accessToken, String organizationId, Notifications notifications);
    Secrets getSecrets(String accessToken, String organizationId);
    Secrets setSecrets(String accessToken, String organizationId, Secrets secrets);

    // Orders
    Order submitOrder(String accessToken, Order order);
    Order retrieveOrderAsOwner(String accessToken, String restaurantId, String orderId);
    Order retrieveOrderAsRestaurant(String accessToken, String restaurantId, String orderId);
    List<Order> retrieveNewOrders(String accessToken, String restaurantId);

    /**
     * Retrieves a batch of orders associated with the given customer phone number.
     *
     * Orders are returned in ascending order, by modification date. Paging can be done by setting a limit (say, 100),
     * starting with a null modifiedSince, and iteratively setting modifiedSince to last returned order's modification
     * date + epsilon, as long as the number of results equals the limit.
     *
     * @param accessToken      Access token with permissions to the restaurant.
     * @param organizationId   The organization's identifier.
     * @param phone            Customer phone number in standard E.164 format.
     * @param modifiedSince    Minimum modification date to return, or null for oldest.
     * @param limit            Maximum number of orders to return, or null for no limit.
     * @return a list of orders.
     */
    List<Order> retrieveOrdersByPhone(String accessToken, String organizationId, String phone, Date modifiedSince, Integer limit);

    /**
     * Retrieves a batch of orders associated with the given customer email.
     *
     * Orders are returned in ascending order, by modification date. Paging can be done by setting a limit (say, 100),
     * starting with a null modifiedSince, and iteratively setting modifiedSince to last returned order's modification
     * date + epsilon, as long as the number of results equals the limit.
     *
     * @param accessToken      Access token with permissions to the restaurant.
     * @param organizationId   The organization's identifier.
     * @param email            Customer email.
     * @param modifiedSince    Minimum modification date to return, or null for oldest.
     * @param limit            Maximum number of orders to return, or null for no limit.
     * @return a list of orders.
     */
    List<Order> retrieveOrdersByEmail(String accessToken, String organizationId, String email, Date modifiedSince, Integer limit);

    Order acceptOrder(String accessToken, String restaurantId, String orderId, Map<String, String> externalIds);
    Order rejectOrder(String accessToken, String restaurantId, String orderId, String comment);

    // Reservations
    Reservation submitReservation(String accessToken, Reservation reservation);
    Reservation retrieveReservationAsOwner(String accessToken, String restaurantId, String reservationId);
    Reservation retrieveReservationAsRestaurant(String accessToken, String restaurantId, String reservationId);
    List<Reservation> retrieveUnhandledReservations(String accessToken, String restaurantId);
    Reservation setReservationStatusAsRestaurant(String accessToken, String restaurantId, String reservationId, String status, String comment);
    Reservation setReservationStatusAsOwner(String ownerToken, String restaurantId, String reservationId, String status, String comment);

    /**
     * Retrieves a batch of reservations associated with the given customer phone number.
     *
     * Reservations are returned in ascending order, by modification date. Paging can be done by setting a limit (say, 100),
     * starting with a null modifiedSince, and iteratively setting modifiedSince to last returned reservation's modification
     * date + epsilon, as long as the number of results equals the limit.
     *
     * @param accessToken      Access token with permissions to the restaurant.
     * @param organizationId   The organization's identifier.
     * @param phone            Customer phone number in standard E.164 format.
     * @param modifiedSince    Minimum modification date to return, or null for oldest.
     * @param limit            Maximum number of reservations to return, or null for no limit.
     * @return a list of reservations.
     */
    List<Reservation> retrieveReservationsByPhone(String accessToken, String organizationId, String phone, Date modifiedSince, Integer limit);

    /**
     * Retrieves a batch of reservations associated with the given customer email.
     *
     * Reservations are returned in ascending order, by modification date. Paging can be done by setting a limit (say, 100),
     * starting with a null modifiedSince, and iteratively setting modifiedSince to last returned reservation's modification
     * date + epsilon, as long as the number of results equals the limit.
     *
     * @param accessToken      Access token with permissions to the restaurant.
     * @param organizationId   The organization's identifier.
     * @param email            Customer email.
     * @param modifiedSince    Minimum modification date to return, or null for oldest.
     * @param limit            Maximum number of reservations to return, or null for no limit.
     * @return a list of reservations.
     */
    List<Reservation> retrieveReservationsByEmail(String accessToken, String organizationId, String email, Date modifiedSince, Integer limit);

    // GDPR
    void deleteOrganization(String accessToken, String organizationId);
    void deleteCustomerByPhone(String accessToken, String organizationId, String phone);
    void deleteCustomerByEmail(String accessToken, String organizationId, String email);
}
