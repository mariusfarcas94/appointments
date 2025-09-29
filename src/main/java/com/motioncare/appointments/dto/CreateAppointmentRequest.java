package com.motioncare.appointments.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentRequest {

	private String therapistName;

	private String therapistEmail;

	private String service;

	private LocalDateTime start;

	private LocalDateTime end;

}
