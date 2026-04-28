package com.example.replayer.api.controller;

import com.example.replayer.service.EventService;
import com.example.replayer.service.ResumeEngine;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST controller for rendering the resume view. */
@RestController
@RequestMapping("/api/resume")
@Tag(name = "Resume", description = "Render resume from the event ledger")
public class ResumeController {

  private final EventService eventService;
  private final ResumeEngine resumeEngine;
  private final Path ledgerPath;

  /** Constructs the controller with required dependencies. */
  public ResumeController(EventService eventService, ResumeEngine resumeEngine, Path ledgerPath) {
    this.eventService = eventService;
    this.resumeEngine = resumeEngine;
    this.ledgerPath = ledgerPath;
  }

  /** Renders all ledger events as plain-text resume output. */
  @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
  @Operation(summary = "Render resume as plain text")
  public ResponseEntity<String> renderResume() throws IOException {
    List<Map<String, Object>> events = eventService.getEvents(ledgerPath);
    return ResponseEntity.ok(resumeEngine.render(events));
  }
}
