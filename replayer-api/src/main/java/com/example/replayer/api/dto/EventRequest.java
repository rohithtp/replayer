package com.example.replayer.api.dto;

import com.example.replayer.model.EducationEvent;
import com.example.replayer.model.Event;
import com.example.replayer.model.WorkEvent;
import java.time.LocalDate;

/** Request body for adding a new event. */
public class EventRequest {

  private String eventType;
  private String company;
  private String title;
  private String institution;
  private String degree;
  private String startDate;
  private String endDate;

  /** Converts this request to a domain {@link Event}. */
  public Event toEvent() {
    Event event;
    if ("work".equals(eventType)) {
      WorkEvent w = new WorkEvent();
      w.setCompany(company);
      w.setTitle(title);
      event = w;
    } else if ("education".equals(eventType)) {
      EducationEvent e = new EducationEvent();
      e.setInstitution(institution);
      e.setDegree(degree);
      event = e;
    } else {
      throw new IllegalArgumentException("Unknown eventType: " + eventType);
    }
    event.setEventType(eventType);
    event.setStartDate(startDate != null ? LocalDate.parse(startDate) : null);
    event.setEndDate(endDate != null ? LocalDate.parse(endDate) : null);
    return event;
  }

  /** Returns the event type. */
  public String getEventType() {
    return eventType;
  }

  /** Sets the event type. */
  public void setEventType(String eventType) {
    this.eventType = eventType;
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
