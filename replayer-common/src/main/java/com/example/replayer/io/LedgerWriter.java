package com.example.replayer.io;

import com.example.replayer.yaml.YamlCodec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

/**
 * Class responsible for writing to the ledger file.
 */
public class LedgerWriter {

  private final Yaml yaml;

  /**
   * Constructs a new LedgerWriter with the given YamlCodec.
   *
   * @param codec the YamlCodec to use for YAML serialization
   */
  public LedgerWriter(YamlCodec codec) {

    this.yaml = codec.getYaml();
  }

  /**
   * Writes the list of events to the ledger file.
   *
   * @param path the path to the ledger file
   * @param events the list of events to write
   * @throws IOException if an I/O error occurs while writing the file
   */
  public void write(Path path, List<Map<String, Object>> events) throws IOException {

    String content = yaml.dump(events);

    Files.writeString(path, content);
  }
}
