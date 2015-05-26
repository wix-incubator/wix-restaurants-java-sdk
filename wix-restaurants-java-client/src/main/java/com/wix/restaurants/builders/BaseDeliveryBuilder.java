package com.wix.restaurants.builders;

import com.openrest.v1_1.Delivery;

class BaseDeliveryBuilder {
    protected final Delivery delivery = new Delivery();

    protected BaseDeliveryBuilder() {}

    public Delivery build() {
        return delivery;
    }
}
