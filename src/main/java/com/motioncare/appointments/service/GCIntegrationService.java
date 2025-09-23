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

@Service
public class GCIntegrationService {

	private static final String APPLICATION_NAME = "MotionCareAppointments";
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

	@Value("${google.calendar.serviceAccountKeyPath:}")
	private String serviceAccountKeyPath;

	@Value("${google.calendar.calendarId:primary}")
	private String calendarId;

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
}


