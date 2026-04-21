package com.example.replayer.yaml;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public final class YamlCodec {

  private final Yaml yaml;

  public YamlCodec() {

    DumperOptions options = new DumperOptions();

    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

    options.setPrettyFlow(true);

    // options.setSortKeys(false);

    this.yaml = new Yaml(options);
  }

  public Yaml getYaml() {

    return yaml;
  }
}
