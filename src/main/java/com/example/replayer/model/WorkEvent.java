package com.example.replayer.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a work event.
 */
public class WorkEvent extends Event {

  private String company;

  private String title;

  /**
   * Gets the company name.
   *
   * @return the company name
   */
  public String getCompany() {

    return company;
  }

  /**
   * Sets the company name.
   *
   * @param company the company name to set
   */
  public void setCompany(String company) {

    this.company = company;
  }

  /**
   * Gets the job title.
   *
   * @return the job title
   */
  public String getTitle() {

    return title;
  }

  /**
   * Sets the job title.
   *
   * @param title the job title to set
   */
  public void setTitle(String title) {

    this.title = title;
  }

  /**
   * Validates the work event.
   *
   * @throws IllegalArgumentException if required fields are missing or invalid
   */
  @Override
  public void validate() {

    if (company == null || company.isBlank()) {

      throw new IllegalArgumentException("company is required");
    }

    if (title == null || title.isBlank()) {

      throw new IllegalArgumentException("title is required");
    }
  }

  /**
   * Converts the work event to a map.
   *
   * @return a map representation of the work event
   */
  @Override
  public Map<String, Object> toMap() {

    Map<String, Object> map = new LinkedHashMap<>();

    map.put("eventType", getEventType());

    map.put("company", company);

    map.put("title", title);

    map.put("startDate", getStartDate() != null ? getStartDate().toString() : null);

    map.put("endDate", getEndDate() != null ? getEndDate().toString() : null);

    return map;
  }
}
