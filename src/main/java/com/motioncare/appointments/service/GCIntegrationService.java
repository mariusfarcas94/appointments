package com.motioncare.appointments.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.motioncare.appointments.dto.EmptySlotResponse;
import com.motioncare.appointments.dto.AppointmentResponse;

@Service
public class GCIntegrationService {

	private static final String APPLICATION_NAME = "MotionCareAppointments";
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

	@Value("${google.calendar.serviceAccountKeyPath:}")
	private String serviceAccountKeyPath;

	@Value("${google.calendar.calendarId:primary}")
	private String calendarId;

	@Value("${appointments.default.working-hours.start:09:00}")
	private String defaultWorkingHoursStart;

	@Value("${appointments.default.working-hours.end:17:00}")
	private String defaultWorkingHoursEnd;


	private Calendar calendarClient;

	public Calendar getCalendarClient() throws GeneralSecurityException, IOException {

		if ( calendarClient == null ) {
			calendarClient = initializeCalendarClient();
		}

		return calendarClient;
	}

	// Visible for testing
	void setCalendarClientForTesting( final Calendar testClient ) {
		this.calendarClient = testClient;
	}

	// Visible for testing
	void setCalendarIdForTesting( final String calendarId ) {
		this.calendarId = calendarId;
	}

	private Calendar initializeCalendarClient() throws GeneralSecurityException, IOException {

		GoogleCredentials credentials;

		if ( serviceAccountKeyPath != null && !serviceAccountKeyPath.isBlank() ) {
			try ( FileInputStream serviceAccountStream = new FileInputStream(
					serviceAccountKeyPath ) ) {
				credentials = GoogleCredentials.fromStream( serviceAccountStream )
						.createScoped( Arrays.asList( CalendarScopes.CALENDAR,
								CalendarScopes.CALENDAR_EVENTS ) );
			}
		} else {
			credentials = GoogleCredentials.getApplicationDefault()
					.createScoped( Arrays.asList( CalendarScopes.CALENDAR,
							CalendarScopes.CALENDAR_EVENTS ) );
		}

		final HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter( credentials );

		return new Calendar.Builder( GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY,
				requestInitializer ).setApplicationName( APPLICATION_NAME ).build();
	}

	public Events listEvents( final Integer maxResults, final DateTime timeMin, final DateTime timeMax )
			throws GeneralSecurityException, IOException {
		return listEvents( calendarId, maxResults, timeMin, timeMax );
	}

	public Events listEvents( final Integer maxResults, final DateTime timeMin, final DateTime timeMax, final String query )
			throws GeneralSecurityException, IOException {
		return listEvents( calendarId, maxResults, timeMin, timeMax, query );
	}

	public Events listEvents( final String targetCalendarId, final Integer maxResults, final DateTime timeMin,
			final DateTime timeMax ) throws GeneralSecurityException, IOException {
		return listEvents( targetCalendarId, maxResults, timeMin, timeMax, null );
	}

	public Events listEvents( final String targetCalendarId, final Integer maxResults, final DateTime timeMin,
			final DateTime timeMax, final String query ) throws GeneralSecurityException, IOException {

		final Calendar.Events.List request = getCalendarClient().events()
				.list( targetCalendarId == null || targetCalendarId.isBlank() ? calendarId : targetCalendarId )
				.setSingleEvents( true )
				.setOrderBy( "startTime" );

		if ( maxResults != null && maxResults > 0 ) {
			request.setMaxResults( maxResults );
		}

		if ( timeMin != null ) {
			request.setTimeMin( timeMin );
		}

		if ( timeMax != null ) {
			request.setTimeMax( timeMax );
		}

		if ( query != null && !query.isBlank() ) {
			request.setQ( query );
		}

		request.setShowDeleted( false );
		request.setShowHiddenInvitations( false );

		return request.execute();
	}

	public Event getEvent( final String eventId ) throws GeneralSecurityException, IOException {
		return getEvent( calendarId, eventId );
	}

	public Event getEvent( final String targetCalendarId, final String eventId )
			throws GeneralSecurityException, IOException {

		return getCalendarClient().events()
				.get( targetCalendarId == null || targetCalendarId.isBlank() ? calendarId : targetCalendarId,
						eventId )
				.execute();
	}

	public Event createEvent( final Event event ) throws GeneralSecurityException, IOException {
		return createEvent( calendarId, event );
	}

	public Event createEvent( final String targetCalendarId, Event event )
			throws GeneralSecurityException, IOException {

		return getCalendarClient().events()
				.insert(
						targetCalendarId == null || targetCalendarId.isBlank() ? calendarId : targetCalendarId,
						event )
				.setSendUpdates( "all" )
				.execute();
	}

	public Event updateEvent( final String eventId, final Event updatedEvent )
			throws GeneralSecurityException, IOException {
		return updateEvent( calendarId, eventId, updatedEvent );
	}

	public Event updateEvent( final String targetCalendarId, final String eventId, final Event updatedEvent )
			throws GeneralSecurityException, IOException {

		return getCalendarClient().events()
				.update(
						targetCalendarId == null || targetCalendarId.isBlank() ? calendarId : targetCalendarId,
						eventId, updatedEvent )
				.execute();
	}

