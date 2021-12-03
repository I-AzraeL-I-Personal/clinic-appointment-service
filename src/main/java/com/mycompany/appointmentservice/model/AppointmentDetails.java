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

    private String prescription;

    private String attachment;

    private String prescriptionFormat;

    private String attachmentFormat;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private Appointment appointment;
}
