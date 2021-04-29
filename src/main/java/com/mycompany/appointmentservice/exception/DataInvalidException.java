package com.mycompany.appointmentservice.exception;

public class DataInvalidException extends RuntimeException {

    public DataInvalidException(String resource, String message) {
        super(String.format("Failed for [%s]: %s", resource, message));
    }
}
