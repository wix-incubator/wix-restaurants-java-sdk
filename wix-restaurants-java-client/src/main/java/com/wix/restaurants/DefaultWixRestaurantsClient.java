package com.wix.restaurants;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.openrest.v1_1.*;
import com.openrest.v1_1.Error;
import com.wix.restaurants.authentication.DefaultWixRestaurantsAuthenticationClient;
import com.wix.restaurants.authentication.WixRestaurantsAuthenticationClient;
import com.wix.restaurants.authorization.Role;
import com.wix.restaurants.authorization.requests.GetRoleRequest;
import com.wix.restaurants.exceptions.*;
import com.wix.restaurants.orders.Statuses;
import com.wix.restaurants.orders.requests.GetOrderRequest;
import com.wix.restaurants.orders.requests.QueryOrdersRequest;
import com.wix.restaurants.orders.requests.SetOrderStatusRequest;
import com.wix.restaurants.orders.requests.SubmitOrderRequest;
import com.wix.restaurants.requests.*;
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

    public static class Builder {
        private HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
        private int connectTimeout = 0;
        private int readTimeout = 0;
        private int numberOfRetries = 0;
        private String authApiUrl = com.wix.restaurants.authentication.Endpoints.PRODUCTION;
        private String apiUrl = Endpoints.production;

        public Builder setRequestFactory(HttpRequestFactory requestFactory) {
            this.requestFactory = requestFactory;
            return this;
        }

        public Builder setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder setNumberOfRetries(int numberOfRetries) {
            this.numberOfRetries = numberOfRetries;
            return this;
        }

        public Builder setAuthApiUrl(String authApiUrl) {
            this.authApiUrl = authApiUrl;
            return this;
        }

        public Builder setApiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
            return this;
        }

        public DefaultWixRestaurantsClient build() {
            return new DefaultWixRestaurantsClient(
                    requestFactory, connectTimeout, readTimeout, numberOfRetries,
                    authApiUrl, apiUrl);
        }
    }

    public DefaultWixRestaurantsClient(HttpRequestFactory requestFactory,
                                       Integer connectTimeout, Integer readTimeout, Integer numberOfRetries,
                                       String authApiUrl, String apiUrl) {
        openrest = new OpenrestClient(requestFactory, connectTimeout, readTimeout, numberOfRetries, apiUrl);
        authenticationClient = new DefaultWixRestaurantsAuthenticationClient(
                requestFactory, connectTimeout, readTimeout, numberOfRetries, authApiUrl);
    }

    public DefaultWixRestaurantsClient(HttpRequestFactory requestFactory,
                                       Integer connectTimeout, Integer readTimeout, Integer numberOfRetries) {
        this(requestFactory, connectTimeout, readTimeout, numberOfRetries,
                com.wix.restaurants.authentication.Endpoints.PRODUCTION,
                Endpoints.production);
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
    public Organization setOrganization(String accessToken, Organization organization) {
        final SetOrganizationRequest setOrganizationRequest = new SetOrganizationRequest();
        setOrganizationRequest.accessToken = accessToken;
        setOrganizationRequest.organization = organization;

        final Organization setOrganizationResponse = request(
                setOrganizationRequest, new TypeReference<Response<Organization>>() {});

        return setOrganizationResponse;
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
    public Role getRole(String accessToken, String organizationId) {
        final GetRoleRequest getRoleRequest = new GetRoleRequest();
        getRoleRequest.accessToken = accessToken;
        getRoleRequest.organizationId = organizationId;

        final Role getRoleResponse = request(
                getRoleRequest, new TypeReference<Response<Role>>() {});

        return getRoleResponse;
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

    @Override
    public Organization retrieveOrganizationForInstance(String instanceId) {
        final GetAppMappedObjectRequest getAppMappedObjectRequest = new GetAppMappedObjectRequest();
        getAppMappedObjectRequest.appId = new AppId();
        getAppMappedObjectRequest.appId.platform = AppId.NS_WIX;
        getAppMappedObjectRequest.appId.id = instanceId;
        getAppMappedObjectRequest.appId.version = "1";
        getAppMappedObjectRequest.full = false;

        final Organization getAppMappedObjectResponse = request(
                getAppMappedObjectRequest, new TypeReference<Response<Organization>>() {});

        return getAppMappedObjectResponse;
    }

    @Override
    public Organization retrieveOrganizationForMetasite(String metasiteId) {
        final GetWixMetasiteOrganizationRequest getWixMetasiteOrganizationRequest = new GetWixMetasiteOrganizationRequest();
        getWixMetasiteOrganizationRequest.metasiteId = metasiteId;
        getWixMetasiteOrganizationRequest.full = false;

        final Organization getWixMetasiteOrganizationResponse = request(
                getWixMetasiteOrganizationRequest, new TypeReference<Response<Organization>>() {});

        return getWixMetasiteOrganizationResponse;
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
            case Error.ERROR_NOT_FOUND:
                return new NotFoundException(e.errorMessage(), e);
            default:
                return new RestaurantsException(e.error() + "|" + e.errorMessage(), e);
        }
    }
}
