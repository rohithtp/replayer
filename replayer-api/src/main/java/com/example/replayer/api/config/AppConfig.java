package com.example.replayer.api.config;

import com.example.replayer.io.LedgerReader;
import com.example.replayer.io.LedgerWriter;
import com.example.replayer.service.EventService;
import com.example.replayer.service.EventValidator;
import com.example.replayer.service.ResumeEngine;
import com.example.replayer.yaml.YamlCodec;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Wires the replayer-common domain objects as Spring beans. */
@Configuration
public class AppConfig {

  @Value("${replayer.ledger.file:events.yaml}")
  private String ledgerFile;

  /** Ledger file path resolved from configuration. */
  @Bean
  public Path ledgerPath() {
    return Path.of(ledgerFile);
  }

  /** YAML codec shared by reader and writer. */
  @Bean
  public YamlCodec yamlCodec() {
    return new YamlCodec();
  }

  /** Ledger reader bean. */
  @Bean
  public LedgerReader ledgerReader(YamlCodec codec) {
    return new LedgerReader(codec);
  }

  /** Ledger writer bean. */
  @Bean
  public LedgerWriter ledgerWriter(YamlCodec codec) {
    return new LedgerWriter(codec);
  }

  /** Event validator bean. */
  @Bean
  public EventValidator eventValidator() {
    return new EventValidator();
  }

  /** Event service bean. */
  @Bean
  public EventService eventService(
      LedgerReader reader, LedgerWriter writer, EventValidator validator) {
    return new EventService(reader, writer, validator);
  }

  /** Resume engine bean. */
  @Bean
  public ResumeEngine resumeEngine() {
    return new ResumeEngine();
  }
}
