package com.training.handson.dto;

public class CustomerCreateRequest {

    private String email;
    private String password;
    private String customerKey;
    private String firstName;
    private String lastName;
    private String country;
    private String anonymousCartId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCustomerKey() {
        return customerKey;
    }

    public void setCustomerKey(String customerKey) {
        this.customerKey = customerKey;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAnonymousCartId() {
        return anonymousCartId;
    }

    public void setCartId(String anonymousCartId) {
        this.anonymousCartId = anonymousCartId;
    }
}
