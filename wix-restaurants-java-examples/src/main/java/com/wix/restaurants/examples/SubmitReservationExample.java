package com.wix.restaurants.examples;

import com.openrest.v1_1.Restaurant;
import com.wix.restaurants.DefaultWixRestaurantsClient;
import com.wix.restaurants.Platforms;
import com.wix.restaurants.Sources;
import com.wix.restaurants.WixRestaurantsClient;
import com.wix.restaurants.builders.ContactBuilder;
import com.wix.restaurants.i18n.Locale;
import com.wix.restaurants.reservations.Reservation;
import com.wix.restaurants.reservations.Statuses;
import com.wix.restaurants.reservations.builders.ReservationBuilder;
import scala.concurrent.duration.Duration;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Demonstrates the "Submit Reservation" flow.
 * 1) Retrieve the test restaurant's business info
 * 2) Create a new table reservation
 * 3) Submit the reservation
 * 4) Query the submitted reservation's status
 *
 * @see <a href="http://www.thetestaurant.com">The Testaurant</a>
 */
public class SubmitReservationExample {
    private final WixRestaurantsClient wixRestaurants;

    public SubmitReservationExample(WixRestaurantsClient wixRestaurants) {
        this.wixRestaurants = wixRestaurants;
    }

    public void runExample() {
        final String restaurantId = "8830975305376234"; // "The Testaurant"

        // 1. Retrieve business info
        System.out.print("Retrieving restaurant info...");
        final Restaurant restaurant = wixRestaurants.retrieveRestaurantInfo(restaurantId).restaurant;
        System.out.println(" done (accepts party sizes of " + restaurant.reservations.partySize.min + " to " + restaurant.reservations.partySize.max + ").");

        // 2. Build reservation
        final Reservation reservation = buildSomeReservation(restaurant);

        // 3. Submit reservation
        System.out.print("Submitting reservation...");
        final Reservation submittedReservation = wixRestaurants.submitReservation(null, reservation);
        System.out.println(" done (reservation ID: " + submittedReservation.id + ", status: " + submittedReservation.status +
                ", ownerToken: " + submittedReservation.ownerToken + ").");

        // 4. Query reservation status
        System.out.print("Retrieving reservation...");
        final Reservation retrievedReservation = wixRestaurants.retrieveReservationAsOwner(
                submittedReservation.ownerToken, submittedReservation.restaurantId, submittedReservation.id);
        System.out.println(" done (status: " + retrievedReservation.status + ").");
    }

    private Reservation buildSomeReservation(Restaurant restaurant) {
        // A table for two, for tomorrow 8pm
        final Calendar now = Calendar.getInstance(TimeZone.getTimeZone(restaurant.timezone));

        final Calendar time = (Calendar) now.clone();
        time.add(Calendar.DAY_OF_MONTH, 1);
        time.set(Calendar.HOUR_OF_DAY, 20);
        time.set(Calendar.MINUTE, 0);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);

        final Calendar heldUntil = (Calendar) time.clone();
        heldUntil.add(Calendar.MINUTE, restaurant.reservations.heldForMins);

        return new ReservationBuilder()
                .setDeveloper("org.example")
                .setPlatform(Platforms.web)
                .setSource(Sources.own)
                .setRestaurant(restaurant.id)
                .setLocale(Locale.fromJavaLocale(java.util.Locale.US))
                .setContact(new ContactBuilder()
                        .setFirstName("John")
                        .setLastName("Doe")
                        .setPhone("+12024561111")
                        .setEmail("johndoe@example.org")
                        .build())
                .setPartySize(2)
                .setTime(time.getTime())
                .setHeldUntil(heldUntil.getTime())
                .setStatus(Statuses.new_)
                .setComment("Non-smoking please!")
                .build();
    }

    public static void main(String[] args) throws Exception {
        final WixRestaurantsClient wixRestaurants = new DefaultWixRestaurantsClient(
                "https://api.wixrestaurants.com/v2",
                "https://auth.wixrestaurants.com/v1",
                Duration.Inf());

        new SubmitReservationExample(wixRestaurants).runExample();
    }
}
