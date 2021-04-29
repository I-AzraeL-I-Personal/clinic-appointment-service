package com.mycompany.appointmentservice.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class AppointmentDetails {

    @Id
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(columnDefinition = "text", length = 1000)
    private String description;

    @Column
    private String prescriptionUri;

    @Column
    private String attachmentUri;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private Appointment appointment;
}
