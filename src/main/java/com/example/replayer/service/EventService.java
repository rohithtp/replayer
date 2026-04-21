package com.example.replayer.service;

import com.example.replayer.io.LedgerReader;
import com.example.replayer.io.LedgerWriter;
import com.example.replayer.model.Event;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class EventService {

  private final LedgerReader reader;

  private final LedgerWriter writer;

  private final EventValidator validator;

  public EventService(LedgerReader reader, LedgerWriter writer, EventValidator validator) {

    this.reader = reader;

    this.writer = writer;

    this.validator = validator;
  }

  public void addEvent(Path path, Event event) throws IOException {

    validator.validate(event);

    List<Map<String, Object>> events = reader.read(path);

    events.add(event.toMap());

    writer.write(path, events);
  }

  public void updateEvent(Path path, int index, Map<String, Object> updates) throws IOException {

    List<Map<String, Object>> events = reader.read(path);

    if (index < 0 || index >= events.size()) {

      throw new IndexOutOfBoundsException("Invalid event index: " + index);
    }

    Map<String, Object> existing = events.get(index);

    existing.putAll(updates);

    validator.validate(existing);

    writer.write(path, events);
  }

  public List<Map<String, Object>> getEvents(Path path) throws IOException {

    return reader.read(path);
  }
}
