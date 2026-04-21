package com.example.replayer.model;

import java.time.LocalDate;
import java.util.Map;

public abstract class Event {

  private String eventType;

  private LocalDate startDate;

  private LocalDate endDate;

  public String getEventType() {

    return eventType;
  }

  public void setEventType(String eventType) {

    this.eventType = eventType;
  }

  public LocalDate getStartDate() {

    return startDate;
  }

  public void setStartDate(LocalDate startDate) {

    this.startDate = startDate;
  }

  public LocalDate getEndDate() {

    return endDate;
  }

  public void setEndDate(LocalDate endDate) {

    this.endDate = endDate;
  }

  public abstract void validate();

  public abstract Map<String, Object> toMap();
}
