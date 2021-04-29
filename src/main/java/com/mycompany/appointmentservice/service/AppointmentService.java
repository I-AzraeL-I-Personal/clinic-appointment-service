package com.mycompany.appointmentservice.service;

import com.mycompany.appointmentservice.dto.*;
import com.mycompany.appointmentservice.exception.DataAlreadyExistsException;
import com.mycompany.appointmentservice.exception.DataInvalidException;
import com.mycompany.appointmentservice.model.Appointment;
import com.mycompany.appointmentservice.model.AppointmentDetails;
import com.mycompany.appointmentservice.model.AppointmentType;
import com.mycompany.appointmentservice.model.Doctor;
import com.mycompany.appointmentservice.repository.AppointmentDetailsRepository;
import com.mycompany.appointmentservice.repository.AppointmentRepository;
import com.mycompany.appointmentservice.util.AppointmentParser;
import com.mycompany.appointmentservice.util.AppointmentValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    @Value("${app.usedTimezone}")
    private String usedTimezone;

    private final DoctorService doctorService;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentDetailsRepository detailsRepository;
    private final ModelMapper modelMapper;
    private final AppointmentValidator appointmentValidator;

    @Transactional(readOnly = true)
    public List<AppointmentDto> getAllByPatientUUID(UUID patientUUID) {
        var appointmentDtoListType = new TypeToken<List<AppointmentDto>>() {}.getType();
        return modelMapper.map(appointmentRepository.findAllByPatientUUIDOrderByDateDescStartHourAsc(patientUUID), appointmentDtoListType);
    }

    @Transactional(readOnly = true)
    public List<AppointmentDto> getAllByDoctorUUID(UUID doctorUUID) {
        var appointmentDtoListType = new TypeToken<List<AppointmentDto>>() {}.getType();
        return modelMapper.map(appointmentRepository.findAllByDoctor_DoctorUUIDOrderByDateDescStartHourAsc(doctorUUID), appointmentDtoListType);
    }

    @Transactional
    @PreAuthorize("@appointmentAccess.isOwner(#id)")
    public void delete(Long id) {
        appointmentRepository.deleteById(id);
    }

    @Transactional
    public AppointmentDto create(CreateAppointmentDto appointmentDto) {
        var doctorDto = doctorService.getDoctorDto(appointmentDto.getDoctorUUID());

        Appointment appointment = modelMapper.map(appointmentDto, Appointment.class);
        appointment.calculateAndSetEndHour();
        appointment.setDoctor(modelMapper.map(doctorDto, Doctor.class));
        validateAppointment(appointment, doctorDto.getWorkDays());

        Appointment persistedAppointment = appointmentRepository.save(appointment);
        var appointmentDetails = new AppointmentDetails();
        appointmentDetails.setAppointment(persistedAppointment);
        detailsRepository.save(appointmentDetails);

        return modelMapper.map(persistedAppointment, AppointmentDto.class);
    }

    @Transactional(readOnly = true)
    public List<FreeAppointmentDto> findFreeAppointmentsByDoctorAndBetweenDatesAndType(UUID doctorUUID, LocalDate startDate, LocalDate endDate, AppointmentType type) {
        var dateTimeNow = dateTimeNow();
        if (dateTimeNow.toLocalDate().isAfter(endDate)) {
            return Collections.emptyList();
        }
        DoctorDto doctorDto = doctorService.getDoctorDtoWithWorkdays(doctorUUID);
        List<WorkDayDto> workDays = doctorDto.getWorkDays();
        startDate = adjustDate(startDate);
        Map<LocalDate, List<Appointment>> groupedAppointments = getAppointmentsGroupedByDate(doctorUUID, startDate, endDate);

        List<FreeAppointmentDto> freeAppointments = new ArrayList<>();
        Stream.iterate(startDate, date -> date.compareTo(endDate) <= 0, date -> date.plusDays(1)).forEach(date ->
                workDays.stream()
                        .filter(workDay -> workDay.getWeekDay() == date.getDayOfWeek() && isBeforeEndTime(date, workDay.getEndTime(), type.duration))
                        .findFirst()
                        .ifPresent(workday -> {
                            var parser = new AppointmentParser(date, adjustTime(date, workday.getStartTime(), type.duration), workday.getEndTime());
                            parser.subtractAll(groupedAppointments.getOrDefault(date, Collections.emptyList()));
                            freeAppointments.addAll(parser.transformToDto(type));
                        }));

        return freeAppointments;
    }

    private LocalDate adjustDate(LocalDate date) {
        var dateNow = dateTimeNow().toLocalDate();
        if (dateNow.isAfter(date)) {
            return dateNow;
        }
        return date;
    }

    private LocalTime adjustTime(LocalDate date, LocalTime time, int duration) {
        var dateTimeNow = dateTimeNow();
        if (dateTimeNow.toLocalDate().equals(date) && dateTimeNow.toLocalTime().isAfter(time)) {
            int nextVisitMinute = (int) (Math.ceil((float) dateTimeNow.toLocalTime().getMinute() / duration) * duration);
            int delta = nextVisitMinute - dateTimeNow.toLocalTime().getMinute();
            return dateTimeNow.toLocalTime().truncatedTo(ChronoUnit.MINUTES).plusMinutes(delta);
        }
        return time;
    }

    private boolean isBeforeEndTime(LocalDate date, LocalTime endTime, int duration) {
        var dateTimeNow = dateTimeNow();
        if (dateTimeNow.toLocalDate().equals(date)) {
            return dateTimeNow.toLocalTime().isBefore(endTime.minusMinutes(duration));
        }
        return true;
    }

    private Map<LocalDate, List<Appointment>> getAppointmentsGroupedByDate(UUID doctorUUID, LocalDate startDate, LocalDate endDate) {
        List<Appointment> appointments = appointmentRepository
                .findAllByDoctor_DoctorUUIDAndDateBetween(doctorUUID, startDate, endDate, Sort.by("date"));
        return appointments.stream()
                .collect(Collectors.groupingBy(Appointment::getDate));
    }

    private void validateAppointment(Appointment appointment, List<WorkDayDto> workDays) {
        if (!isAppointmentValid(appointment, workDays))
            throw new DataInvalidException(appointment.getStartHour().toString(), "Appointment is invalid");
        if (isAppointmentTaken(appointment))
            throw new DataAlreadyExistsException(appointment.getStartHour().toString(), "Appointment already exists");
    }

    private boolean isAppointmentTaken(Appointment appointment) {
        return appointmentRepository.existsByDateAndStartHourAndDoctor_DoctorUUID(appointment.getDate(),
                appointment.getStartHour(), appointment.getDoctor().getDoctorUUID());
    }

    private boolean isAppointmentValid(Appointment appointment, List<WorkDayDto> workDays) {
        return workDays.stream()
                .filter(workday -> workday.getWeekDay() == appointment.getDate().getDayOfWeek())
                .findAny()
                .map(workday -> appointmentValidator.isValid(workday, appointment))
                .orElse(false);
    }

    private LocalDateTime dateTimeNow() {
        return LocalDateTime.now(ZoneId.of(usedTimezone));
    }
}
