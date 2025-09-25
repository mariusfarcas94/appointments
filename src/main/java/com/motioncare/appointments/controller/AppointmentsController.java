package com.motioncare.appointments.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.motioncare.appointments.dto.AppointmentResponse;
import com.motioncare.appointments.dto.EmptySlotsRequest;
import com.motioncare.appointments.dto.EmptySlotResponse;
import com.motioncare.appointments.dto.MyAppointmentsRequest;
import com.motioncare.appointments.service.GCIntegrationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentsController {

    private final GCIntegrationService gcIntegrationService;

    /**
     * Get all empty slots in the calendar between working hours for a given period
     */
    @PostMapping("/empty-slots")
    public ResponseEntity<List<EmptySlotResponse>> getEmptySlots(final @RequestBody EmptySlotsRequest request)
            throws GeneralSecurityException, IOException {

        final List<EmptySlotResponse> emptySlots = gcIntegrationService.getEmptySlots(
                request.getStartDate(),
                request.getEndDate(),
                request.getWorkingHoursStart(),
                request.getWorkingHoursEnd(),
                request.getCalendarId()
        );

        return ResponseEntity.ok(emptySlots);
    }

    /**
     * Get all appointments where the current user is an attendee
     */
    @PostMapping("/my-appointments")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(
            final @RequestBody MyAppointmentsRequest request,
            final Authentication authentication)
            throws GeneralSecurityException, IOException {

        final String userEmail = authentication.getName();

        final List<AppointmentResponse> appointments = gcIntegrationService.getMyAppointments(
                request.getStartDate(),
                request.getEndDate(),
                userEmail,
                request.getCalendarId()
        );

        return ResponseEntity.ok(appointments);
    }

    /**
     * Alternative endpoint for getting empty slots with query parameters
     */
    @GetMapping("/empty-slots")
    public ResponseEntity<List<EmptySlotResponse>> getEmptySlotsWithParams(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("workingHoursStart") String workingHoursStart,
            @RequestParam("workingHoursEnd") String workingHoursEnd,
            @RequestParam(value = "minimumSlotDurationMinutes", required = false) Integer minimumSlotDurationMinutes,
            @RequestParam(value = "calendarId", required = false) String calendarId)
            throws GeneralSecurityException, IOException {

        final EmptySlotsRequest request = EmptySlotsRequest.builder()
                .startDate(LocalDate.parse(startDate))
                .endDate(LocalDate.parse(endDate))
                .workingHoursStart(LocalTime.parse(workingHoursStart))
                .workingHoursEnd(LocalTime.parse(workingHoursEnd))
                .calendarId(calendarId)
                .build();

        return getEmptySlots(request);
    }

    /**
     * Alternative endpoint for getting my appointments with query parameters
     */
    @GetMapping("/my-appointments")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointmentsWithParams(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam(value = "calendarId", required = false) String calendarId,
            Authentication authentication)
            throws GeneralSecurityException, IOException {

        MyAppointmentsRequest request = MyAppointmentsRequest.builder()
                .startDate(java.time.LocalDate.parse(startDate))
                .endDate(java.time.LocalDate.parse(endDate))
                .calendarId(calendarId)
                .build();

        return getMyAppointments(request, authentication);
    }
}
