package com.example.replayer.service;

import com.example.replayer.io.LedgerReader;
import com.example.replayer.io.LedgerWriter;
import com.example.replayer.model.Event;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Service class for managing events.
 */
public class EventService {

  private final LedgerReader reader;

  private final LedgerWriter writer;

  private final EventValidator validator;

  /**
   * Constructs a new EventService with the given components.
   *
   * @param reader the LedgerReader to use for reading ledger files
   * @param writer the LedgerWriter to use for writing ledger files
   * @param validator the EventValidator to use for validating events
   */
  public EventService(LedgerReader reader, LedgerWriter writer, EventValidator validator) {

    this.reader = reader;

    this.writer = writer;

    this.validator = validator;
  }

  /**
   * Adds a new event to the ledger file.
   *
   * @param path the path to the ledger file
   * @param event the event to add
   * @throws IOException if an I/O error occurs while adding the event
   */
  public void addEvent(Path path, Event event) throws IOException {

    validator.validate(event);

    List<Map<String, Object>> events = reader.read(path);

    events.add(event.toMap());

    writer.write(path, events);
  }

  /**
   * Updates an existing event in the ledger file.
   *
   * @param path the path to the ledger file
   * @param index the index of the event to update
   * @param updates the map of updates to apply
   * @throws IOException if an I/O error occurs while updating the event
   */
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

  /**
   * Retrieves the list of events from the ledger file.
   *
   * @param path the path to the ledger file
   * @return a list of events represented as maps
   * @throws IOException if an I/O error occurs while reading the ledger file
   */
  public List<Map<String, Object>> getEvents(Path path) throws IOException {

    return reader.read(path);
  }
}
