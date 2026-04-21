## Replayer Java Design Doc

### Overview

`replayer` is a small Java CLI for reading, filtering, and updating a long-running personal event ledger stored in YAML. The tool focuses on three core workflows:

1. **Read** the ledger and render resume-style output.
2. **Add** new events safely with validation.
3. **Update** existing events without rewriting the file unsafely.

The design uses:

* **Picocli** for CLI parsing and subcommand dispatch.
* **SnakeYAML** for YAML serialization and deserialization.
* Plain Java model classes for events and validation.

---

### Naming and packaging

Java package names should be lowercase and reverse-domain based:

```text
com.example.replayer
```

Suggested source layout:

```text
replayer/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/replayer/
│   │   │       ├── Main.java
│   │   │       ├── cli/
│   │   │       │   ├── ReplayCommand.java
│   │   │       │   ├── AddEventCommand.java
│   │   │       │   └── UpdateEventCommand.java
│   │   │       ├── model/
│   │   │       │   ├── Event.java
│   │   │       │   ├── WorkEvent.java
│   │   │       │   └── EducationEvent.java
│   │   │       ├── service/
│   │   │       │   ├── EventService.java
│   │   │       │   ├── ResumeEngine.java
│   │   │       │   └── EventValidator.java
│   │   │       ├── io/
│   │   │       │   ├── LedgerReader.java
│   │   │       │   └── LedgerWriter.java
│   │   │       └── yaml/
│   │   │           └── YamlCodec.java
│   │   └── resources/
│   │       └── events.yaml
│   └── test/
│       └── java/
│           └── com/example/replayer/
└── README.md
```

---

### Dependencies

The project can be built with Maven. The key dependencies are Picocli and SnakeYAML.

```xml
<dependencies>
    <dependency>
        <groupId>info.picocli</groupId>
        <artifactId>picocli</artifactId>
    </dependency>

    <dependency>
        <groupId>org.yaml</groupId>
        <artifactId>snakeyaml</artifactId>
    </dependency>
</dependencies>
```

If you want a runnable fat jar, add the Maven Shade Plugin or use a Spring Boot-style launcher only if the project later grows beyond a CLI.

---

### CLI design

The CLI exposes a single root command with subcommands:

```sh
replay events.yaml
replay add-event events.yaml event=work company=Acme title=Engineer startDate=2020-01-01 endDate=2022-12-31
replay update-event events.yaml 0 title="Senior Engineer"
```

#### Commands

* `replay`
  Reads the ledger and prints the resume view.

* `add-event`
  Validates and appends a new event.

* `update-event`
  Loads an existing event by index, merges updates, validates, and saves.

---

### Domain model

The event model should be explicit and typed.

#### `Event.java`

```java
package com.example.replayer.model;

import java.time.LocalDate;
import java.util.Map;

public abstract class Event {
    private String eventType;
    private LocalDate startDate;
    private LocalDate endDate;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public abstract void validate();

    public abstract Map<String, Object> toMap();
}
```

#### `WorkEvent.java`

```java
package com.example.replayer.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class WorkEvent extends Event {
    private String company;
    private String title;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void validate() {
        if (company == null || company.isBlank()) {
            throw new IllegalArgumentException("company is required");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title is required");
        }
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("eventType", getEventType());
        map.put("company", company);
        map.put("title", title);
        map.put("startDate", getStartDate());
        map.put("endDate", getEndDate());
        return map;
    }
}
```

#### `EducationEvent.java`

```java
package com.example.replayer.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class EducationEvent extends Event {
    private String institution;
    private String degree;

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    @Override
    public void validate() {
        if (institution == null || institution.isBlank()) {
            throw new IllegalArgumentException("institution is required");
        }
        if (degree == null || degree.isBlank()) {
            throw new IllegalArgumentException("degree is required");
        }
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("eventType", getEventType());
        map.put("institution", institution);
        map.put("degree", degree);
        map.put("startDate", getStartDate());
        map.put("endDate", getEndDate());
        return map;
    }
}
```

---

### YAML handling

SnakeYAML should be isolated behind a small codec class so the rest of the code does not depend on YAML details.

#### `YamlCodec.java`

```java
package com.example.replayer.yaml;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public final class YamlCodec {
    private final Yaml yaml;

    public YamlCodec() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        options.setSortKeys(false);
        this.yaml = new Yaml(options);
    }

    public Yaml getYaml() {
        return yaml;
    }
}
```

---

### File I/O

Ledger access should be handled by dedicated classes.

#### `LedgerReader.java`

```java
package com.example.replayer.io;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface LedgerReader {
    List<Map<String, Object>> read(Path path) throws IOException;
}
```

#### `LedgerWriter.java`

```java
package com.example.replayer.io;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface LedgerWriter {
    void write(Path path, List<Map<String, Object>> events) throws IOException;
}
```

A simple implementation can load the full file into memory, modify the list, then write the complete document back. That keeps updates deterministic and avoids partial writes.

