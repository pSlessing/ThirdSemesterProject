package com.mailmak.time_registration_system.exceptions;

public class CustomerNotFound extends RuntimeException {
    public CustomerNotFound(String customername) {

      super("Customer with name " + customername + " not found");
    }
}
