package com.example.replayer.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.replayer.yaml.YamlCodec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests for {@link LedgerReader}. */
class LedgerReaderTest {

  @Test
  void read_validYamlList_returnsParsedEvents(@TempDir Path tempDir) throws IOException {
    Path file = tempDir.resolve("ledger.yaml");
    Files.writeString(
        file,
        "- eventType: work\n  company: Acme\n  title: Engineer\n"
            + "- eventType: education\n  institution: MIT\n  degree: B.Sc.\n");

    LedgerReader reader = new LedgerReader(new YamlCodec());
    List<Map<String, Object>> events = reader.read(file);

    assertNotNull(events);
    assertEquals(2, events.size());
    assertEquals("work", events.get(0).get("eventType"));
    assertEquals("Acme", events.get(0).get("company"));
    assertEquals("education", events.get(1).get("eventType"));
    assertEquals("MIT", events.get(1).get("institution"));
  }

  @Test
  void read_singleEvent_returnsListOfOne(@TempDir Path tempDir) throws IOException {
    Path file = tempDir.resolve("ledger.yaml");
    Files.writeString(file, "- eventType: work\n  company: Corp\n  title: Dev\n");

    LedgerReader reader = new LedgerReader(new YamlCodec());
    List<Map<String, Object>> events = reader.read(file);

    assertEquals(1, events.size());
    assertEquals("Corp", events.get(0).get("company"));
  }

  @Test
  void read_preservesAllKeys(@TempDir Path tempDir) throws IOException {
    Path file = tempDir.resolve("ledger.yaml");
    Files.writeString(
        file, "- eventType: work\n  company: Acme\n  title: Eng\n  startDate: 2020-01-01\n");

    LedgerReader reader = new LedgerReader(new YamlCodec());
    Map<String, Object> event = reader.read(file).get(0);

    assertEquals("work", event.get("eventType"));
    assertEquals("Acme", event.get("company"));
    assertEquals("Eng", event.get("title"));
    assertNotNull(event.get("startDate"));
  }
}
