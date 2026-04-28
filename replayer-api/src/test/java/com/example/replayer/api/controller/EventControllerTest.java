package com.example.replayer.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.replayer.model.Event;
import com.example.replayer.service.EventService;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** Tests for {@link EventController}. */
@WebMvcTest(EventController.class)
class EventControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private EventService eventService;

  // Path is an interface — MockBean supplies the bean EventController needs.
  @MockBean private Path ledgerPath;

  @Test
  void listEvents_returnsJsonArray() throws Exception {
    when(eventService.getEvents(any()))
        .thenReturn(List.of(Map.of("eventType", "work", "company", "Acme")));

    mockMvc
        .perform(get("/api/events"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].eventType").value("work"))
        .andExpect(jsonPath("$[0].company").value("Acme"));
  }

  @Test
  void listEvents_emptyLedger_returnsEmptyArray() throws Exception {
    when(eventService.getEvents(any())).thenReturn(List.of());

    mockMvc.perform(get("/api/events")).andExpect(status().isOk()).andExpect(jsonPath("$").isEmpty());
  }

  @Test
  void addEvent_validWorkEvent_returns201() throws Exception {
    mockMvc
        .perform(
            post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"eventType":"work","company":"Acme","title":"Engineer",
                     "startDate":"2020-01-01","endDate":"2022-12-31"}
                    """))
        .andExpect(status().isCreated());

    verify(eventService).addEvent(any(Path.class), any(Event.class));
  }

  @Test
  void addEvent_unknownEventType_returns400() throws Exception {
    mockMvc
        .perform(
            post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"eventType":"invalid","startDate":"2020-01-01","endDate":"2022-12-31"}
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").exists());
  }

  @Test
  void addEvent_missingRequiredField_returns400() throws Exception {
    doThrow(new IllegalArgumentException("company is required"))
        .when(eventService)
        .addEvent(any(), any());

    mockMvc
        .perform(
            post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"eventType":"work","title":"Engineer",
                     "startDate":"2020-01-01","endDate":"2022-12-31"}
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("company is required"));
  }

  @Test
  void updateEvent_validRequest_returns204() throws Exception {
    mockMvc
        .perform(
            put("/api/events/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Senior Engineer"}
                    """))
        .andExpect(status().isNoContent());

    verify(eventService).updateEvent(any(Path.class), eq(0), any());
  }

  @Test
  void updateEvent_indexOutOfBounds_returns404() throws Exception {
    doThrow(new IndexOutOfBoundsException("Invalid event index: 99"))
        .when(eventService)
        .updateEvent(any(), eq(99), any());

    mockMvc
        .perform(
            put("/api/events/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Senior Engineer"}
                    """))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Invalid event index: 99"));
  }
}
