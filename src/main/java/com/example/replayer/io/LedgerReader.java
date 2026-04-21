package com.example.replayer.io;

import com.example.replayer.yaml.YamlCodec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class LedgerReader {

  private final Yaml yaml;

  public LedgerReader(YamlCodec codec) {

    this.yaml = codec.getYaml();
  }

  public List<Map<String, Object>> read(Path path) throws IOException {

    String content = Files.readString(path);

    return yaml.load(content);
  }
}
