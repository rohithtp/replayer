package com.example.replayer.yaml;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 * Class responsible for YAML encoding and decoding.
 */
public final class YamlCodec {

  private final Yaml yaml;

  /**
   * Constructs a new YamlCodec with default options.
   */
  public YamlCodec() {

    DumperOptions options = new DumperOptions();

    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

    options.setPrettyFlow(true);

    // options.setSortKeys(false);

    this.yaml = new Yaml(options);
  }

  /**
   * Gets the YAML instance.
   *
   * @return the YAML instance
   */
  public Yaml getYaml() {

    return yaml;
  }
}
