package com.mycompany.appointmentservice.security;

public enum Role {

    ADMIN,
    DOCTOR,
    PATIENT;

    public String withPrefix() {
        return "ROLE_" + this.name();
    }
}
