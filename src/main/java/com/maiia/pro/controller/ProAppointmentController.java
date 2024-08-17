package com.maiia.pro.controller;

import com.maiia.pro.controller.dto.AppointmentDto;
import com.maiia.pro.entity.Appointment;
import com.maiia.pro.service.ProAppointmentService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/appointments", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProAppointmentController {
    @Autowired
    private ProAppointmentService proAppointmentService;


    @ApiOperation(value = "Get appointments by practitionerId")
    @GetMapping("/{practitionerId}")
    public List<Appointment> getAppointmentsByPractitioner(@PathVariable final Integer practitionerId) {
        return proAppointmentService.findByPractitionerId(practitionerId);
    }

    @ApiOperation(value = "Get all appointments")
    @GetMapping
    public List<Appointment> getAppointments() {
        return proAppointmentService.findAll();
    }

    @ApiOperation(value = "Create an appointment")
    @PostMapping
    public Appointment createAppointment(@RequestBody AppointmentDto appointmentDto) {
        Appointment appointment = Appointment.builder()
                .practitionerId(appointmentDto.getPractitionerId())
                .patientId(appointmentDto.getPatientId())
                .startDate(appointmentDto.getStartDate())
                .endDate(appointmentDto.getEndDate())
                .build();

        return proAppointmentService.createAppointment(appointment);
    }
}