package com.motioncare.appointments.service;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GCIntegrationServiceTest {

    @Mock
    private Calendar mockCalendar;

    private GCIntegrationService service;

    @BeforeEach
    void setUp() {
        service = new GCIntegrationService();
        service.setCalendarClientForTesting(mockCalendar);
        service.setCalendarIdForTesting("test-calendar");
    }

    @Test
    void listEvents_ShouldReturnEvents_WhenValidRequest() throws GeneralSecurityException, IOException {
        // Given
        Events expected = new Events();
        Calendar.Events events = mock(Calendar.Events.class);
        Calendar.Events.List list = mock(Calendar.Events.List.class);
        
        when(mockCalendar.events()).thenReturn(events);
        when(events.list(anyString())).thenReturn(list);
        when(list.setSingleEvents(true)).thenReturn(list);
        when(list.setOrderBy("startTime")).thenReturn(list);
        when(list.execute()).thenReturn(expected);

        // When
        Events result = service.listEvents(null, null, null);

        // Then
        assertNotNull(result);
        verify(events).list(anyString());
        verify(list).setSingleEvents(true);
        verify(list).setOrderBy("startTime");
        verify(list).execute();
    }

    @Test
    void getEvent_ShouldReturnEvent_WhenValidEventId() throws GeneralSecurityException, IOException {
        // Given
        Event expected = new Event();
        Calendar.Events events = mock(Calendar.Events.class);
        Calendar.Events.Get get = mock(Calendar.Events.Get.class);
        
        when(mockCalendar.events()).thenReturn(events);
        when(events.get(anyString(), anyString())).thenReturn(get);
        when(get.execute()).thenReturn(expected);

        // When
        Event result = service.getEvent("event-123");

        // Then
        assertNotNull(result);
        verify(events).get("test-calendar", "event-123");
        verify(get).execute();
    }

    @Test
    void createEvent_ShouldCreateEvent_WhenValidEvent() throws GeneralSecurityException, IOException {
        // Given
        Event created = new Event().setId("1");
        Calendar.Events events = mock(Calendar.Events.class);
        Calendar.Events.Insert insert = mock(Calendar.Events.Insert.class);
        
        when(mockCalendar.events()).thenReturn(events);
        when(events.insert(eq("test-calendar"), any(Event.class))).thenReturn(insert);
        when(insert.setSendUpdates(anyString())).thenReturn(insert);
        when(insert.execute()).thenReturn(created);

        // When
        Event result = service.createEvent("test-calendar", new Event().setSummary("Test"));

        // Then
        assertNotNull(result);
        assertEquals("1", result.getId());
        verify(events).insert(eq("test-calendar"), any(Event.class));
        verify(insert).setSendUpdates(anyString());
        verify(insert).execute();
    }

    @Test
    void updateEvent_ShouldUpdateEvent_WhenValidEvent() throws GeneralSecurityException, IOException {
        // Given
        Event updated = new Event().setId("1").setSummary("Updated");
        Calendar.Events events = mock(Calendar.Events.class);
        Calendar.Events.Update update = mock(Calendar.Events.Update.class);
        
        when(mockCalendar.events()).thenReturn(events);
        when(events.update(eq("test-calendar"), eq("1"), any(Event.class))).thenReturn(update);
        when(update.execute()).thenReturn(updated);

        // When
        Event result = service.updateEvent("1", updated);

        // Then
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Updated", result.getSummary());
        verify(events).update(eq("test-calendar"), eq("1"), any(Event.class));
        verify(update).execute();
    }

    @Test
    void deleteEvent_ShouldDeleteEvent_WhenValidEventId() throws GeneralSecurityException, IOException {
        // Given
        Calendar.Events events = mock(Calendar.Events.class);
        Calendar.Events.Delete delete = mock(Calendar.Events.Delete.class);
        
        when(mockCalendar.events()).thenReturn(events);
        when(events.delete(eq("test-calendar"), eq("1"))).thenReturn(delete);
        doNothing().when(delete).execute();

        // When & Then
        assertDoesNotThrow(() -> service.deleteEvent("1"));
        verify(events).delete("test-calendar", "1");
        verify(delete).execute();
    }
}


