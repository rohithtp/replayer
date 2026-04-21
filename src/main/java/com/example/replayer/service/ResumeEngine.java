package com.example.replayer.service;

import java.util.List;
import java.util.Map;

public class ResumeEngine {

  public String render(List<Map<String, Object>> events) {

    StringBuilder out = new StringBuilder();

    for (Map<String, Object> event : events) {

      out.append(event).append(System.lineSeparator());
    }

    return out.toString();
  }
}
