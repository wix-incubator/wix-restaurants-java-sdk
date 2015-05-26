package com.wix.restaurants.helpers;

import com.openrest.v1_1.OrderItem;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class PriceCalculator {
    public double price(OrderItem orderItem) {
        return priceImpl(orderItem).doubleValue();
    }

    public double price(List<OrderItem> orderItems) {
        return priceImpl(orderItems).doubleValue();
    }

    public double price(OrderItem... orderItems) {
        return price(Arrays.asList(orderItems));
    }

    private BigDecimal priceImpl(OrderItem orderItem) {
        BigDecimal choicesTotal = new BigDecimal(0);
        for (List<OrderItem> choices : orderItem.variationsChoices) {
            choicesTotal = choicesTotal.add(priceImpl(choices));
        }
        return BigDecimal.valueOf(orderItem.price).movePointLeft(2)
                .add(choicesTotal)
                .multiply(BigDecimal.valueOf(orderItem.count));
    }

    private BigDecimal priceImpl(List<OrderItem> orderItems) {
        BigDecimal total = new BigDecimal(0);
        for (OrderItem orderItem : orderItems) {
            total = total.add(priceImpl(orderItem));
        }
        return total;
    }
}
