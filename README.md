# Replayer CLI

A Java command-line tool that manages a YAML-based event ledger and renders it as resume-style output. Events are stored as plain YAML so they can be version-controlled and edited by hand.

---

## Requirements

| Requirement | Version |
|-------------|---------|
| Java | 17 or later |
| Maven | 3.6 or later |

---

## Installation

```sh
git clone <repository-url>
cd replayer
mvn clean package -DskipTests
```

The fat JAR is written to `target/replayer-1.0-SNAPSHOT.jar`.

---

## Commands

### Global synopsis

```
java -jar target/replayer-1.0-SNAPSHOT.jar <ledgerFile>
java -jar target/replayer-1.0-SNAPSHOT.jar <command> <ledgerFile> [options]
```

| Flag | Description |
|------|-------------|
| `-h`, `--help` | Print help and exit |
| `-V`, `--version` | Print version and exit |

---

### `replay` — Render the ledger

Reads the ledger file and prints every event to stdout.

```sh
java -jar target/replayer-1.0-SNAPSHOT.jar <ledgerFile>
```

**Arguments**

| Argument | Required | Description |
|----------|----------|-------------|
| `<ledgerFile>` | Yes | Path to the YAML ledger file |

**Example**

```sh
java -jar target/replayer-1.0-SNAPSHOT.jar ./events.yaml
```

**Sample output**

```
{eventType=work, company=Acme, title=Software Engineer, startDate=2020-01-01, endDate=2022-12-31}
```

---

### `add-event` — Add a new event

Appends a new event to the ledger. Creates the file if it does not exist.

```sh
java -jar target/replayer-1.0-SNAPSHOT.jar add-event <ledgerFile> \
  --eventType <type> \
  [--company <name>] [--title <title>] \
  [--institution <name>] [--degree <degree>] \
  --startDate <YYYY-MM-DD> \
  --endDate   <YYYY-MM-DD>
```

**Arguments**

| Argument | Required | Description |
|----------|----------|-------------|
| `<ledgerFile>` | Yes | Path to the YAML ledger file |

**Options**

| Option | Required | Applies to | Description |
|--------|----------|------------|-------------|
| `--eventType` | Yes | all | Event type: `work` or `education` |
| `--company` | If `work` | `work` | Company name |
| `--title` | If `work` | `work` | Job title |
| `--institution` | If `education` | `education` | Institution name |
| `--degree` | If `education` | `education` | Degree obtained |
| `--startDate` | Yes | all | Start date in `YYYY-MM-DD` format |
| `--endDate` | Yes | all | End date in `YYYY-MM-DD` format |

> **Note:** Option values that contain spaces must be wrapped in quotes (e.g. `--title "Associate Consultant"`). Without quotes the shell splits the value into separate tokens and picocli will error with `Unmatched argument`.

**Examples**

Add a work event:

```sh
java -jar target/replayer-1.0-SNAPSHOT.jar add-event ./events.yaml \
  --eventType work \
  --company "Acme Corp" \
  --title "Software Engineer" \
  --startDate 2020-01-01 \
  --endDate 2022-12-31
```

Add an education event:

```sh
java -jar target/replayer-1.0-SNAPSHOT.jar add-event ./events.yaml \
  --eventType education \
  --institution "State University" \
  --degree "B.Sc. Computer Science" \
  --startDate 2015-09-01 \
  --endDate 2019-05-31
```

---

### `update-event` — Update an existing event

Merges the supplied fields into an existing event identified by its 0-based index. Only the fields you pass are changed; all other fields are left untouched.

```sh
java -jar target/replayer-1.0-SNAPSHOT.jar update-event <ledgerFile> <index> \
  [--company <name>] [--title <title>] \
  [--institution <name>] [--degree <degree>] \
  [--startDate <YYYY-MM-DD>] \
  [--endDate   <YYYY-MM-DD>]
```

**Arguments**

| Argument | Required | Description |
|----------|----------|-------------|
| `<ledgerFile>` | Yes | Path to the YAML ledger file |
| `<index>` | Yes | 0-based index of the event to update |

**Options**

| Option | Required | Description |
|--------|----------|-------------|
| `--company` | No | New company name |
| `--title` | No | New job title |
| `--institution` | No | New institution name |
| `--degree` | No | New degree |
| `--startDate` | No | New start date in `YYYY-MM-DD` format |
| `--endDate` | No | New end date in `YYYY-MM-DD` format |

**Examples**

Update the title of the first event:

```sh
java -jar target/replayer-1.0-SNAPSHOT.jar update-event ./events.yaml 0 \
  --title "Senior Software Engineer"
```

Update multiple fields at once:

```sh
java -jar target/replayer-1.0-SNAPSHOT.jar update-event ./events.yaml 0 \
  --company "New Corp" \
  --title "Staff Engineer" \
  --endDate 2023-06-30
```

---

## Ledger file format

Events are stored as a YAML list. Each entry is a map of fields.

**Work event**

```yaml
- eventType: work
  company: Acme Corp
  title: Software Engineer
  startDate: '2020-01-01'
  endDate: '2022-12-31'
```

**Education event**

```yaml
- eventType: education
  institution: State University
  degree: B.Sc. Computer Science
  startDate: '2015-09-01'
  endDate: '2019-05-31'
```

**Mixed ledger example**

```yaml
- eventType: education
  institution: State University
  degree: B.Sc. Computer Science
  startDate: '2015-09-01'
  endDate: '2019-05-31'
- eventType: work
  company: Acme Corp
  title: Software Engineer
  startDate: '2020-01-01'
  endDate: '2022-12-31'
- eventType: work
  company: New Corp
  title: Senior Software Engineer
  startDate: '2023-01-01'
  endDate: '2025-06-30'
```

---

## Error handling

| Situation | Behaviour |
|-----------|-----------|
| Ledger file does not exist (`add-event`) | A new file is created automatically |
| Invalid event index (`update-event`) | Exits with `Error: Invalid event index: <n>` |
| Missing required field on add | Exits with a validation error message |
| Unknown `--eventType` value | Exits with `Invalid event type: <value>` |

---

## Development

Run tests:

```sh
mvn test
```

Run tests and check code style:

```sh
mvn verify
```

Apply automatic formatting:

```sh
mvn spotless:apply
```
