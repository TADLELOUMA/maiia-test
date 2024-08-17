package com.maiia.pro.service;

import com.maiia.pro.EntityFactory;
import com.maiia.pro.entity.Appointment;
import com.maiia.pro.entity.Availability;
import com.maiia.pro.entity.Practitioner;
import com.maiia.pro.exception.BusinessException;
import com.maiia.pro.repository.AppointmentRepository;
import com.maiia.pro.repository.AvailabilityRepository;
import com.maiia.pro.repository.PractitionerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProAppointmentServiceTest {
    private final EntityFactory entityFactory = new EntityFactory();
    private  final static Integer patient_id=657679;
    @Autowired
    private ProAvailabilityService proAvailabilityService;

    @Autowired
    private PractitionerRepository practitionerRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ProAppointmentService proAppointmentService;


    @Test
    void createAppointment() {

        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        LocalDateTime startDate = LocalDateTime.of(2020, Month.FEBRUARY, 5, 11, 0, 0);

        availabilityRepository.save(Availability.builder().practitionerId(practitioner.getId()).startDate(startDate).endDate(startDate.plusMinutes(15)).build());
        availabilityRepository.save(Availability.builder().practitionerId(practitioner.getId()).startDate(startDate.plusMinutes(15)).endDate(startDate.plusMinutes(30)).build());
        availabilityRepository.save(Availability.builder().practitionerId(practitioner.getId()).startDate(startDate.plusMinutes(35)).endDate(startDate.plusMinutes(45)).build());
        availabilityRepository.save(Availability.builder().practitionerId(practitioner.getId()).startDate(startDate.plusMinutes(45)).endDate(startDate.plusHours(1)).build());

        Appointment appointment = Appointment.builder().practitionerId(practitioner.getId()).patientId(patient_id).startDate(startDate).endDate(startDate.plusMinutes(15)).build();

        Appointment actualAppointment = proAppointmentService.createAppointment(appointment);

        assertEquals(appointment.getId(), actualAppointment.getId());
        assertEquals(appointment.getPractitionerId(), actualAppointment.getPractitionerId());
        assertEquals(appointment.getPatientId(), actualAppointment.getPatientId());
        assertEquals(appointment.getStartDate(), actualAppointment.getStartDate());
        assertEquals(appointment.getEndDate(), actualAppointment.getEndDate());

        List<Availability> availabilities = this.availabilityRepository.findByPractitionerId(practitioner.getId());
        assertEquals(3, availabilities.size());

        List<Appointment> appointments = this.appointmentRepository.findByPractitionerId(practitioner.getId());
        assertTrue(appointments.contains(actualAppointment));

    }

    @Test
    void createAppointmentFailedWhenPractitionerIsInvalid() {

        LocalDateTime startDate = LocalDateTime.of(2020, Month.FEBRUARY, 5, 11, 0, 0);

        Appointment appointment = Appointment.builder().practitionerId(0).patientId(patient_id).startDate(startDate).endDate(startDate.plusMinutes(15)).build();


        BusinessException exception = assertThrows(BusinessException.class, () -> {
            proAppointmentService.createAppointment(appointment);
        });

        assertEquals("appointment is not available", exception.getMessage());

    }

    @Test
    void createAppointmentFailedWhenAppointmentIsInvalid() {

        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        LocalDateTime startDate = LocalDateTime.of(2020, Month.FEBRUARY, 5, 11, 0, 0);

        availabilityRepository.save(Availability.builder().practitionerId(practitioner.getId()).startDate(startDate).endDate(startDate.plusMinutes(15)).build());
        availabilityRepository.save(Availability.builder().practitionerId(practitioner.getId()).startDate(startDate.plusMinutes(15)).endDate(startDate.plusMinutes(30)).build());
        availabilityRepository.save(Availability.builder().practitionerId(practitioner.getId()).startDate(startDate.plusMinutes(35)).endDate(startDate.plusMinutes(45)).build());
        availabilityRepository.save(Availability.builder().practitionerId(practitioner.getId()).startDate(startDate.plusMinutes(45)).endDate(startDate.plusHours(1)).build());

        // endDate is invalid
        Appointment appointment = Appointment.builder().practitionerId(practitioner.getId()).patientId(patient_id).startDate(startDate).endDate(startDate.plusMinutes(20)).build();


        BusinessException exception = assertThrows(BusinessException.class, () -> {
            proAppointmentService.createAppointment(appointment);
        });

        assertEquals("appointment is not available", exception.getMessage());

    }
}