package com.motioncare.appointments.controller;

import static com.motioncare.appointments.mapper.RequestMapper.convert;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.motioncare.appointments.dto.CreateEventRequest;
import com.motioncare.appointments.service.GCIntegrationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarEventsController {

	private final GCIntegrationService gcIntegrationService;

	@GetMapping("/events")
	public ResponseEntity<Events> listEvents(
			@RequestParam(value = "calendarId", required = false) String calendarId,
			@RequestParam(value = "maxResults", required = false) Integer maxResults,
			@RequestParam(value = "timeMin", required = false) String timeMin,
			@RequestParam(value = "timeMax", required = false) String timeMax,
			@RequestParam(value = "q", required = false) String query )
			throws GeneralSecurityException, IOException {

		final DateTime min = timeMin != null && !timeMin.isBlank() ? new DateTime( timeMin ) : null;
		final DateTime max = timeMax != null && !timeMax.isBlank() ? new DateTime( timeMax ) : null;

		final Events events = ( calendarId == null || calendarId.isBlank() ) ?
				gcIntegrationService.listEvents( maxResults, min, max, query ) :
				gcIntegrationService.listEvents( calendarId, maxResults, min, max, query );

		return ResponseEntity.ok( events );
	}

	@GetMapping("/events/{eventId}")
	public ResponseEntity<Event> getEvent( @PathVariable("eventId") String eventId,
			@RequestParam(value = "calendarId", required = false) String calendarId )
			throws GeneralSecurityException, IOException {

		final Event event = ( calendarId == null || calendarId.isBlank() ) ?
				gcIntegrationService.getEvent( eventId ) :
				gcIntegrationService.getEvent( calendarId, eventId );

		return ResponseEntity.ok( event );
	}

	@PutMapping("/events/{eventId}")
	public ResponseEntity<Event> updateEvent( @PathVariable("eventId") String eventId,
			@RequestBody Event updatedEvent,
			@RequestParam(value = "calendarId", required = false) String calendarId )
			throws GeneralSecurityException, IOException {

		final Event result = ( calendarId == null || calendarId.isBlank() ) ?
				gcIntegrationService.updateEvent( eventId, updatedEvent ) :
				gcIntegrationService.updateEvent( calendarId, eventId,
				updatedEvent );

		return ResponseEntity.ok( result );
	}

	@DeleteMapping("/events/{eventId}")
	public ResponseEntity<Void> deleteEvent( @PathVariable("eventId") String eventId,
			@RequestParam(value = "calendarId", required = false) String calendarId )
			throws GeneralSecurityException, IOException {

		if ( calendarId == null || calendarId.isBlank() ) {
			gcIntegrationService.deleteEvent( eventId );
		} else {
			gcIntegrationService.deleteEvent( calendarId, eventId );
		}
		return ResponseEntity.noContent().build();
	}
}


