package com.example.replayer.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.replayer.yaml.YamlCodec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests for {@link LedgerWriter}. */
class LedgerWriterTest {

  @Test
  void write_singleEvent_createsYamlFile(@TempDir Path tempDir) throws IOException {
    Path file = tempDir.resolve("ledger.yaml");
    Map<String, Object> event = new LinkedHashMap<>();
    event.put("eventType", "work");
    event.put("company", "Acme");

    new LedgerWriter(new YamlCodec()).write(file, List.of(event));

    assertTrue(Files.exists(file));
    String content = Files.readString(file);
    assertTrue(content.contains("eventType: work"));
    assertTrue(content.contains("company: Acme"));
  }

  @Test
  void write_multipleEvents_allPersisted(@TempDir Path tempDir) throws IOException {
    Path file = tempDir.resolve("ledger.yaml");
    Map<String, Object> e1 = new LinkedHashMap<>();
    e1.put("eventType", "work");
    e1.put("company", "Corp A");
    Map<String, Object> e2 = new LinkedHashMap<>();
    e2.put("eventType", "education");
    e2.put("institution", "MIT");

    new LedgerWriter(new YamlCodec()).write(file, List.of(e1, e2));

    String content = Files.readString(file);
    assertTrue(content.contains("Corp A"));
    assertTrue(content.contains("MIT"));
  }

  @Test
  void write_thenRead_roundTrip(@TempDir Path tempDir) throws IOException {
    Path file = tempDir.resolve("ledger.yaml");
    Map<String, Object> event = new LinkedHashMap<>();
    event.put("eventType", "work");
    event.put("company", "Acme");
    event.put("title", "Engineer");

    YamlCodec codec = new YamlCodec();
    new LedgerWriter(codec).write(file, List.of(event));
    List<Map<String, Object>> read = new LedgerReader(codec).read(file);

    assertEquals(1, read.size());
    assertEquals("work", read.get(0).get("eventType"));
    assertEquals("Acme", read.get(0).get("company"));
    assertEquals("Engineer", read.get(0).get("title"));
  }

  @Test
  void write_overwritesExistingFile(@TempDir Path tempDir) throws IOException {
    Path file = tempDir.resolve("ledger.yaml");
    Files.writeString(file, "old content");

    Map<String, Object> event = new LinkedHashMap<>();
    event.put("eventType", "work");
    new LedgerWriter(new YamlCodec()).write(file, List.of(event));

    String content = Files.readString(file);
    assertTrue(content.contains("eventType: work"));
    assertTrue(!content.contains("old content"));
  }
}
