package com.maiia.pro.service;

import com.maiia.pro.entity.Appointment;
import com.maiia.pro.entity.Availability;
import com.maiia.pro.exception.BusinessException;
import com.maiia.pro.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ProAppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ProAvailabilityService proAvailabilityService;


    public Appointment find(String appointmentId) {
        return appointmentRepository.findById(appointmentId).orElseThrow();
    }

    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> findByPractitionerId(Integer practitionerId) {
        return appointmentRepository.findByPractitionerId(practitionerId);
    }

    @Transactional
    public Appointment createAppointment(Appointment appointment) {
        List<Availability> availabilities = this.proAvailabilityService.findByPractitionerId(appointment.getPractitionerId());

        Optional<Availability> optionalAvailability = availabilities.stream().filter(availabilityEntity -> availabilityEntity.getStartDate().isEqual(appointment.getStartDate()) && availabilityEntity.getEndDate().isEqual(appointment.getEndDate())).findFirst();

        if (optionalAvailability.isEmpty()) {
            throw new BusinessException("appointment is not available");
        }
        this.proAvailabilityService.delete(optionalAvailability.get());
        return appointmentRepository.save(appointment);
    }
}
