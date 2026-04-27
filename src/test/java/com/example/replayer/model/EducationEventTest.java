package com.example.replayer.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Tests for {@link EducationEvent}. */
class EducationEventTest {

  private EducationEvent buildValid() {
    EducationEvent e = new EducationEvent();
    e.setEventType("education");
    e.setInstitution("State University");
    e.setDegree("B.Sc. Computer Science");
    e.setStartDate(LocalDate.of(2015, 9, 1));
    e.setEndDate(LocalDate.of(2019, 5, 31));
    return e;
  }

  @Test
  void validate_allFieldsPresent_doesNotThrow() {
    buildValid().validate();
  }

  @Test
  void validate_nullInstitution_throwsIllegalArgument() {
    EducationEvent e = buildValid();
    e.setInstitution(null);
    assertThrows(IllegalArgumentException.class, e::validate);
  }

  @Test
  void validate_blankInstitution_throwsIllegalArgument() {
    EducationEvent e = buildValid();
    e.setInstitution("   ");
    assertThrows(IllegalArgumentException.class, e::validate);
  }

  @Test
  void validate_nullDegree_throwsIllegalArgument() {
    EducationEvent e = buildValid();
    e.setDegree(null);
    assertThrows(IllegalArgumentException.class, e::validate);
  }

  @Test
  void validate_blankDegree_throwsIllegalArgument() {
    EducationEvent e = buildValid();
    e.setDegree("");
    assertThrows(IllegalArgumentException.class, e::validate);
  }

  @Test
  void toMap_containsAllFields() {
    EducationEvent e = buildValid();
    Map<String, Object> map = e.toMap();

    assertEquals("education", map.get("eventType"));
    assertEquals("State University", map.get("institution"));
    assertEquals("B.Sc. Computer Science", map.get("degree"));
    assertEquals("2015-09-01", map.get("startDate"));
    assertEquals("2019-05-31", map.get("endDate"));
  }

  @Test
  void toMap_nullDatesIncluded() {
    EducationEvent e = new EducationEvent();
    e.setEventType("education");
    e.setInstitution("MIT");
    e.setDegree("M.Sc.");
    Map<String, Object> map = e.toMap();

    assertNull(map.get("startDate"));
    assertNull(map.get("endDate"));
  }
}
