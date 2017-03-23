package com.wix.restaurants;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.api.client.http.HttpRequestFactory;
import com.openrest.v1_1.Error;
import com.openrest.v1_1.*;
import com.wix.restaurants.authentication.DefaultWixRestaurantsAuthenticationClient;
import com.wix.restaurants.authentication.WixRestaurantsAuthenticationClient;
import com.wix.restaurants.exceptions.*;
import com.wix.restaurants.olo.Statuses;
import com.wix.restaurants.reservations.Reservation;
import com.wix.restaurants.reservations.ReservationsResponse;
import com.wix.restaurants.reservations.requests.GetReservationRequest;
import com.wix.restaurants.reservations.requests.QueryUnhandledReservationsRequest;
import com.wix.restaurants.reservations.requests.SetReservationStatusRequest;
import com.wix.restaurants.reservations.requests.SubmitReservationRequest;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DefaultWixRestaurantsClient implements WixRestaurantsClient {
    private final OpenrestClient openrest;
    private final WixRestaurantsAuthenticationClient authenticationClient;

    public DefaultWixRestaurantsClient(HttpRequestFactory requestFactory, Integer connectTimeout, Integer readTimeout,
                                       Integer numberOfRetries) {
        openrest = new OpenrestClient(requestFactory, connectTimeout, readTimeout, numberOfRetries, Endpoints.production);
        authenticationClient = new DefaultWixRestaurantsAuthenticationClient(
                requestFactory, connectTimeout, readTimeout, numberOfRetries,
                com.wix.restaurants.authentication.Endpoints.PRODUCTION);
    }

    @Override
    public WixRestaurantsAuthenticationClient getAuthenticationClient() {
        return authenticationClient;
    }

    @Override
    public RestaurantFullInfo retrieveRestaurantInfo(String restaurantId) {
        final GetOrganizationFullRequest getOrganizationFullRequest = new GetOrganizationFullRequest();
        getOrganizationFullRequest.organizationId = restaurantId;

        final RestaurantFullInfo getOrganizationFullResponse = request(
                getOrganizationFullRequest, new TypeReference<Response<RestaurantFullInfo>>() {});

        return getOrganizationFullResponse;
    }

    @Override
    public Order submitOrder(String accessToken, Order order) {
        final SubmitOrderRequest submitOrderRequest = new SubmitOrderRequest();
        submitOrderRequest.accessToken = accessToken;
        submitOrderRequest.order = order;

        final OrderConfirmation submitOrderResponse = request(
                submitOrderRequest, new TypeReference<Response<OrderConfirmation>>() {});

        return submitOrderResponse.order;
    }

    @Override
    public Order retrieveOrderAsOwner(String orderId, String ownerToken) {
        final GetOrderRequest getOrderRequest = new GetOrderRequest();
        getOrderRequest.orderId = orderId;
        getOrderRequest.viewMode = Actors.customer;
        getOrderRequest.ownerToken = ownerToken;

        final Order getOrderResponse = request(
                getOrderRequest, new TypeReference<Response<Order>>() {});

        return getOrderResponse;
    }

    @Override
    public Order retrieveOrderAsRestaurant(String accessToken, String orderId) {
        final GetOrderRequest getOrderRequest = new GetOrderRequest();
        getOrderRequest.accessToken = accessToken;
        getOrderRequest.orderId = orderId;
        getOrderRequest.viewMode = Actors.restaurant;

        final Order getOrderResponse = request(
                getOrderRequest, new TypeReference<Response<Order>>() {});

        return getOrderResponse;
    }


    @Override
    public List<SearchResult> search(Filter filter, int limit) {
        final SearchRequest searchRequest = new SearchRequest();
        searchRequest.filter = filter;
        searchRequest.limit = limit;

        final SearchResponse searchResponse = request(
                searchRequest, new TypeReference<Response<SearchResponse>>() {});

        return searchResponse.results;
    }

    @Override
    public List<Order> retrieveNewOrders(String accessToken, String restaurantId) {
        final QueryOrdersRequest queryOrdersRequest = new QueryOrdersRequest();
        queryOrdersRequest.accessToken = accessToken;
        queryOrdersRequest.restaurantIds = Collections.singleton(restaurantId);
        queryOrdersRequest.viewMode = Actors.restaurant;
        queryOrdersRequest.status = Statuses.new_;
        queryOrdersRequest.ordering = "asc";
        queryOrdersRequest.limit = Integer.MAX_VALUE;

        final OrdersResponse queryOrdersResponse = request(
                queryOrdersRequest, new TypeReference<Response<OrdersResponse>>() {});

        return queryOrdersResponse.results;
    }

    @Override
    public Order acceptOrder(String accessToken, String orderId, Map<String, String> externalIds) {
        final SetOrderStatusRequest setOrderStatusRequest = new SetOrderStatusRequest();
        setOrderStatusRequest.accessToken = accessToken;
        setOrderStatusRequest.orderId = orderId;
        setOrderStatusRequest.status = Statuses.accepted;
        setOrderStatusRequest.externalIds = externalIds;

        final Order setOrderStatusResponse = request(
                setOrderStatusRequest, new TypeReference<Response<Order>>() {});

        return setOrderStatusResponse;
    }

    @Override
    public Order rejectOrder(String accessToken, String orderId, String comment) {
        final SetOrderStatusRequest setOrderStatusRequest = new SetOrderStatusRequest();
        setOrderStatusRequest.accessToken = accessToken;
        setOrderStatusRequest.orderId = orderId;
        setOrderStatusRequest.status = Statuses.canceled;
        setOrderStatusRequest.comment = comment;

        final Order setOrderStatusResponse = request(
                setOrderStatusRequest, new TypeReference<Response<Order>>() {});

        return setOrderStatusResponse;
    }

    @Override
    public Reservation submitReservation(String accessToken, Reservation reservation) {
        final SubmitReservationRequest submitReservationRequest = new SubmitReservationRequest();
        submitReservationRequest.accessToken = accessToken;
        submitReservationRequest.reservation = reservation;

        final Reservation submitReservationResponse = request(
                submitReservationRequest, new TypeReference<Response<Reservation>>() {});

        return submitReservationResponse;
    }

    @Override
    public Reservation retrieveReservationAsOwner(String ownerToken, String reservationId) {
        final GetReservationRequest getReservationRequest = new GetReservationRequest();
        getReservationRequest.ownerToken = ownerToken;
        getReservationRequest.reservationId = reservationId;
        getReservationRequest.viewMode = Actors.customer;

        final Reservation getReservationResponse = request(
                getReservationRequest, new TypeReference<Response<Reservation>>() {});

        return getReservationResponse;
    }

    @Override
    public Reservation retrieveReservationAsRestaurant(String accessToken, String reservationId) {
        final GetReservationRequest getReservationRequest = new GetReservationRequest();
        getReservationRequest.accessToken = accessToken;
        getReservationRequest.reservationId = reservationId;
        getReservationRequest.viewMode = Actors.restaurant;

        final Reservation getReservationResponse = request(
                getReservationRequest, new TypeReference<Response<Reservation>>() {});

        return getReservationResponse;
    }

    @Override
    public List<Reservation> retrieveUnhandledReservations(String accessToken, String restaurantId) {
        final QueryUnhandledReservationsRequest queryUnhandledReservationsRequest = new QueryUnhandledReservationsRequest();
        queryUnhandledReservationsRequest.accessToken = accessToken;
        queryUnhandledReservationsRequest.organizationId = restaurantId;
        queryUnhandledReservationsRequest.viewMode = Actors.restaurant;

        final ReservationsResponse queryUnhandledReservationsResponse = request(
                queryUnhandledReservationsRequest, new TypeReference<Response<ReservationsResponse>>() {});

        return queryUnhandledReservationsResponse.results;
    }

    @Override
    public Reservation setReservationStatusAsRestaurant(String accessToken, String reservationId, String status, String comment) {
        final SetReservationStatusRequest setReservationStatusRequest = new SetReservationStatusRequest();
        setReservationStatusRequest.accessToken = accessToken;
        setReservationStatusRequest.reservationId = reservationId;
        setReservationStatusRequest.status = status;
        setReservationStatusRequest.actingAs = Actors.restaurant;
        setReservationStatusRequest.comment = comment;

        final Reservation setReservationStatusResponse = request(
                setReservationStatusRequest, new TypeReference<Response<Reservation>>() {});

        return setReservationStatusResponse;
    }

    @Override
    public Reservation setReservationStatusAsOwner(String ownerToken, String reservationId, String status, String comment) {
        final SetReservationStatusRequest setReservationStatusRequest = new SetReservationStatusRequest();
        setReservationStatusRequest.ownerToken = ownerToken;
        setReservationStatusRequest.reservationId = reservationId;
        setReservationStatusRequest.status = status;
        setReservationStatusRequest.actingAs = Actors.customer;
        setReservationStatusRequest.comment = comment;

        final Reservation setReservationStatusResponse = request(
                setReservationStatusRequest, new TypeReference<Response<Reservation>>() {});

        return setReservationStatusResponse;
    }

    private <T> T request(Request request, TypeReference<Response<T>> responseType) {
        try {
            return openrest.request(request, responseType);
        } catch (IOException e) {
            throw new CommunicationException(e.getMessage(), e);
        } catch (OpenrestException e) {
            throw translateException(e);
        }
    }

    private static RestaurantsException translateException(OpenrestException e) {
        switch (e.error()) {
            case Error.ERROR_NO_PERMISSION:
                return new NoPermissionException(e.errorMessage(), e);
            case Error.ERROR_INVALID_DATA:
                return new InvalidDataException(e.errorMessage(), e);
            case Error.ERROR_INTERNAL:
                return new InternalException(e.errorMessage(), e);
            default:
                return new RestaurantsException(e.error() + "|" + e.errorMessage(), e);
        }
    }
}
