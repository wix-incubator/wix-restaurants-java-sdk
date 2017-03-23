package com.wix.restaurants.orders.builders;

import com.openrest.olo.dispatches.DeliveryDispatch;
import com.openrest.v1_1.Address;
import com.wix.restaurants.TimeGuarantees;

import java.util.Date;

public class DeliveryBuilder {
    private final DeliveryDispatch deliveryDispatch = new DeliveryDispatch();

    public DeliveryBuilder forAsap() {
        deliveryDispatch.timeGuarantee = TimeGuarantees.before;
        deliveryDispatch.time = null;
        return this;
    }

    public DeliveryBuilder forFutureTime(Date when) {
        deliveryDispatch.timeGuarantee = TimeGuarantees.approximate;
        deliveryDispatch.time = when;
        return this;
    }

    public DeliveryBuilder toAddress(Address address) {
        deliveryDispatch.address = address;
        return this;
    }

    public DeliveryDispatch build() {
        return deliveryDispatch;
    }
}
