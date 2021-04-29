package com.mycompany.appointmentservice.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AppointmentType {

    BASIC(15),
    SPECIALIST(30);

    public final int duration;
}
