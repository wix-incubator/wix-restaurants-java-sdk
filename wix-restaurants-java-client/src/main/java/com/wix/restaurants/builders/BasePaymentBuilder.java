package com.wix.restaurants.builders;

import com.openrest.v1_1.Payment;

class BasePaymentBuilder {
    protected final Payment payment = new Payment();

    protected BasePaymentBuilder() {}

    public Payment build() {
        return payment;
    }
}
