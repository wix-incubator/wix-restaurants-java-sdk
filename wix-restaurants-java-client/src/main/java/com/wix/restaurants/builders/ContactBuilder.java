package com.wix.restaurants.builders;

import com.openrest.v1_1.Contact;

public class ContactBuilder {
    private final Contact contact = new Contact();

    public ContactBuilder setFirstName(String firstName) {
        contact.firstName = firstName;
        return this;
    }

    public ContactBuilder setLastName(String lastName) {
        contact.lastName = lastName;
        return this;
    }

    public ContactBuilder setPhone(String phone) {
        contact.phone = phone;
        return this;
    }

    public ContactBuilder setEmail(String email) {
        contact.email = email;
        return this;
    }

    public Contact build() {
        return contact;
    }
}
