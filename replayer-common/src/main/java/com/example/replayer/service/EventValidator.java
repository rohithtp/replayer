package com.example.replayer.service;

import com.example.replayer.model.Event;
import java.util.Map;

/**
 * Class responsible for validating events.
 */
public class EventValidator {

  /**
   * Validates the given event.
   *
   * @param event the event to validate
   */
  public void validate(Event event) {

    event.validate();
  }

  /**
   * Validates the given map representation of an event.
   *
   * @param event the map representation of the event to validate
   * @throws IllegalArgumentException if required fields are missing or invalid
   */
  public void validate(Map<String, Object> event) {

    Object eventType = event.get("eventType");

    if (eventType == null) {

      throw new IllegalArgumentException("eventType is required");
    }

    if (!event.containsKey("startDate")) {

      throw new IllegalArgumentException("startDate is required");
    }

    if (!event.containsKey("endDate")) {

      throw new IllegalArgumentException("endDate is required");
    }
  }
}
