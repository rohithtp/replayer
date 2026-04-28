# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project layout

Multi-module Maven project (`feature/spring-boot-api` branch). `main` still holds the original single-module CLI.

```
replayer/                    ← parent pom (spring-boot-starter-parent 3.2.5)
├── replayer-common/         ← model, service, io, yaml — no Spring deps
├── replayer-cli/            ← picocli fat-JAR, depends on replayer-common
└── replayer-api/            ← Spring Boot 3.2 REST API, depends on replayer-common
```

## Build and test commands

```sh
# Build all modules
mvn clean package -DskipTests

# Build a single module and its dependencies
mvn clean package -DskipTests -pl replayer-api -am

# Run all tests (52 total across all modules)
mvn test

# Run tests for one module only
mvn test -pl replayer-common
mvn test -pl replayer-api

# Run a single test class
mvn test -pl replayer-common -Dtest=WorkEventTest

# Run a single test method
mvn test -pl replayer-common -Dtest=EventServiceTest#addEvent_validEvent_appendsToLedger

# Check code style (fires at validate phase — runs before test/package automatically)
mvn checkstyle:check

# Auto-format all Java source (must run before mvn verify passes)
mvn spotless:apply

# Full build with style checks
mvn verify
```

> `spotless:check` binds to `verify`, so `mvn test` skips it. `spotless:apply` uses `google-java-format 1.17.0`, which has a known incompatibility with Java 25 — if it fails, run `mvn test` instead of `mvn verify` for day-to-day development.

## Running the API

```sh
java -jar replayer-api/target/replayer-api-1.0-SNAPSHOT.jar
# override ledger path:
java -jar replayer-api/target/replayer-api-1.0-SNAPSHOT.jar --replayer.ledger.file=my.yaml
```

Swagger UI: `http://localhost:8080/swagger-ui.html` — the OpenAPI spec in `replayer-api/src/main/resources/openapi.yml` is the authoritative contract.

## Running the CLI

```sh
java -jar replayer-cli/target/replayer-cli-1.0-SNAPSHOT.jar <ledgerFile>
java -jar replayer-cli/target/replayer-cli-1.0-SNAPSHOT.jar add-event <ledgerFile> [options]
java -jar replayer-cli/target/replayer-cli-1.0-SNAPSHOT.jar update-event <ledgerFile> <index> [options]
```

## Architecture

```
HTTP request / CLI command
        ↓
EventController (replayer-api)     ReplayCommand / AddEventCommand / UpdateEventCommand (replayer-cli)
        ↓                                          ↓
     EventService  ←──────────────────── replayer-common ────────────────────→  EventValidator
        ↓
LedgerReader / LedgerWriter  →  YAML file
```

**Key design decisions:**

- `AppConfig` (replayer-api) wires all `replayer-common` beans as Spring singletons via `@Value("${replayer.ledger.file}")` for the ledger path. The CLI commands construct their own `EventService` via `new` — no DI container.
- `GlobalExceptionHandler` maps `IllegalArgumentException → 400` and `IndexOutOfBoundsException → 404`. New domain exceptions should get a handler here.
- `LedgerReader.read()` always returns a non-null list — empty when the file is missing or blank. Callers never check for null.
- Dates travel as ISO strings (`'2020-01-01'`) through the YAML map layer. `WorkEvent.toMap()`, `EducationEvent.toMap()`, and `UpdateEventCommand` all call `LocalDate.toString()` before insertion. Do not put raw `LocalDate` objects into any map that SnakeYAML will serialize.
- `EventValidator.validate(Map)` uses `containsKey` for `startDate`/`endDate` (null value with key present passes) but a null-check for `eventType`. This asymmetry is intentional and tested explicitly in `EventValidatorTest`.
- The `ReplayCommand` positional parameter uses `arity = "0..1"` so subcommands can supply their own `<ledgerFile>` without picocli demanding the parent's parameter first.
- Controller tests use `@WebMvcTest` + `@MockBean`. The `Path` bean is provided via `@MockBean Path ledgerPath` — do not use an inner `@Configuration` class for this, it is not reliably picked up by the slice context.

## Code style

- Formatter: `google-java-format 1.17.0` via Spotless.
- Checkstyle: `google_checks.xml` bound to `validate` phase. All public types and public/protected methods require Javadoc.
- JUnit 5 test methods are package-private (no `public`) — intentional, avoids Javadoc requirement.

## Mockito / Java 25

Mockito's inline mock maker cannot instrument classes under Java 25. Both `replayer-common` and `replayer-api` carry `src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker` set to `mock-maker-subclass`. Do not remove either file, and copy it to any new module that uses Mockito.
