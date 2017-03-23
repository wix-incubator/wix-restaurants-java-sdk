package com.wix.restaurants.reservations.builders;

import com.openrest.v1_1.Contact;
import com.wix.restaurants.TimeGuarantees;
import com.wix.restaurants.i18n.Locale;
import com.wix.restaurants.reservations.Reservation;

import java.util.Date;
import java.util.LinkedHashMap;

public class ReservationBuilder {
    private final Reservation reservation = new Reservation();

    public ReservationBuilder() {
        reservation.properties = new LinkedHashMap<>();
    }

    public ReservationBuilder setDeveloper(String developer) {
        reservation.developer = developer;
        return this;
    }

    public ReservationBuilder setPlatform(String platform) {
        reservation.platform = platform;
        return this;
    }

    public ReservationBuilder setSource(String source) {
        reservation.source = source;
        return this;
    }

    public ReservationBuilder setRestaurant(String restaurantId) {
        reservation.restaurantId = restaurantId;
        return this;
    }

    public ReservationBuilder setLocale(Locale locale) {
        reservation.locale = locale;
        return this;
    }

    public ReservationBuilder setContact(Contact contact) {
        reservation.contact = contact;
        return this;
    }

    public ReservationBuilder setPartySize(int partySize) {
        reservation.partySize = partySize;
        return this;
    }

    public ReservationBuilder setTime(Date time) {
        reservation.timeGuarantee = TimeGuarantees.approximate;
        reservation.time = time;
        return this;
    }

    public ReservationBuilder setHeldUntil(Date heldUntil) {
        reservation.heldUntil = heldUntil;
        return this;
    }

    public ReservationBuilder setStatus(String status) {
        reservation.status = status;
        return this;
    }

    public ReservationBuilder setComment(String comment) {
        reservation.comment = comment;
        return this;
    }

    public Reservation build() {
        return reservation;
    }
}
