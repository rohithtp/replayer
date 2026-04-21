package com.example.replayer.cli;

import com.example.replayer.io.LedgerReader;
import com.example.replayer.io.LedgerWriter;
import com.example.replayer.model.EducationEvent;
import com.example.replayer.model.Event;
import com.example.replayer.model.WorkEvent;
import com.example.replayer.service.EventService;
import com.example.replayer.service.EventValidator;
import com.example.replayer.yaml.YamlCodec;
import java.nio.file.Path;
import java.time.LocalDate;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@CommandLine.Command(name = "add-event", description = "Add a new event to the ledger.")
public class AddEventCommand implements Runnable {

  @Parameters(index = "0", description = "Path to the ledger file")
  private Path ledgerFile;

  @Option(names = "--eventType", required = true)
  private String eventType;

  @Option(names = "--company")
  private String company;

  @Option(names = "--title")
  private String title;

  @Option(names = "--institution")
  private String institution;

  @Option(names = "--degree")
  private String degree;

  @Option(names = "--startDate", required = true)
  private LocalDate startDate;

  @Option(names = "--endDate", required = true)
  private LocalDate endDate;

  private final EventService eventService;

  public AddEventCommand() {

    YamlCodec yamlCodec = new YamlCodec();

    LedgerReader reader = new LedgerReader(yamlCodec);

    LedgerWriter writer = new LedgerWriter(yamlCodec);

    EventValidator validator = new EventValidator();

    this.eventService = new EventService(reader, writer, validator);
  }

  @Override
  public void run() {

    try {

      Event event;

      if ("work".equals(eventType)) {

        event = new WorkEvent();

        ((WorkEvent) event).setCompany(company);

        ((WorkEvent) event).setTitle(title);

      } else if ("education".equals(eventType)) {

        event = new EducationEvent();

        ((EducationEvent) event).setInstitution(institution);

        ((EducationEvent) event).setDegree(degree);

      } else {

        System.err.println("Invalid event type: " + eventType);

        System.exit(1);

        return;
      }

      event.setEventType(eventType);

      event.setStartDate(startDate);

      event.setEndDate(endDate);

      eventService.addEvent(ledgerFile, event);

    } catch (Exception e) {

      System.err.println("Error: " + e.getMessage());

      System.exit(1);
    }
  }
}
