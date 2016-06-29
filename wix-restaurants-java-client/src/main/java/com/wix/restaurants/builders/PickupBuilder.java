package com.wix.restaurants.builders;

import com.openrest.olo.dispatches.Dispatch;
import com.openrest.olo.dispatches.PickupDispatch;

import java.util.Date;

public class PickupBuilder {
    private final PickupDispatch pickupDispatch = new PickupDispatch();

    public PickupBuilder forAsap() {
        pickupDispatch.timeGuarantee = Dispatch.TIME_GUARANTEE_BEFORE;
        pickupDispatch.time = null;
        return this;
    }

    public PickupBuilder forFutureTime(Date when) {
        pickupDispatch.timeGuarantee = Dispatch.TIME_GUARANTEE_APPROXIMATE;
        pickupDispatch.time = when.getTime();
        return this;
    }

    public PickupDispatch build() {
        return pickupDispatch;
    }
}
