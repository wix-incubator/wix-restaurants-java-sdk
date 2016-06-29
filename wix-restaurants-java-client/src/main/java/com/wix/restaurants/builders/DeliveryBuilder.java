package com.wix.restaurants.builders;

import com.openrest.olo.dispatches.DeliveryDispatch;
import com.openrest.olo.dispatches.Dispatch;
import com.openrest.v1_1.Address;

import java.util.Date;

public class DeliveryBuilder {
    private final DeliveryDispatch deliveryDispatch = new DeliveryDispatch();

    public DeliveryBuilder forAsap() {
        deliveryDispatch.timeGuarantee = Dispatch.TIME_GUARANTEE_BEFORE;
        deliveryDispatch.time = null;
        return this;
    }

    public DeliveryBuilder forFutureTime(Date when) {
        deliveryDispatch.timeGuarantee = Dispatch.TIME_GUARANTEE_APPROXIMATE;
        deliveryDispatch.time = when.getTime();
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
