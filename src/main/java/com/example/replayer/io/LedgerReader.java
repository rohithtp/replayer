package com.example.replayer.io;

import com.example.replayer.yaml.YamlCodec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

/**
 * Class responsible for reading the ledger file.
 */
public class LedgerReader {

  private final Yaml yaml;

  /**
   * Constructs a new LedgerReader with the given YamlCodec.
   *
   * @param codec the YamlCodec to use for YAML parsing
   */
  public LedgerReader(YamlCodec codec) {

    this.yaml = codec.getYaml();
  }

  /**
   * Reads the ledger file and returns the list of events.
   *
   * @param path the path to the ledger file
   * @return a list of events represented as maps
   * @throws IOException if an I/O error occurs while reading the file
   */
  public List<Map<String, Object>> read(Path path) throws IOException {

    if (!Files.exists(path)) {

      return new ArrayList<>();
    }

    String content = Files.readString(path);

    List<Map<String, Object>> result = yaml.load(content);

    return result != null ? result : new ArrayList<>();
  }
}
