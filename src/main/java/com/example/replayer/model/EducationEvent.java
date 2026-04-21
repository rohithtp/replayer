package com.example.replayer.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class EducationEvent extends Event {

  private String institution;

  private String degree;

  public String getInstitution() {

    return institution;
  }

  public void setInstitution(String institution) {

    this.institution = institution;
  }

  public String getDegree() {

    return degree;
  }

  public void setDegree(String degree) {

    this.degree = degree;
  }

  @Override
  public void validate() {

    if (institution == null || institution.isBlank()) {

      throw new IllegalArgumentException("institution is required");
    }

    if (degree == null || degree.isBlank()) {

      throw new IllegalArgumentException("degree is required");
    }
  }

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
