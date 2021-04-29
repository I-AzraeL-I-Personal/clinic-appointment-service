package com.mycompany.appointmentservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class AppointmentDetailsDto {

    @Size(max = 1000, message = "description cannot be longer than {max}")
    private String description;

    @JsonProperty(access = Access.READ_ONLY)
    private String prescription;

    @JsonProperty(access = Access.READ_ONLY)
    private String attachment;

}
