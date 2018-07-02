package com.wix.restaurants;

public class Errors {
    private Errors() {}

    // Client
    public static final String InvalidData            = "https://www.wixrestaurants.com/errors/invalid_data";
    public static final String PaymentRejected        = "https://www.wixrestaurants.com/errors/cc_rejected";
    public static final String PaymentExceedsLimit    = "https://www.wixrestaurants.com/errors/payment_exceeds_limit";
    public static final String Forbidden              = "https://www.wixrestaurants.com/errors/no_permission";
    public static final String Conflict               = "https://www.wixrestaurants.com/errors/conflict";
    public static final String Authentication         = "https://www.wixrestaurants.com/errors/authentication";
    public static final String NotSecure              = "https://www.wixrestaurants.com/errors/not_secure";
    public static final String NotFound               = "https://www.wixrestaurants.com/errors/not_found";

    // Server
    public static final String Internal               = "https://www.wixrestaurants.com/errors/internal";
    public static final String TemporarilyUnavailable = "https://www.wixrestaurants.com/errors/temporarily_unavailable";
    public static final String Deprecated             = "https://www.wixrestaurants.com/errors/deprecated";


    // TODO: consolidate with other error types
    public static final String CannotSubmitOrder        = "https://www.wixrestaurants.com/errors/cannot_submit_order";
    public static final String Unavailable              = "https://www.wixrestaurants.com/errors/unavailable";
    public static final String AddressNotInRange        = "https://www.wixrestaurants.com/errors/address_not_in_range";
    public static final String PaymentMethodUnavailable = "https://www.wixrestaurants.com/errors/payment_method_unavailable";
    public static final String OutOfStock               = "https://www.wixrestaurants.com/errors/out_of_stock";
}