	public void deleteEvent( final String eventId ) throws GeneralSecurityException, IOException {
		deleteEvent( calendarId, eventId );
	}

	public void deleteEvent( final String targetCalendarId, final String eventId )
			throws GeneralSecurityException, IOException {

		getCalendarClient().events()
				.delete(
						targetCalendarId == null || targetCalendarId.isBlank() ? calendarId : targetCalendarId,
						eventId )
				.execute();
	}

	/**
	 * Get empty slots in the calendar between working hours for a given period
	 * Finds gaps between existing events within working hours
	 */
	public List<EmptySlotResponse> getEmptySlots(final LocalDate startDate, final LocalDate endDate,
			final LocalTime workingHoursStart, final LocalTime workingHoursEnd, String targetCalendarId)
			throws GeneralSecurityException, IOException {
		
		List<EmptySlotResponse> emptySlots = new ArrayList<>();
		String calendarIdToUse = targetCalendarId != null && !targetCalendarId.isBlank() ? targetCalendarId : this.calendarId;
		
		// Use default values if not provided
		LocalTime effectiveWorkingHoursStart = workingHoursStart != null ? 
			workingHoursStart : LocalTime.parse(defaultWorkingHoursStart);
		LocalTime effectiveWorkingHoursEnd = workingHoursEnd != null ? 
			workingHoursEnd : LocalTime.parse(defaultWorkingHoursEnd);
		
		// Get all events in the date range
		final DateTime timeMin = new DateTime(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
		final DateTime timeMax = new DateTime(endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
		
		final Events events = listEvents(calendarIdToUse, null, timeMin, timeMax);
		
		// Process each day in the date range
		LocalDate currentDate = startDate;
		while (!currentDate.isAfter(endDate)) {
			// Get events for this specific day
			List<Event> dayEvents = getEventsForDay(events, currentDate);
			
			// Find gaps between events within working hours
			List<EmptySlotResponse> dayEmptySlots = findGapsBetweenEvents(
					currentDate, dayEvents, effectiveWorkingHoursStart, effectiveWorkingHoursEnd);
			
			emptySlots.addAll(dayEmptySlots);
			currentDate = currentDate.plusDays(1);
		}
		
		return emptySlots;
	}

	/**
	 * Get appointments where the user is an attendee
	 */
	public List<AppointmentResponse> getMyAppointments(final LocalDate startDate, final LocalDate endDate, 
			final String userEmail, final String targetCalendarId) throws GeneralSecurityException, IOException {
		
		final String calendarIdToUse = targetCalendarId != null && !targetCalendarId.isBlank() ? targetCalendarId : this.calendarId;
		
		// Get events in the date range filtered by attendee
		final DateTime timeMin = new DateTime(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
		final DateTime timeMax = new DateTime(endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
		
		// Use Google Calendar API q parameter to search for events where the user is an attendee
		// The q parameter searches across multiple fields including attendees' email addresses
		final Events events = listEvents(calendarIdToUse, null, timeMin, timeMax, userEmail);
		
		return events != null && events.getItems() != null ? events.getItems().stream()
					.map(this::convertEventToAppointmentResponse)
					.toList() : List.of();
	}

	/**
	 * Get events for a specific day from the events list
	 */
	private List<Event> getEventsForDay(final Events events, final LocalDate date) {
		List<Event> dayEvents = new ArrayList<>();
		
		if (events.getItems() != null) {
			for (Event event : events.getItems()) {
				if (isEventOnDate(event, date)) {
					dayEvents.add(event);
				}
			}
		}
		
		// Sort events by start time
		dayEvents.sort((e1, e2) -> {
			LocalDateTime start1 = getEventStartTime(e1);
			LocalDateTime start2 = getEventStartTime(e2);
			return start1.compareTo(start2);
		});
		
		return dayEvents;
	}

	/**
	 * Check if an event occurs on the specified date
	 */
	private boolean isEventOnDate(final Event event, final LocalDate date) {
		if (event.getStart() == null) {
			return false;
		}
		
		LocalDateTime eventStart = getEventStartTime(event);
		return eventStart.toLocalDate().equals(date);
	}

	/**
	 * Get the start time of an event as LocalDateTime
	 */
	private LocalDateTime getEventStartTime(final Event event) {
		if (event.getStart() == null) {
			return null;
		}
		
		String timeString = event.getStart().getDateTime() != null ? 
			event.getStart().getDateTime().toStringRfc3339() :
			event.getStart().getDate().toStringRfc3339();
		
		// Parse the RFC3339 datetime string with timezone and convert to LocalDateTime
		return java.time.OffsetDateTime.parse(timeString)
			.atZoneSameInstant(ZoneId.systemDefault())
			.toLocalDateTime();
	}

	/**
	 * Get the end time of an event as LocalDateTime
	 */
	private LocalDateTime getEventEndTime(final Event event) {
		if (event.getEnd() == null) {
			return null;
		}
		
		String timeString = event.getEnd().getDateTime() != null ? 
			event.getEnd().getDateTime().toStringRfc3339() :
			event.getEnd().getDate().toStringRfc3339();
		
		// Parse the RFC3339 datetime string with timezone and convert to LocalDateTime
		return java.time.OffsetDateTime.parse(timeString)
			.atZoneSameInstant(ZoneId.systemDefault())
			.toLocalDateTime();
	}

	/**
	 * Find gaps between events within working hours for a specific day
	 */
	private List<EmptySlotResponse> findGapsBetweenEvents(final LocalDate date, final List<Event> dayEvents,
			final LocalTime workingHoursStart, final LocalTime workingHoursEnd) {
		
		List<EmptySlotResponse> gaps = new ArrayList<>();
		
		final LocalDateTime dayStart = date.atTime(workingHoursStart);
		final LocalDateTime dayEnd = date.atTime(workingHoursEnd);
		
		// If no events, the entire working day is available as one gap
		if (dayEvents.isEmpty()) {
			long durationMinutes = Duration.between(dayStart, dayEnd).toMinutes();
			gaps.add(EmptySlotResponse.builder()
					.startTime(dayStart)
					.endTime(dayEnd)
					.durationMinutes((int) durationMinutes)
					.build());
			return gaps;
		}
		
		// Check gap before first event
		final LocalDateTime firstEventStart = getEventStartTime(dayEvents.get(0));
		if (firstEventStart.isAfter(dayStart)) {
			long durationMinutes = Duration.between(dayStart, firstEventStart).toMinutes();
			gaps.add(EmptySlotResponse.builder()
					.startTime(dayStart)
					.endTime(firstEventStart)
					.durationMinutes((int) durationMinutes)
					.build());
		}
		
		// Check gaps between events
		for (int i = 0; i < dayEvents.size() - 1; i++) {
			final LocalDateTime currentEventEnd = getEventEndTime(dayEvents.get(i));
			final LocalDateTime nextEventStart = getEventStartTime(dayEvents.get(i + 1));
			
			if (currentEventEnd.isBefore(nextEventStart)) {
				long durationMinutes = Duration.between(currentEventEnd, nextEventStart).toMinutes();
				gaps.add(EmptySlotResponse.builder()
						.startTime(currentEventEnd)
						.endTime(nextEventStart)
						.durationMinutes((int) durationMinutes)
						.build());
			}
		}
		
		// Check gap after last event
		LocalDateTime lastEventEnd = getEventEndTime(dayEvents.get(dayEvents.size() - 1));
		if (lastEventEnd.isBefore(dayEnd)) {
			long durationMinutes = java.time.Duration.between(lastEventEnd, dayEnd).toMinutes();
			gaps.add(EmptySlotResponse.builder()
					.startTime(lastEventEnd)
					.endTime(dayEnd)
					.durationMinutes((int) durationMinutes)
					.build());
		}
		
		return gaps;
	}

	private AppointmentResponse convertEventToAppointmentResponse(final Event event) {
		AppointmentResponse.AppointmentResponseBuilder builder = AppointmentResponse.builder()
				.id(event.getId())
				.summary(event.getSummary())
				.description(event.getDescription())
				.status(event.getStatus())
				.location(event.getLocation())
				.htmlLink(event.getHtmlLink());
		
		// Set start and end times
		if (event.getStart() != null) {
			if (event.getStart().getDateTime() != null) {
				LocalDateTime startTime = java.time.OffsetDateTime.parse(
					event.getStart().getDateTime().toStringRfc3339()
				).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
				builder.startTime(startTime);
			} else if (event.getStart().getDate() != null) {
				LocalDateTime startTime = java.time.OffsetDateTime.parse(
					event.getStart().getDate().toStringRfc3339()
				).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
				builder.startTime(startTime);
			}
		}
		
		if (event.getEnd() != null) {
			if (event.getEnd().getDateTime() != null) {
				LocalDateTime endTime = java.time.OffsetDateTime.parse(
					event.getEnd().getDateTime().toStringRfc3339()
				).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
				builder.endTime(endTime);
			} else if (event.getEnd().getDate() != null) {
				LocalDateTime endTime = java.time.OffsetDateTime.parse(
					event.getEnd().getDate().toStringRfc3339()
				).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
				builder.endTime(endTime);
			}
		}
		
		// Set organizer
		if (event.getOrganizer() != null) {
			AppointmentResponse.Organizer organizer = AppointmentResponse.Organizer.builder()
					.email(event.getOrganizer().getEmail())
					.displayName(event.getOrganizer().getDisplayName())
					.build();
			builder.organizer(organizer);
		}
		
		// Set attendees
		if (event.getAttendees() != null) {
			List<AppointmentResponse.Attendee> attendees = event.getAttendees().stream()
					.map(attendee -> AppointmentResponse.Attendee.builder()
							.email(attendee.getEmail())
							.displayName(attendee.getDisplayName())
							.responseStatus(attendee.getResponseStatus())
							.build())
					.toList();
			builder.attendees(attendees);
		}
		
		return builder.build();
	}
}


