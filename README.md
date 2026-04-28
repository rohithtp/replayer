# Replayer

A Java tool for managing a YAML-based event ledger and rendering it as resume-style output. Available as both a **REST API** (Spring Boot) and a **CLI** (picocli), sharing a common domain library.

---

## Project structure

```
replayer/
├── replayer-common/   shared domain — model, service, YAML I/O
├── replayer-api/      Spring Boot REST API (port 8080)
└── replayer-cli/      picocli fat-JAR
```

| Branch | Status |
|--------|--------|
| `main` | stable CLI |
| `feature/spring-boot-api` | multi-module Spring Boot API |

---

## Requirements

| Requirement | Version |
|-------------|---------|
| Java | 17 or later |
| Maven | 3.6 or later |

---

## Build

```sh
# Build all modules (fat JARs in each module's target/)
mvn clean package -DskipTests

# Run all 52 tests
mvn test
```

---

## REST API (`replayer-api`)

### Start the server

```sh
java -jar replayer-api/target/replayer-api-1.0-SNAPSHOT.jar
```

The ledger file path defaults to `events.yaml` in the working directory. Override via:

```sh
java -jar replayer-api/target/replayer-api-1.0-SNAPSHOT.jar \
  --replayer.ledger.file=/path/to/events.yaml
```

Interactive API docs: `http://localhost:8080/swagger-ui.html`
OpenAPI spec: `http://localhost:8080/api-docs`

### Endpoints

| Method | Path | Status | Description |
|--------|------|--------|-------------|
| `GET` | `/api/events` | 200 | List all events |
| `POST` | `/api/events` | 201 | Add a new event |
| `PUT` | `/api/events/{index}` | 204 | Update event by 0-based index |
| `GET` | `/api/resume` | 200 | Render resume as plain text |

### `POST /api/events`

```json
{
  "eventType": "work",
  "company": "Acme Corp",
  "title": "Software Engineer",
  "startDate": "2020-01-01",
  "endDate": "2022-12-31"
}
```

```json
{
  "eventType": "education",
  "institution": "State University",
  "degree": "B.Sc. Computer Science",
  "startDate": "2015-09-01",
  "endDate": "2019-05-31"
}
```

### `PUT /api/events/{index}`

Only fields included in the body are updated; omitted fields are unchanged.

```json
{ "title": "Senior Software Engineer" }
```

### Error responses

| Situation | HTTP status | Body |
|-----------|-------------|------|
| Missing or invalid field | 400 | `{"error": "company is required"}` |
| Index out of range | 404 | `{"error": "Invalid event index: 5"}` |
| Unknown `eventType` | 400 | `{"error": "Unknown eventType: foo"}` |

---

## CLI (`replayer-cli`)

### Build

```sh
mvn clean package -DskipTests -pl replayer-cli -am
```

JAR: `replayer-cli/target/replayer-cli-1.0-SNAPSHOT.jar`

### Commands

#### Render ledger

```sh
java -jar replayer-cli/target/replayer-cli-1.0-SNAPSHOT.jar <ledgerFile>
```

#### Add event

```sh
java -jar replayer-cli/target/replayer-cli-1.0-SNAPSHOT.jar add-event <ledgerFile> \
  --eventType work \
  --company "Acme Corp" \
  --title "Software Engineer" \
  --startDate 2020-01-01 \
  --endDate 2022-12-31
```

```sh
java -jar replayer-cli/target/replayer-cli-1.0-SNAPSHOT.jar add-event <ledgerFile> \
  --eventType education \
  --institution "State University" \
  --degree "B.Sc. Computer Science" \
  --startDate 2015-09-01 \
  --endDate 2019-05-31
```

> **Note:** Option values that contain spaces must be quoted (e.g. `--title "Associate Consultant"`). Without quotes the shell splits the value and picocli errors with `Unmatched argument`.

#### Update event

```sh
java -jar replayer-cli/target/replayer-cli-1.0-SNAPSHOT.jar update-event <ledgerFile> <index> \
  [--company <name>] [--title <title>] \
  [--institution <name>] [--degree <degree>] \
  [--startDate <YYYY-MM-DD>] [--endDate <YYYY-MM-DD>]
```

`<index>` is 0-based. Only supplied fields are changed.

### CLI flags

| Flag | Description |
|------|-------------|
| `-h`, `--help` | Print help and exit |
| `-V`, `--version` | Print version and exit |

---

## Ledger file format

```yaml
- eventType: work
  company: Acme Corp
  title: Software Engineer
  startDate: '2020-01-01'
  endDate: '2022-12-31'
- eventType: education
  institution: State University
  degree: B.Sc. Computer Science
  startDate: '2015-09-01'
  endDate: '2019-05-31'
```

---

## Development

```sh
mvn test                # run all tests (52 total)
mvn verify              # tests + checkstyle + spotless
mvn spotless:apply      # auto-format Java source
```

Run tests for a single module:

```sh
mvn test -pl replayer-common
mvn test -pl replayer-api
```
