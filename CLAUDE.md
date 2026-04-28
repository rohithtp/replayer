# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and test commands

```sh
# Build fat JAR (skipping tests)
mvn clean package -DskipTests

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=WorkEventTest

# Run a single test method
mvn test -Dtest=EventServiceTest#addEvent_validEvent_appendsToLedger

# Check code style (runs at validate phase — fires automatically before test/package)
mvn checkstyle:check

# Auto-format all Java source (required before verify passes)
mvn spotless:apply

# Full build including style checks
mvn verify
```

> `spotless:check` runs at the `verify` phase only, so `mvn test` skips it. Always run `mvn spotless:apply` before `mvn verify`.

## Running the CLI

```sh
java -jar target/replayer-1.0-SNAPSHOT.jar <ledgerFile>                          # render
java -jar target/replayer-1.0-SNAPSHOT.jar add-event <ledgerFile> [options]      # add
java -jar target/replayer-1.0-SNAPSHOT.jar update-event <ledgerFile> <index> [options]  # update
```

## Architecture

The application is a picocli CLI that reads and writes a YAML event ledger. The flow is:

```
CLI command → EventService → LedgerReader / LedgerWriter → YAML file
                           ↕
                     EventValidator
```

**Key design decisions:**

- `ReplayCommand` is the root command. Its `<ledgerFile>` positional parameter has `arity = "0..1"` so subcommands (`add-event`, `update-event`) can specify their own `<ledgerFile>` without satisfying the parent's parameter first.
- `AddEventCommand` and `UpdateEventCommand` each construct their own `EventService` (no shared DI container). If adding a new command, follow the same constructor pattern.
- `LedgerReader.read()` returns an empty list when the file does not exist or is empty — callers never need to handle a null return.
- Dates are stored as ISO strings (`'2020-01-01'`) in the YAML map, not as `LocalDate` objects. Both `toMap()` in the model classes and `UpdateEventCommand` call `.toString()` on `LocalDate` before putting values into the map. Do not put raw `LocalDate` into any map that will be serialised by SnakeYAML.
- `EventValidator.validate(Map)` uses `containsKey` for `startDate`/`endDate` (null value with key present passes) but a null-check for `eventType` (missing key and null value both fail). This asymmetry is intentional.

## Code style

- Formatter: `google-java-format 1.17.0` via the Spotless plugin.
- Checkstyle: `google_checks.xml`. Violations are warnings in output but the build fails on errors. All public types and public/protected methods require Javadoc.
- JUnit 5 test methods are package-private (no `public` modifier) — this is intentional and avoids triggering Javadoc requirements.

## Mockito / Java 25 note

Mockito's default inline mock maker cannot instrument classes under Java 25. The file `src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker` contains `mock-maker-subclass` to force subclass-based mocking. Do not remove this file.
