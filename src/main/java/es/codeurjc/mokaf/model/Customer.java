package es.codeurjc.mokaf.model;

import jakarta.persistence.Entity;

@Entity
public class Customer extends User {
    
    private String shippingAddress;

    public Customer() {
    }

    public Customer(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}