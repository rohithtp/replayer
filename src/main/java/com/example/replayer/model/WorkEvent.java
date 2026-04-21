package com.example.replayer.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class WorkEvent extends Event {

  private String company;

  private String title;

  public String getCompany() {

    return company;
  }

  public void setCompany(String company) {

    this.company = company;
  }

  public String getTitle() {

    return title;
  }

  public void setTitle(String title) {

    this.title = title;
  }

  @Override
  public void validate() {

    if (company == null || company.isBlank()) {

      throw new IllegalArgumentException("company is required");
    }

    if (title == null || title.isBlank()) {

      throw new IllegalArgumentException("title is required");
    }
  }

  @Override
  public Map<String, Object> toMap() {

    Map<String, Object> map = new LinkedHashMap<>();

    map.put("eventType", getEventType());

    map.put("company", company);

    map.put("title", title);

    map.put("startDate", getStartDate());

    map.put("endDate", getEndDate());

    return map;
  }
}
