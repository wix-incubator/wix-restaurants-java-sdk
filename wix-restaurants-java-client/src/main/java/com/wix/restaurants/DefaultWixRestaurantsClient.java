package com.wix.restaurants;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.api.client.http.HttpRequestFactory;
import com.openrest.v1_1.*;
import com.openrest.v1_1.Error;
import com.wix.restaurants.exceptions.*;

import java.io.IOException;
import java.util.List;

public class DefaultWixRestaurantsClient implements WixRestaurantsClient {
    private final OpenrestClient openrest;

    public DefaultWixRestaurantsClient(HttpRequestFactory requestFactory, Integer connectTimeout, Integer readTimeout) {
        openrest = new OpenrestClient(requestFactory, "https://api.openrest.com/v1.1", connectTimeout, readTimeout);
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
    public Order submitOrder(Order order) {
        final SubmitOrderRequest submitOrderRequest = new SubmitOrderRequest();
        submitOrderRequest.order = order;

        final OrderConfirmation submitOrderResponse = request(
                submitOrderRequest, new TypeReference<Response<OrderConfirmation>>() {});

        return submitOrderResponse.order;
    }

    @Override
    public Order retrieveOrderAsOwner(String orderId, String ownerToken) {
        final GetOrderRequest getOrderRequest = new GetOrderRequest();
        getOrderRequest.orderId = orderId;
        getOrderRequest.viewMode = Order.ORDER_VIEW_MODE_CUSTOMER;
        getOrderRequest.ownerToken = ownerToken;

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
