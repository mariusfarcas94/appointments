package com.motioncare.appointments.mapper;

import java.time.ZoneId;
import java.util.Date;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.motioncare.appointments.dto.CreateAppointmentRequest;

public class RequestMapper {

	private RequestMapper() {}

	public static Event convert( CreateAppointmentRequest request ) {
		final Event event = new Event();
		event.setSummary( "[" + request.getTherapistName() + "] " + request.getService() );

		if ( request.getStart() != null ) {
			final EventDateTime start = new EventDateTime();
			start.setDateTime( new DateTime( Date.from(request.getStart().atZone( ZoneId.systemDefault()).toInstant()) ));
			start.setTimeZone( ZoneId.systemDefault().getId() );
			event.setStart( start );
		}

		if ( request.getEnd() != null ) {
			final EventDateTime end = new EventDateTime();
			end.setDateTime( new DateTime( Date.from(request.getEnd().atZone(ZoneId.systemDefault()).toInstant()) ));
			end.setTimeZone( ZoneId.systemDefault().getId() );
			event.setEnd( end );
		}

		return event;
	}
}