---

### Service layer

The service layer coordinates validation, transformation, and persistence.

#### `EventService.java`

```java
package com.example.replayer.service;

import com.example.replayer.io.LedgerReader;
import com.example.replayer.io.LedgerWriter;
import com.example.replayer.model.Event;
import com.example.replayer.yaml.YamlCodec;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class EventService {
    private final LedgerReader reader;
    private final LedgerWriter writer;
    private final EventValidator validator;

    public EventService(LedgerReader reader, LedgerWriter writer, EventValidator validator) {
        this.reader = reader;
        this.writer = writer;
        this.validator = validator;
    }

    public void addEvent(Path path, Event event) throws IOException {
        validator.validate(event);
        List<Map<String, Object>> events = reader.read(path);
        events.add(event.toMap());
        writer.write(path, events);
    }

    public void updateEvent(Path path, int index, Map<String, Object> updates) throws IOException {
        List<Map<String, Object>> events = reader.read(path);
        if (index < 0 || index >= events.size()) {
            throw new IndexOutOfBoundsException("Invalid event index: " + index);
        }

        Map<String, Object> existing = events.get(index);
        existing.putAll(updates);

        validator.validate(existing);
        writer.write(path, events);
    }
}
```

#### `EventValidator.java`

```java
package com.example.replayer.service;

import com.example.replayer.model.Event;

import java.util.Map;

public class EventValidator {

    public void validate(Event event) {
        event.validate();
    }

    public void validate(Map<String, Object> event) {
        Object eventType = event.get("eventType");
        if (eventType == null) {
            throw new IllegalArgumentException("eventType is required");
        }
        if (!event.containsKey("startDate")) {
            throw new IllegalArgumentException("startDate is required");
        }
        if (!event.containsKey("endDate")) {
            throw new IllegalArgumentException("endDate is required");
        }
    }
}
```

#### `ResumeEngine.java`

```java
package com.example.replayer.service;

import java.util.List;
import java.util.Map;

public class ResumeEngine {
    public String render(List<Map<String, Object>> events) {
        StringBuilder out = new StringBuilder();
        for (Map<String, Object> event : events) {
            out.append(event).append(System.lineSeparator());
        }
        return out.toString();
    }
}
```

---

### Picocli commands

Picocli keeps the CLI definitions clean and testable.

#### `Main.java`

```java
package com.example.replayer;

import com.example.replayer.cli.ReplayCommand;
import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new ReplayCommand()).execute(args);
        System.exit(exitCode);
    }
}
```

#### `ReplayCommand.java`

```java
package com.example.replayer.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;

@Command(
        name = "replay",
        mixinStandardHelpOptions = true,
        subcommands = {
                AddEventCommand.class,
                UpdateEventCommand.class
        },
        description = "Render the resume view from a YAML event ledger."
)
public class ReplayCommand implements Runnable {

    @Parameters(index = "0", description = "Path to the ledger file")
    private Path ledgerFile;

    @Override
    public void run() {
        System.out.println("Render ledger: " + ledgerFile);
    }
}
```

#### `AddEventCommand.java`

```java
package com.example.replayer.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.time.LocalDate;

@Command(name = "add-event", description = "Add a new event to the ledger.")
public class AddEventCommand implements Runnable {

    @Parameters(index = "0", description = "Path to the ledger file")
    private Path ledgerFile;

    @Option(names = "--eventType", required = true)
    private String eventType;

    @Option(names = "--company")
    private String company;

    @Option(names = "--title")
    private String title;

    @Option(names = "--institution")
    private String institution;

    @Option(names = "--degree")
    private String degree;

    @Option(names = "--startDate", required = true)
    private LocalDate startDate;

    @Option(names = "--endDate", required = true)
    private LocalDate endDate;

    @Override
    public void run() {
        System.out.println("Add event to: " + ledgerFile);
    }
}
```

#### `UpdateEventCommand.java`

```java
package com.example.replayer.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;

@Command(name = "update-event", description = "Update an existing event in the ledger.")
public class UpdateEventCommand implements Runnable {

    @Parameters(index = "0", description = "Path to the ledger file")
    private Path ledgerFile;

    @Parameters(index = "1", description = "Index of the event to update")
    private int index;

    @Override
    public void run() {
        System.out.println("Update event " + index + " in: " + ledgerFile);
    }
}
```

---

### Behavior rules

The implementation should follow these rules:

1. **Load the full YAML file before modifying it.**
2. **Validate required fields before writing.**
3. **Preserve field order when serializing.**
4. **Keep command parsing separate from business logic.**
5. **Use immutable boundaries where practical, especially in service APIs.**

---

### Recommended next step

A clean next version of this design would add:

* a real `EventFactory` for converting CLI input into typed events,
* a `YamlEventMapper` for converting between YAML maps and Java objects,
* tests for `add-event`, `update-event`, and ledger rendering.
