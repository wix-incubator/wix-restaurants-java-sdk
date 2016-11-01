package com.wix.restaurants.builders;

import com.openrest.olo.dispatches.Dispatch;
import com.openrest.olo.dispatches.PickupDispatch;
import com.wix.restaurants.TimeGuarantees;

import java.util.Date;

public class PickupBuilder {
    private final PickupDispatch pickupDispatch = new PickupDispatch();

    public PickupBuilder forAsap() {
        pickupDispatch.timeGuarantee = TimeGuarantees.before;
        pickupDispatch.time = null;
        return this;
    }

    public PickupBuilder forFutureTime(Date when) {
        pickupDispatch.timeGuarantee = TimeGuarantees.approximate;
        pickupDispatch.time = when;
        return this;
    }

    public PickupDispatch build() {
        return pickupDispatch;
    }
}
