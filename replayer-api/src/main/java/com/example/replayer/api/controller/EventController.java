package com.example.replayer.api.controller;

import com.example.replayer.api.dto.EventRequest;
import com.example.replayer.api.dto.EventUpdateRequest;
import com.example.replayer.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST controller for event CRUD operations. */
@RestController
@RequestMapping("/api/events")
@Tag(name = "Events", description = "Manage ledger events")
public class EventController {

  private final EventService eventService;
  private final Path ledgerPath;

  /** Constructs the controller with required dependencies. */
  public EventController(EventService eventService, Path ledgerPath) {
    this.eventService = eventService;
    this.ledgerPath = ledgerPath;
  }

  /** Returns all events in the ledger. */
  @GetMapping
  @Operation(summary = "List all events")
  public ResponseEntity<List<Map<String, Object>>> listEvents() throws IOException {
    return ResponseEntity.ok(eventService.getEvents(ledgerPath));
  }

  /** Appends a new event to the ledger. */
  @PostMapping
  @Operation(summary = "Add a new event")
  public ResponseEntity<Void> addEvent(@RequestBody EventRequest request) throws IOException {
    eventService.addEvent(ledgerPath, request.toEvent());
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  /** Merges the supplied fields into the event at the given index. */
  @PutMapping("/{index}")
  @Operation(summary = "Update an existing event by index")
  public ResponseEntity<Void> updateEvent(
      @PathVariable int index, @RequestBody EventUpdateRequest request) throws IOException {
    eventService.updateEvent(ledgerPath, index, request.toMap());
    return ResponseEntity.noContent().build();
  }
}
