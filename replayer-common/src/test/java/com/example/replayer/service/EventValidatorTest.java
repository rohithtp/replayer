package com.example.replayer.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.example.replayer.model.WorkEvent;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests for {@link EventValidator}. */
class EventValidatorTest {

  private EventValidator validator;

  @BeforeEach
  void setUp() {
    validator = new EventValidator();
  }

  // --- validate(Event) ---

  @Test
  void validate_event_validWorkEvent_doesNotThrow() {
    WorkEvent event = new WorkEvent();
    event.setCompany("Acme");
    event.setTitle("Engineer");
    assertDoesNotThrow(() -> validator.validate(event));
  }

  @Test
  void validate_event_invalidWorkEvent_propagatesException() {
    WorkEvent event = new WorkEvent(); // missing company and title
    assertThrows(IllegalArgumentException.class, () -> validator.validate(event));
  }

  // --- validate(Map) ---

  @Test
  void validate_map_allRequiredFields_doesNotThrow() {
    Map<String, Object> map = buildValidMap();
    assertDoesNotThrow(() -> validator.validate(map));
  }

  @Test
  void validate_map_missingEventType_throwsIllegalArgument() {
    Map<String, Object> map = buildValidMap();
    map.remove("eventType");
    assertThrows(IllegalArgumentException.class, () -> validator.validate(map));
  }

  @Test
  void validate_map_nullEventType_throwsIllegalArgument() {
    Map<String, Object> map = buildValidMap();
    map.put("eventType", null);
    assertThrows(IllegalArgumentException.class, () -> validator.validate(map));
  }

  @Test
  void validate_map_missingStartDate_throwsIllegalArgument() {
    Map<String, Object> map = buildValidMap();
    map.remove("startDate");
    assertThrows(IllegalArgumentException.class, () -> validator.validate(map));
  }

  @Test
  void validate_map_missingEndDate_throwsIllegalArgument() {
    Map<String, Object> map = buildValidMap();
    map.remove("endDate");
    assertThrows(IllegalArgumentException.class, () -> validator.validate(map));
  }

  @Test
  void validate_map_nullStartDateValuePresent_doesNotThrow() {
    // containsKey passes even when value is null — only key presence is checked
    Map<String, Object> map = buildValidMap();
    map.put("startDate", null);
    assertDoesNotThrow(() -> validator.validate(map));
  }

  private Map<String, Object> buildValidMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("eventType", "work");
    map.put("startDate", "2020-01-01");
    map.put("endDate", "2022-01-01");
    return map;
  }
}
