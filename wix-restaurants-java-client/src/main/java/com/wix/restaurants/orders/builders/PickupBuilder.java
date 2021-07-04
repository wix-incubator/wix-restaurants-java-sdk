package com.wix.restaurants.orders.builders;

import com.openrest.olo.dispatches.PickupDispatch;
import com.openrest.v1_1.ContactlessDineIn;
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

    public PickupBuilder forAsapWithContactless(String label, String labelValue) {
        pickupDispatch.timeGuarantee = TimeGuarantees.before;
        pickupDispatch.time = null;
        pickupDispatch.contactlessDineIn = new ContactlessDineIn(label, labelValue);
        return this;
    }

    public PickupDispatch build() {
        return pickupDispatch;
    }
}
