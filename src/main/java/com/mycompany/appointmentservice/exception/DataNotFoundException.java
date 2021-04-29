package com.mycompany.appointmentservice.exception;

public class DataNotFoundException extends RuntimeException {

    public DataNotFoundException(String resource, String message) {
        super(String.format("Failed for [%s]: %s", resource, message));
    }
}
