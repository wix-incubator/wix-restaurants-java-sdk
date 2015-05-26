package com.wix.restaurants.builders;

import com.openrest.v1_1.Address;
import com.openrest.v1_1.Delivery;

public class TakeoutBuilder extends BaseDeliveryBuilder {
    public TakeoutBuilder() {
        delivery.type = Delivery.DELIVERY_TYPE_TAKEOUT;
    }
}
