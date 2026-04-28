package com.example.replayer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests for {@link ResumeEngine}. */
class ResumeEngineTest {

  private ResumeEngine engine;

  @BeforeEach
  void setUp() {
    engine = new ResumeEngine();
  }

  @Test
  void render_emptyList_returnsEmptyString() {
    assertEquals("", engine.render(List.of()));
  }

  @Test
  void render_singleEvent_containsEventData() {
    Map<String, Object> event = Map.of("eventType", "work", "company", "Acme");
    String result = engine.render(List.of(event));

    assertTrue(result.contains("work"));
    assertTrue(result.contains("Acme"));
  }

  @Test
  void render_multipleEvents_eachOnOwnLine() {
    Map<String, Object> e1 = Map.of("eventType", "work");
    Map<String, Object> e2 = Map.of("eventType", "education");
    String result = engine.render(List.of(e1, e2));

    String sep = System.lineSeparator();
    String[] lines = result.split(sep);
    assertEquals(2, lines.length);
  }

  @Test
  void render_eventMap_lineContainsMapToString() {
    Map<String, Object> event = Map.of("eventType", "work");
    String result = engine.render(List.of(event));

    assertTrue(result.startsWith(event.toString()));
  }
}
