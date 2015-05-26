package com.wix.restaurants.builders;

import com.openrest.v1_1.Payment;

import java.math.BigDecimal;

public class CashPaymentBuilder extends BasePaymentBuilder {
    public CashPaymentBuilder() {
        payment.type = Payment.PAYMENT_TYPE_CASH;
    }

    public CashPaymentBuilder amount(double amount) {
        payment.amount = BigDecimal.valueOf(amount).movePointRight(2).intValueExact();
        return this;
    }
}
