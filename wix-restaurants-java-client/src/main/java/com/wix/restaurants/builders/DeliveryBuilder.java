package com.wix.restaurants.builders;

import com.openrest.v1_1.Address;
import com.openrest.v1_1.Delivery;

public class DeliveryBuilder extends BaseDeliveryBuilder {
    public DeliveryBuilder() {
        delivery.type = Delivery.DELIVERY_TYPE_DELIVERY;
    }

    public DeliveryBuilder toAddress(Address address) {
        delivery.address = address;
        return this;
    }
}
