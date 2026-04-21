package com.example.replayer.cli;

import com.example.replayer.io.LedgerReader;
import com.example.replayer.io.LedgerWriter;
import com.example.replayer.service.EventService;
import com.example.replayer.service.EventValidator;
import com.example.replayer.yaml.YamlCodec;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@CommandLine.Command(name = "update-event", description = "Update an existing event in the ledger.")
public class UpdateEventCommand implements Runnable {

  @Parameters(index = "0", description = "Path to the ledger file")
  private Path ledgerFile;

  @Parameters(index = "1", description = "Index of the event to update")
  private int index;

  @Option(names = "--company")
  private String company;

  @Option(names = "--title")
  private String title;

  @Option(names = "--institution")
  private String institution;

  @Option(names = "--degree")
  private String degree;

  @Option(names = "--startDate")
  private LocalDate startDate;

  @Option(names = "--endDate")
  private LocalDate endDate;

  private final EventService eventService;

  public UpdateEventCommand() {

    YamlCodec yamlCodec = new YamlCodec();

    LedgerReader reader = new LedgerReader(yamlCodec);

    LedgerWriter writer = new LedgerWriter(yamlCodec);

    EventValidator validator = new EventValidator();

    this.eventService = new EventService(reader, writer, validator);
  }

  @Override
  public void run() {

    try {

      Map<String, Object> updates = new HashMap<>();

      if (company != null) updates.put("company", company);

      if (title != null) updates.put("title", title);

      if (institution != null) updates.put("institution", institution);

      if (degree != null) updates.put("degree", degree);

      if (startDate != null) updates.put("startDate", startDate);

      if (endDate != null) updates.put("endDate", endDate);

      eventService.updateEvent(ledgerFile, index, updates);

    } catch (Exception e) {

      System.err.println("Error: " + e.getMessage());

      System.exit(1);
    }
  }
}
