package com.motioncare.appointments.mapper;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.motioncare.appointments.dto.CreateEventRequest;

public class RequestMapper {

	private RequestMapper() {}

	public static Event convert( CreateEventRequest request ) {
		final Event event = new Event();
		event.setSummary( request.getSummary() );
		event.setDescription( request.getDescription() );

		if ( request.getStart() != null ) {
			final EventDateTime start = new EventDateTime();
			start.setDateTime( new DateTime( request.getStart().getDateTime() ) );

			if ( request.getStart().getTimeZone() != null ) {
				start.setTimeZone( request.getStart().getTimeZone() );
			}

			event.setStart( start );
		}

		if ( request.getEnd() != null ) {
			final EventDateTime end = new EventDateTime();
			end.setDateTime( new DateTime( request.getEnd().getDateTime() ) );

			if ( request.getEnd().getTimeZone() != null ) {
				end.setTimeZone( request.getEnd().getTimeZone() );
			}

			event.setEnd( end );
		}

		if ( request.getAttendees() != null ) {
			event.setAttendees(
					request.getAttendees().stream().map( RequestMapper::convert ).toList() );
		}

		return event;
	}

	private static EventAttendee convert( final CreateEventRequest.Attendee attendee ) {
		EventAttendee eventAttendee = new EventAttendee();
		eventAttendee.setEmail( attendee.getEmail() );
		eventAttendee.setDisplayName( attendee.getDisplayName() );

		if ( attendee.getResponseStatus() != null ) {
			eventAttendee.setResponseStatus( attendee.getResponseStatus() );
		}

		return eventAttendee;
	}
}
