package com.wix.restaurants.orders.builders;

import com.openrest.olo.dispatches.Dispatch;
import com.openrest.v1_1.Contact;
import com.openrest.v1_1.Order;
import com.openrest.v1_1.OrderItem;
import com.wix.restaurants.helpers.PriceCalculator;
import com.wix.restaurants.i18n.Locale;
import com.wix.restaurants.payments.Payment;

import java.math.BigDecimal;
import java.util.Currency;

public class OrderBuilder {
    private final Order order = new Order();
    private PriceCalculator calculator = new PriceCalculator();

    public OrderBuilder() {
        order.price = 0;
    }

    public OrderBuilder setDeveloper(String developer) {
        order.developer = developer;
        return this;
    }

    public OrderBuilder setSource(String source) {
        order.source = source;
        return this;
    }

    public OrderBuilder setPlatform(String platform) {
        order.platform = platform;
        return this;
    }

    public OrderBuilder setRestaurant(String restaurantId) {
        order.restaurantId = restaurantId;
        return this;
    }

    public OrderBuilder setLocale(Locale locale) {
        order.locale = locale;
        return this;
    }

    public OrderBuilder setCurrency(Currency currency) {
        order.currency = currency.getCurrencyCode();
        return this;
    }

    public OrderBuilder setContact(Contact contact) {
        order.contact = contact;
        return this;
    }

    public OrderBuilder setDispatch(Dispatch dispatch) {
        order.delivery = dispatch;
        order.price += dispatch.charge;
        return this;
    }

    public OrderBuilder addItem(OrderItem orderItem) {
        order.orderItems.add(orderItem);
        order.price += BigDecimal.valueOf(calculator.price(orderItem)).movePointRight(2).intValueExact();
        return this;
    }

    public OrderBuilder setComment(String comment) {
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
