package com.example.replayer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.replayer.io.LedgerReader;
import com.example.replayer.io.LedgerWriter;
import com.example.replayer.model.WorkEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests for {@link EventService}. */
@ExtendWith(MockitoExtension.class)
class EventServiceTest {

  @Mock private LedgerReader reader;
  @Mock private LedgerWriter writer;

  private EventService service;
  private final Path path = Path.of("test_ledger.yaml");

  @BeforeEach
  void setUp() {
    service = new EventService(reader, writer, new EventValidator());
  }

  // --- addEvent ---

  @Test
  void addEvent_validEvent_appendsToLedger() throws IOException {
    WorkEvent event = validWorkEvent();
    when(reader.read(path)).thenReturn(new ArrayList<>());

    service.addEvent(path, event);

    verify(writer).write(eq(path), argThat(list -> list.size() == 1));
  }

  @Test
  void addEvent_validEvent_preservesExistingEntries() throws IOException {
    WorkEvent event = validWorkEvent();
    List<Map<String, Object>> existing = new ArrayList<>();
    existing.add(Map.of("eventType", "education"));
    when(reader.read(path)).thenReturn(existing);

    service.addEvent(path, event);

    verify(writer).write(eq(path), argThat(list -> list.size() == 2));
  }

  @Test
  void addEvent_invalidEvent_throwsBeforeReading() {
    WorkEvent event = new WorkEvent(); // missing company and title
    assertThrows(IllegalArgumentException.class, () -> service.addEvent(path, event));
    verifyNoInteractions(reader, writer);
  }

  // --- updateEvent ---

  @Test
  void updateEvent_validIndex_mergesFieldsAndWrites() throws IOException {
    List<Map<String, Object>> events = new ArrayList<>();
    events.add(validMapEvent());
    when(reader.read(path)).thenReturn(events);

    service.updateEvent(path, 0, Map.of("title", "Senior Engineer"));

    verify(writer)
        .write(
            eq(path),
            argThat(list -> "Senior Engineer".equals(list.get(0).get("title"))));
  }

  @Test
  void updateEvent_indexBeyondSize_throwsIndexOutOfBounds() throws IOException {
    when(reader.read(path)).thenReturn(new ArrayList<>());
    assertThrows(IndexOutOfBoundsException.class, () -> service.updateEvent(path, 0, Map.of()));
  }

  @Test
  void updateEvent_negativeIndex_throwsIndexOutOfBounds() throws IOException {
    when(reader.read(path)).thenReturn(new ArrayList<>());
    assertThrows(IndexOutOfBoundsException.class, () -> service.updateEvent(path, -1, Map.of()));
  }

  @Test
  void updateEvent_updatedMapFailsValidation_throwsIllegalArgument() throws IOException {
    Map<String, Object> event = new LinkedHashMap<>();
    event.put("startDate", "2020-01-01");
    event.put("endDate", "2022-01-01");
    // no eventType — removing it via update should fail validator
    List<Map<String, Object>> events = new ArrayList<>();
    events.add(event);
    when(reader.read(path)).thenReturn(events);

    assertThrows(
        IllegalArgumentException.class,
        () -> service.updateEvent(path, 0, Map.of("company", "Acme")));
  }

  // --- getEvents ---

  @Test
  void getEvents_returnsDelegatedList() throws IOException {
    List<Map<String, Object>> expected = List.of(Map.of("eventType", "work"));
    when(reader.read(path)).thenReturn(expected);

    List<Map<String, Object>> result = service.getEvents(path);

    assertEquals(expected, result);
  }

  // --- helpers ---

  private WorkEvent validWorkEvent() {
    WorkEvent e = new WorkEvent();
    e.setEventType("work");
    e.setCompany("Acme Corp");
    e.setTitle("Engineer");
    e.setStartDate(LocalDate.of(2020, 1, 1));
    e.setEndDate(LocalDate.of(2022, 6, 30));
    return e;
  }

  private Map<String, Object> validMapEvent() {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("eventType", "work");
    map.put("company", "Acme Corp");
    map.put("title", "Engineer");
    map.put("startDate", "2020-01-01");
    map.put("endDate", "2022-01-01");
    return map;
  }
}
