package com.example.replayer.io;

import com.example.replayer.yaml.YamlCodec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class LedgerWriter {

  private final Yaml yaml;

  public LedgerWriter(YamlCodec codec) {

    this.yaml = codec.getYaml();
  }

  public void write(Path path, List<Map<String, Object>> events) throws IOException {

    String content = yaml.dump(events);

    Files.writeString(path, content);
  }
}
