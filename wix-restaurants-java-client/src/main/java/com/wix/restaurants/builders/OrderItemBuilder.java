package com.wix.restaurants.builders;

import com.openrest.v1_1.Item;
import com.openrest.v1_1.OrderItem;
import com.openrest.v1_1.Variation;

import java.util.ArrayList;
import java.util.LinkedList;

public class OrderItemBuilder {
    private final OrderItem orderItem;

    public OrderItemBuilder(Item item, Variation context) {
        orderItem = new OrderItem();
        orderItem.itemId = item.id;
        orderItem.count = 1;

        if (context == null) {
            orderItem.price = item.price;
        } else {
            Integer priceOverride = context.prices.get(item.id);
            orderItem.price = ((priceOverride != null) ? priceOverride : 0);
        }

        orderItem.variations = item.variations;
        orderItem.variationsChoices = new ArrayList<>(item.variations.size());
        for (Variation variation : item.variations) {
            orderItem.variationsChoices.add(new LinkedList<OrderItem>());
        }
    }

    public OrderItemBuilder(Item item) {
        this(item, null);
    }

    public OrderItemBuilder count(int count) {
        orderItem.count = count;
        return this;
    }

    public OrderItemBuilder comment(String comment) {
        orderItem.comment = comment;
        return this;
    }

    public OrderItemBuilder addChoice(int index, OrderItem choice) {
        orderItem.variationsChoices.get(index).add(choice);
        return this;
    }

    public OrderItem build() {
        return orderItem;
    }
}
