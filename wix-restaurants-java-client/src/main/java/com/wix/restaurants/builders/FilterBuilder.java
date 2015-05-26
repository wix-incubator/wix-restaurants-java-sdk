package com.wix.restaurants.builders;

import com.openrest.v1_1.Filter;
import com.openrest.v1_1.LatLng;

public class FilterBuilder {
    private final Filter filter = new Filter();

    public FilterBuilder latLng(double lat, double lng) {
        filter.latLng = new LatLng(lat, lng);
        return this;
    }

    public FilterBuilder latLng(LatLng latLng) {
        filter.latLng = latLng;
        return this;
    }

    public FilterBuilder radius(double radius) {
        filter.radius = radius;
        return this;
    }

    public Filter build() {
        return filter;
    }
}
