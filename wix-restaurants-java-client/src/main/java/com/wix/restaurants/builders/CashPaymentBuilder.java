package com.wix.restaurants.builders;

import com.openrest.olo.payments.CashPayment;

import java.math.BigDecimal;

public class CashPaymentBuilder {
    private final CashPayment payment = new CashPayment();

    public CashPaymentBuilder amount(double amount) {
        payment.amount = BigDecimal.valueOf(amount).movePointRight(2).intValueExact();
        return this;
    }

    public CashPayment build() {
        return payment;
    }
}
