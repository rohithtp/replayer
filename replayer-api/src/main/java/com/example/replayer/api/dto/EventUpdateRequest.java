package com.example.replayer.api.dto;

import java.util.LinkedHashMap;
import java.util.Map;

/** Request body for updating fields on an existing event. Only non-null fields are applied. */
public class EventUpdateRequest {

  private String company;
  private String title;
  private String institution;
  private String degree;
  private String startDate;
  private String endDate;

  /** Converts non-null fields to an update map for {@code EventService.updateEvent}. */
  public Map<String, Object> toMap() {
    Map<String, Object> updates = new LinkedHashMap<>();
    if (company != null) {
      updates.put("company", company);
    }
    if (title != null) {
      updates.put("title", title);
    }
    if (institution != null) {
      updates.put("institution", institution);
    }
    if (degree != null) {
      updates.put("degree", degree);
    }
    if (startDate != null) {
      updates.put("startDate", startDate);
    }
    if (endDate != null) {
      updates.put("endDate", endDate);
    }
    return updates;
  }

  /** Returns the company. */
  public String getCompany() {
    return company;
  }

  /** Sets the company. */
  public void setCompany(String company) {
    this.company = company;
  }

  /** Returns the title. */
  public String getTitle() {
    return title;
  }

  /** Sets the title. */
  public void setTitle(String title) {
    this.title = title;
  }

  /** Returns the institution. */
  public String getInstitution() {
    return institution;
  }

  /** Sets the institution. */
  public void setInstitution(String institution) {
    this.institution = institution;
  }

  /** Returns the degree. */
  public String getDegree() {
    return degree;
  }

  /** Sets the degree. */
  public void setDegree(String degree) {
    this.degree = degree;
  }

  /** Returns the start date string. */
  public String getStartDate() {
    return startDate;
  }

  /** Sets the start date string. */
  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  /** Returns the end date string. */
  public String getEndDate() {
    return endDate;
  }

  /** Sets the end date string. */
  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }
}
