package com.motioncare.appointments.controller;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.motioncare.appointments.dto.CreateEventRequest;
import com.motioncare.appointments.service.GCIntegrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalendarEventsControllerTest {

    @Mock
    private GCIntegrationService gcIntegrationService;

    @InjectMocks
    private CalendarEventsController calendarEventsController;

    private Event testEvent;
    private Events testEvents;
    private CreateEventRequest createEventRequest;

    @BeforeEach
    void setUp() {
        testEvent = new Event()
                .setId("1")
                .setSummary("Test Event");
        
        testEvents = new Events()
                .setItems(List.of(testEvent));

        createEventRequest = CreateEventRequest.builder()
                .summary("Test Event")
                .build();
    }

    @Test
    void listEvents_ShouldReturnEvents_WhenValidRequest() throws Exception {
        // Given
        doReturn(testEvents).when(gcIntegrationService).listEvents(
                (Integer) isNull(), 
                (com.google.api.client.util.DateTime) isNull(), 
                (com.google.api.client.util.DateTime) isNull(), 
                (String) isNull());

        // When
        ResponseEntity<Events> response = calendarEventsController.listEvents(null, null, null, null, null);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getItems().size());
        assertEquals("1", response.getBody().getItems().get(0).getId());
        assertEquals("Test Event", response.getBody().getItems().get(0).getSummary());
    }

    @Test
    void getEvent_ShouldReturnEvent_WhenValidEventId() throws Exception {
        // Given
        when(gcIntegrationService.getEvent(eq("1"))).thenReturn(testEvent);

        // When
        ResponseEntity<Event> response = calendarEventsController.getEvent("1", null);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("1", response.getBody().getId());
        assertEquals("Test Event", response.getBody().getSummary());
    }

    @Test
    void createEvent_ShouldCreateEvent_WhenValidRequest() throws Exception {
        // Given
        Event created = new Event().setId("42").setSummary("New Event");
        when(gcIntegrationService.createEvent(any(Event.class))).thenReturn(created);

        // When
        ResponseEntity<Event> response = calendarEventsController.createEvent(createEventRequest, null);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("42", response.getBody().getId());
        assertEquals("New Event", response.getBody().getSummary());
    }

    @Test
    void updateEvent_ShouldUpdateEvent_WhenValidRequest() throws Exception {
        // Given
        Event updated = new Event().setId("1").setSummary("Updated Event");
        when(gcIntegrationService.updateEvent(eq("1"), any(Event.class))).thenReturn(updated);

        // When
        ResponseEntity<Event> response = calendarEventsController.updateEvent("1", updated, null);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("1", response.getBody().getId());
        assertEquals("Updated Event", response.getBody().getSummary());
    }

    @Test
    void deleteEvent_ShouldDeleteEvent_WhenValidEventId() throws Exception {
        // Given
        doNothing().when(gcIntegrationService).deleteEvent(eq("1"));

        // When
        ResponseEntity<Void> response = calendarEventsController.deleteEvent("1", null);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(gcIntegrationService).deleteEvent("1");
    }
}


