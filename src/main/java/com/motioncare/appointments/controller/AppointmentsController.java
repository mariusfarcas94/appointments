package com.motioncare.appointments.controller;

import static com.motioncare.appointments.mapper.RequestMapper.convert;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.services.calendar.model.Event;
import com.motioncare.appointments.dto.AppointmentResponse;
import com.motioncare.appointments.dto.CreateAppointmentRequest;
import com.motioncare.appointments.dto.EmptySlotResponse;
import com.motioncare.appointments.service.GCIntegrationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentsController {

    private final GCIntegrationService gcIntegrationService;

    @PostMapping
    public ResponseEntity<Event> createAppointment( @RequestBody CreateAppointmentRequest request,
            @RequestParam(value = "calendarId", required = false) String calendarId,
            Authentication authentication)
            throws GeneralSecurityException, IOException {

        final String userEmail = authentication.getName();

        final Event created = gcIntegrationService.createAppointment( request, userEmail, calendarId );

        return new ResponseEntity<>( created, HttpStatus.CREATED );
    }

    @GetMapping("/empty-slots")
    public ResponseEntity<List<EmptySlotResponse>> getEmptySlots(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate,
            @RequestParam(value = "calendarId", required = false) String calendarId)
			throws GeneralSecurityException, IOException {

        final List<EmptySlotResponse> emptySlots = gcIntegrationService.getEmptySlots( startDate,
                endDate, calendarId );

        return ResponseEntity.ok(emptySlots);
    }

    @GetMapping("/my-appointments")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate,
            @RequestParam(value = "calendarId", required = false) String calendarId,
            Authentication authentication)
            throws GeneralSecurityException, IOException {

        final String userEmail = authentication.getName();

        final List<AppointmentResponse> appointments = gcIntegrationService.getMyAppointments(
                startDate,
                endDate,
                userEmail,
                calendarId );

        return ResponseEntity.ok(appointments);
    }
}
