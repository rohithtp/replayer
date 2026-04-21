package com.example.replayer.model;

import java.time.LocalDate;
import java.util.Map;

/**
 * Abstract class representing a generic event.
 */
public abstract class Event {

  private String eventType;

  private LocalDate startDate;

  private LocalDate endDate;

  /**
   * Gets the type of the event.
   *
   * @return the type of the event
   */
  public String getEventType() {

    return eventType;
  }

  /**
   * Sets the type of the event.
   *
   * @param eventType the type of the event to set
   */
  public void setEventType(String eventType) {

    this.eventType = eventType;
  }

  /**
   * Gets the start date of the event.
   *
   * @return the start date of the event
   */
  public LocalDate getStartDate() {

    return startDate;
  }

  /**
   * Sets the start date of the event.
   *
   * @param startDate the start date to set
   */
  public void setStartDate(LocalDate startDate) {

    this.startDate = startDate;
  }

  /**
   * Gets the end date of the event.
   *
   * @return the end date of the event
   */
  public LocalDate getEndDate() {

    return endDate;
  }

  /**
   * Sets the end date of the event.
   *
   * @param endDate the end date to set
   */
  public void setEndDate(LocalDate endDate) {

    this.endDate = endDate;
  }

  /**
   * Validates the event.
   *
   * @throws IllegalArgumentException if required fields are missing or invalid
   */
  public abstract void validate();

  /**
   * Converts the event to a map.
   *
   * @return a map representation of the event
   */
  public abstract Map<String, Object> toMap();
}
