package com.example.replayer.yaml;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

/** Tests for {@link YamlCodec}. */
class YamlCodecTest {

  @Test
  void getYaml_returnsNonNullInstance() {
    assertNotNull(new YamlCodec().getYaml());
  }

  @Test
  void getYaml_usesBlockFlowStyle() {
    Yaml yaml = new YamlCodec().getYaml();
    Map<String, Object> data = Map.of("key", "value");
    String output = yaml.dump(List.of(data));

    // block style uses newlines rather than inline braces
    assertTrue(output.contains("\n"));
    assertTrue(output.contains("key: value"));
  }

  @Test
  void getYaml_canRoundTripList() {
    Yaml yaml = new YamlCodec().getYaml();
    Map<String, Object> original = Map.of("eventType", "work", "company", "Acme");

    String dumped = yaml.dump(List.of(original));
    List<Map<String, Object>> loaded = yaml.load(dumped);

    assertNotNull(loaded);
    assertTrue(loaded.size() == 1);
    assertTrue("work".equals(loaded.get(0).get("eventType")));
  }
}
