package com.motioncare.appointments.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {

    private String id;
    private String summary;
    private String description;
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    private String timeZone;
    private Organizer organizer;
    private List<Attendee> attendees;
    private String location;
    private String htmlLink;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Organizer {
        private String email;
        private String displayName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Attendee {
        private String email;
        private String displayName;
        private String responseStatus;
    }
}
