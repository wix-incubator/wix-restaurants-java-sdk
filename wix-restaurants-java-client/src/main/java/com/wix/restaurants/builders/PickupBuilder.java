package com.wix.restaurants.builders;

import com.openrest.olo.dispatches.PickupDispatch;

public class PickupBuilder {
    private final PickupDispatch pickupDispatch = new PickupDispatch();

    public PickupDispatch build() {
        return pickupDispatch;
    }
}
