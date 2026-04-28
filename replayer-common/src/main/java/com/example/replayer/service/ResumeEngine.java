package com.example.replayer.service;

import java.util.List;
import java.util.Map;

/**
 * Class responsible for rendering the resume from events.
 */
public class ResumeEngine {

  /**
   * Renders the list of events into a formatted string.
   *
   * @param events the list of events to render
   * @return a string representation of the resume
   */
  public String render(List<Map<String, Object>> events) {

    StringBuilder out = new StringBuilder();

    for (Map<String, Object> event : events) {

      out.append(event).append(System.lineSeparator());
    }

    return out.toString();
  }
}
