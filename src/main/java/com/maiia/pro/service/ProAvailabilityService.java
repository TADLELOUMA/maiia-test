package com.maiia.pro.service;

import com.maiia.pro.entity.Appointment;
import com.maiia.pro.entity.Availability;
import com.maiia.pro.entity.TimeSlot;
import com.maiia.pro.repository.AppointmentRepository;
import com.maiia.pro.repository.AvailabilityRepository;
import com.maiia.pro.repository.TimeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProAvailabilityService {

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    public List<Availability> findByPractitionerId(Integer practitionerId) {
        return availabilityRepository.findByPractitionerId(practitionerId);
    }

    public List<Availability> generateAvailabilities(Integer practitionerId) {

        List<Availability> availabilities = new ArrayList<>();
        List<TimeSlot> timeSlots = timeSlotRepository.findByPractitionerId(practitionerId);
        List<Appointment> appointments = appointmentRepository.findByPractitionerId(practitionerId);
        List<Availability> existing = availabilityRepository.findByPractitionerId(practitionerId);

        List<Availability> allOccupiedSlots = new ArrayList<>();

        allOccupiedSlots.addAll(appointments.stream()
                .map(app -> new Availability(app.getId(), practitionerId, app.getStartDate(), app.getEndDate()))
                .collect(Collectors.toList()));
        allOccupiedSlots.addAll(existing);

        allOccupiedSlots.sort(Comparator.comparing(Availability::getStartDate));

        for (TimeSlot timeSlot : timeSlots) {
            availabilities.addAll(calculateAvailableSlots(practitionerId, timeSlot, allOccupiedSlots));
        }

        availabilityRepository.saveAll(availabilities);
        availabilities.addAll(existing);
        return availabilities;
    }

    private List<Availability> calculateAvailableSlots(Integer practitionerId, TimeSlot timeSlot, List<Availability> allOccupiedSlots) {
        long SLOT_DURATION = 15;
        LocalDateTime slotStart = timeSlot.getStartDate();
        LocalDateTime slotEnd = timeSlot.getStartDate().plusMinutes(SLOT_DURATION);
        List<Availability> availabilities = new ArrayList<>();

        long minutesBetween = Duration.between(timeSlot.getStartDate(), timeSlot.getEndDate()).toMinutes();
        long maxAvailability = (minutesBetween/SLOT_DURATION) + (minutesBetween%SLOT_DURATION == 0 ? 0 : 1);

        for (int i= 0; i < maxAvailability; i++) {
            Optional<Availability> possibleConflictAvailability = this.findOverlappingAvailability(allOccupiedSlots, slotStart, slotEnd);

            if (possibleConflictAvailability.isEmpty()) {
                Availability availability = new Availability();
                availability.setPractitionerId(practitionerId);
                availability.setStartDate(slotStart);
                availability.setEndDate(slotEnd);
                availabilities.add(availability);
                slotStart = slotEnd;
                slotEnd= generateNextDate(slotEnd, timeSlot.getEndDate(), SLOT_DURATION);

            } else {
                slotStart = possibleConflictAvailability.get().getEndDate();
                slotEnd = generateNextDate(possibleConflictAvailability.get().getEndDate(), timeSlot.getEndDate(), SLOT_DURATION);
            }
        }
        return availabilities;
    }

    private Optional<Availability> findOverlappingAvailability(List<Availability> allOccupiedSlots, LocalDateTime slotStart, LocalDateTime slotEnd) {
        return allOccupiedSlots.stream()
                .filter(slot -> slot.getStartDate().isBefore(slotEnd) && slot.getEndDate().isAfter(slotStart))
                .findFirst();
    }

    private LocalDateTime generateNextDate(LocalDateTime currentDate, LocalDateTime endDate, long slotDuration) {
        if (currentDate.plusMinutes(slotDuration).isAfter(endDate)) {
            long minuteBetween = Duration.between(currentDate, endDate).toMinutes();
            return currentDate.plusMinutes(minuteBetween);
        }
        return currentDate.plusMinutes(slotDuration);
    }

    public void delete(Availability availability) {
        this.availabilityRepository.delete(availability);
    }
}
