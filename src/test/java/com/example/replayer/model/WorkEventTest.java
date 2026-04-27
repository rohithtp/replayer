package com.example.replayer.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Tests for {@link WorkEvent}. */
class WorkEventTest {

  private WorkEvent buildValid() {
    WorkEvent e = new WorkEvent();
    e.setEventType("work");
    e.setCompany("Acme Corp");
    e.setTitle("Software Engineer");
    e.setStartDate(LocalDate.of(2020, 1, 1));
    e.setEndDate(LocalDate.of(2022, 6, 30));
    return e;
  }

  @Test
  void validate_allFieldsPresent_doesNotThrow() {
    buildValid().validate();
  }

  @Test
  void validate_nullCompany_throwsIllegalArgument() {
    WorkEvent e = buildValid();
    e.setCompany(null);
    assertThrows(IllegalArgumentException.class, e::validate);
  }

  @Test
  void validate_blankCompany_throwsIllegalArgument() {
    WorkEvent e = buildValid();
    e.setCompany("   ");
    assertThrows(IllegalArgumentException.class, e::validate);
  }

  @Test
  void validate_nullTitle_throwsIllegalArgument() {
    WorkEvent e = buildValid();
    e.setTitle(null);
    assertThrows(IllegalArgumentException.class, e::validate);
  }

  @Test
  void validate_blankTitle_throwsIllegalArgument() {
    WorkEvent e = buildValid();
    e.setTitle("");
    assertThrows(IllegalArgumentException.class, e::validate);
  }

  @Test
  void toMap_containsAllFields() {
    WorkEvent e = buildValid();
    Map<String, Object> map = e.toMap();

    assertEquals("work", map.get("eventType"));
    assertEquals("Acme Corp", map.get("company"));
    assertEquals("Software Engineer", map.get("title"));
    assertEquals("2020-01-01", map.get("startDate"));
    assertEquals("2022-06-30", map.get("endDate"));
  }

  @Test
  void toMap_nullDatesIncluded() {
    WorkEvent e = new WorkEvent();
    e.setEventType("work");
    e.setCompany("Acme");
    e.setTitle("Dev");
    Map<String, Object> map = e.toMap();

    assertNull(map.get("startDate"));
    assertNull(map.get("endDate"));
  }
}
