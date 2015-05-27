package com.wix.restaurants.examples.helpers;

import com.openrest.v1_1.Item;
import com.openrest.v1_1.Menu;

import java.util.LinkedHashMap;
import java.util.Map;

public class MenuHelper {
    private final Map<String, Item> itemsMap;

    public MenuHelper(Menu menu) {
        itemsMap = new LinkedHashMap<>(menu.items.size());
        for (Item item : menu.items) {
            itemsMap.put(item.id, item);
        }
    }

    public Item getItem(String itemId) {
        return itemsMap.get(itemId);
    }
}
