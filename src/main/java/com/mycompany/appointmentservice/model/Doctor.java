package com.mycompany.appointmentservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;
import java.util.UUID;

@Getter
@Setter
@Embeddable
public class Doctor {

    @Column(nullable = false)
    private UUID doctorUUID;

    @Column(nullable = false)
    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String middleName;

    @Column(nullable = false)
    @Size(max = 50)
    private String lastName;
}
