package com.wix.restaurants.examples.helpers;

import com.openrest.v1_1.Item;
import com.openrest.v1_1.Menu;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MenuHelper {
    private final Menu menu;
    private final Map<String, Item> itemsMap;

    public MenuHelper(Menu menu) {
        this.menu = menu;

        itemsMap = new LinkedHashMap<>(menu.items.size());
        for (Item item : menu.items) {
            itemsMap.put(item.id, item);
        }
    }

    public Item getItem(String itemId) {
        return itemsMap.get(itemId);
    }

    public List<Item> search(String text) {
        final List<Item> results = new LinkedList<>();
        for (Item item : menu.items) {
            if (matches(item, text)) {
                results.add(item);
            }
        }
        return results;
    }

    public Item findFirst(String text) {
        return search(text).get(0);
    }

    private boolean matches(Item item, String text) {
        // Search all locales
        for (String title : item.title.values()) {
            if (title.toLowerCase().contains(text.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
