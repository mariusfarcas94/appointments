package com.motioncare.appointments.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventRequest {

	private String summary;

	private String description;

	private EventDateTime start;

	private EventDateTime end;

	private Organizer organizer;

	private List<Attendee> attendees;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class EventDateTime {
		@JsonProperty("dateTime")
		private String dateTime;

		@JsonProperty("timeZone")
		private String timeZone;
	}

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
