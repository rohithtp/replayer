package com.example.replayer.service;

import com.example.replayer.model.Event;
import java.util.Map;

public class EventValidator {

  public void validate(Event event) {

    event.validate();
  }

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
