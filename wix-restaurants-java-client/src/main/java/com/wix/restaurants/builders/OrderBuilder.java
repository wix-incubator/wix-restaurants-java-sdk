package com.wix.restaurants.builders;

import com.openrest.olo.payments.Payment;
import com.openrest.v1_1.Contact;
import com.openrest.v1_1.Delivery;
import com.openrest.v1_1.Order;
import com.openrest.v1_1.OrderItem;
import com.wix.restaurants.helpers.PriceCalculator;

import java.math.BigDecimal;
import java.util.Locale;

public class OrderBuilder {
    private final Order order = new Order();
    private PriceCalculator calculator = new PriceCalculator();

    public OrderBuilder() {
        order.price = 0;
    }

    public OrderBuilder developer(String developer) {
        order.developer = developer;
        return this;
    }

    public OrderBuilder source(String source) {
        order.source = source;
        return this;
    }

    public OrderBuilder restaurant(String restaurantId) {
        order.restaurantId = restaurantId;
        return this;
    }

    public OrderBuilder locale(Locale locale) {
        order.locale = locale.toString();
        return this;
    }

    public OrderBuilder contact(Contact contact) {
        order.contact = contact;
        return this;
    }

    public OrderBuilder delivery(Delivery delivery) {
        order.delivery = delivery;
        order.price += delivery.charge;
        return this;
    }

    public OrderBuilder addItem(OrderItem orderItem) {
        order.orderItems.add(orderItem);
        order.price += BigDecimal.valueOf(calculator.price(orderItem)).movePointRight(2).intValueExact();
        return this;
    }

    public OrderBuilder comment(String comment) {
        order.comment = comment;
        return this;
    }

    public OrderBuilder addPayment(Payment payment) {
        order.payments.add(payment);
        return this;
    }

    public Order build() {
        return order;
    }
}
