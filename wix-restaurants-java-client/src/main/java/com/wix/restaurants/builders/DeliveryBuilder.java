package com.wix.restaurants.builders;

import com.openrest.olo.dispatches.DeliveryDispatch;
import com.openrest.v1_1.Address;

public class DeliveryBuilder {
    private final DeliveryDispatch deliveryDispatch = new DeliveryDispatch();

    public DeliveryBuilder toAddress(Address address) {
        deliveryDispatch.address = address;
        return this;
    }

    public DeliveryDispatch build() {
        return deliveryDispatch;
    }
}
