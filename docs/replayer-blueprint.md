# Replayer CLI – Technical Design Document

---

## Overview

**Project Name:** Replayer
**Type:** Java CLI Application
**Primary Purpose:** Manage and render a YAML-based event ledger into structured, resume-style output

`replayer` is a lightweight command-line tool designed to maintain a long-term personal or professional event history. It enables users to:

* Read and render events
* Add new structured entries
* Update existing entries safely

The system prioritizes **data integrity**, **human-readable storage**, and **clean CLI ergonomics**.

---

## Objectives

### Core Goals

* Provide a simple CLI interface for interacting with a YAML ledger
* Enforce structured, validated event data
* Ensure safe read-modify-write operations
* Maintain human-readable YAML output
* Keep architecture modular and extensible

### Non-Goals

* No database integration (file-based only)
* No GUI or web interface
* No concurrent multi-user editing support (single-user tool)

---

## Assumptions

* The ledger file (`events.yaml`) is the single source of truth
* Users interact via CLI only
* File size remains manageable (fits in memory)
* YAML structure remains stable and version-compatible
* Events are append-heavy with occasional updates
* Users provide valid ISO-8601 date inputs (`YYYY-MM-DD`)

---

## Technology Stack

* **Java 17+**
* **Picocli** – CLI parsing and subcommands
* **SnakeYAML** – YAML serialization/deserialization
* **Maven** – Build and packaging
* **JUnit 5** – Testing

---

## High-Level Architecture

```text
CLI Layer (Picocli)
        ↓
Service Layer (Business Logic)
        ↓
I/O Layer (Ledger Read/Write)
        ↓
YAML Codec (SnakeYAML)
```

---

## Package Structure

```text
com.example.replayer
├── Main.java
├── cli/
│   ├── ReplayCommand.java
│   ├── AddEventCommand.java
│   └── UpdateEventCommand.java
├── model/
│   ├── Event.java
│   ├── WorkEvent.java
│   └── EducationEvent.java
├── service/
│   ├── EventService.java
│   ├── EventValidator.java
│   └── ResumeEngine.java
├── io/
│   ├── LedgerReader.java
│   └── LedgerWriter.java
└── yaml/
    └── YamlCodec.java
```

---

## CLI Design

### Root Command

```sh
replay <ledger-file>
```

Renders the ledger into resume-style output.

---

### Add Event

```sh
replay add-event <ledger-file> \
  --eventType work \
  --company Acme \
  --title Engineer \
  --startDate 2020-01-01 \
  --endDate 2022-12-31
```

---

### Update Event

```sh
replay update-event <ledger-file> 0 --title "Senior Engineer"
```

---

## Data Model

### Base Fields

* `eventType`
* `startDate`
* `endDate`

### Work Event

* `company`
* `title`

### Education Event

* `institution`
* `degree`

---

## Component Responsibilities

### CLI Layer (`cli/`)

* Parses user input via Picocli
* Maps CLI arguments to domain objects
* Delegates execution to service layer

---

### Service Layer (`service/`)

#### `EventService`

* Handles add/update workflows
* Coordinates validation and persistence

#### `EventValidator`

* Validates required fields
* Ensures structural correctness

#### `ResumeEngine`

* Transforms events into formatted output

---

### I/O Layer (`io/`)

#### `LedgerReader`

* Reads YAML file into memory

#### `LedgerWriter`

* Writes full dataset back to disk

---

### YAML Layer (`yaml/`)

#### `YamlCodec`

* Encapsulates SnakeYAML configuration
* Ensures consistent formatting

---

## Persistence Strategy

* Always load the entire YAML file before modification
* Modify in-memory representation
* Write full file back atomically
* Never append raw text directly

---

## Validation Rules

### Work Event

* `company` required
* `title` required
* `startDate`, `endDate` required

### Education Event

* `institution` required
* `degree` required
* `startDate`, `endDate` required

### General

* `eventType` must be valid
* Index must exist for updates
* Dates must be valid ISO-8601

---

## YAML Format Guidelines

* Use block style (human-readable)
* Preserve field order
* Avoid unnecessary nesting
* Keep file diff-friendly

Example:

```yaml
- eventType: work
  company: Acme
  title: Engineer
  startDate: 2020-01-01
  endDate: 2022-12-31
```

---

## Error Handling Strategy

* Fail fast on validation errors
* Do not write partial updates
* Provide clear CLI error messages
* Exit with non-zero status on failure

---

## Testing Strategy

### Unit Tests

* Event validation
* YAML serialization/deserialization
* Resume rendering
* CLI parsing

### Integration Tests

* Full add-event workflow
* Full update-event workflow
* End-to-end ledger read/render

---

## Implementation Notes

### 1. Event Mapping Strategy

Use a lightweight mapping layer to convert between:

* CLI input → typed `Event` objects
* YAML → `Map<String, Object>` → domain objects

Consider introducing:

* `EventFactory` for object creation
* `YamlEventMapper` for conversions

---

### 2. Field Order Preservation

Use `LinkedHashMap` when serializing to YAML to maintain order.

---

### 3. CLI Design Best Practice

Keep CLI classes thin:

* No business logic in commands
* Only parsing and delegation

---

### 4. File Safety

* Write to a temp file, then replace original (optional improvement)
* Prevent corruption on crash

---

### 5. Extensibility

Future event types should require:

* New model class
* Validation rules
* Minimal changes elsewhere

---

### 6. Dependency Isolation

Keep SnakeYAML usage confined to `YamlCodec` and I/O layer to avoid leaking implementation details.

---

## Milestones

### Phase 1

* CLI skeleton
* Basic YAML read/write

### Phase 2

* Event models and validation
* Add/update functionality

### Phase 3

* Resume rendering
* Integration tests

### Phase 4

* Packaging (fat JAR)
* Error handling polish

---

## Future Enhancements

* Event filtering (by date, type)
* Sorting and weighting logic
* Export formats (JSON, Markdown resume)
* Interactive CLI prompts
* Schema versioning

---

## Summary

`replayer` is designed as a clean, modular Java CLI with clear separation of concerns:

* CLI handles input
* Services handle logic
* I/O manages persistence
* YAML remains human-readable and stable

The architecture favors simplicity, correctness, and long-term maintainability over premature complexity.

---
