package com.example.replayer.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents an education event.
 */
public class EducationEvent extends Event {

  private String institution;

  private String degree;

  /**
   * Gets the institution name.
   *
   * @return the institution name
   */
  public String getInstitution() {

    return institution;
  }

  /**
   * Sets the institution name.
   *
   * @param institution the institution name to set
   */
  public void setInstitution(String institution) {

    this.institution = institution;
  }

  /**
   * Gets the degree obtained.
   *
   * @return the degree obtained
   */
  public String getDegree() {

    return degree;
  }

  /**
   * Sets the degree obtained.
   *
   * @param degree the degree to set
   */
  public void setDegree(String degree) {

    this.degree = degree;
  }

  /**
   * Validates the education event.
   *
   * @throws IllegalArgumentException if required fields are missing or invalid
   */
  @Override
  public void validate() {

    if (institution == null || institution.isBlank()) {

      throw new IllegalArgumentException("institution is required");
    }

    if (degree == null || degree.isBlank()) {

      throw new IllegalArgumentException("degree is required");
    }
  }

  /**
   * Converts the education event to a map.
   *
   * @return a map representation of the education event
   */
  @Override
  public Map<String, Object> toMap() {

    Map<String, Object> map = new LinkedHashMap<>();

    map.put("eventType", getEventType());

    map.put("institution", institution);

    map.put("degree", degree);

    map.put("startDate", getStartDate());

    map.put("endDate", getEndDate());

    return map;
  }
}
