package com.example.replayer.cli;

import com.example.replayer.io.LedgerReader;
import com.example.replayer.io.LedgerWriter;
import com.example.replayer.service.EventService;
import com.example.replayer.service.EventValidator;
import com.example.replayer.service.ResumeEngine;
import com.example.replayer.yaml.YamlCodec;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import picocli.CommandLine;
import picocli.CommandLine.Parameters;

@CommandLine.Command(
    name = "replay",
    mixinStandardHelpOptions = true,
    subcommands = {AddEventCommand.class, UpdateEventCommand.class},
    description = "Render the resume view from a YAML event ledger.")
public class ReplayCommand implements Runnable {

  @Parameters(index = "0", arity = "0..1", description = "Path to the ledger file")
  private Path ledgerFile;

  private final EventService eventService;

  private final ResumeEngine resumeEngine;

  public ReplayCommand() {

    YamlCodec yamlCodec = new YamlCodec();

    LedgerReader reader = new LedgerReader(yamlCodec);

    LedgerWriter writer = new LedgerWriter(yamlCodec);

    EventValidator validator = new EventValidator();

    this.eventService = new EventService(reader, writer, validator);

    this.resumeEngine = new ResumeEngine();
  }

  @Override
  public void run() {

    if (ledgerFile == null) {

      System.err.println("Usage: replay <ledgerFile>");

      System.exit(1);

      return;
    }

    try {

      List<Map<String, Object>> events = eventService.getEvents(ledgerFile);

      String resume = resumeEngine.render(events);

      System.out.println(resume);

    } catch (Exception e) {

      System.err.println("Error: " + e.getMessage());

      System.exit(1);
    }
  }
}
