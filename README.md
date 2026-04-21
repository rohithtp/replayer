## Replayer CLI

### Overview

`replayer` is a Java-based command-line tool designed to manage and render a YAML-based event ledger into structured, resume-style output. It allows users to read, add, and update events safely while maintaining data integrity and human-readable storage.

### Requirements

- **Java 17+**
- **Maven**

### Installation

1. Clone the repository:
   ```sh
   git clone <repository-url>
   cd replayer
   ```

2. Build the project using Maven:
   ```sh
   mvn clean package
   ```

3. The executable JAR file will be located in the `target` directory.

### Usage

#### Render Ledger

To render the ledger into resume-style output:

```sh
java -jar target/replayer-1.0-SNAPSHOT.jar replay <ledger-file>
```

Replace `<ledger-file>` with the path to your YAML event ledger file.

#### Add Event

To add a new event:

```sh
java -jar target/replayer-1.0-SNAPSHOT.jar add-event <ledger-file> \
  --eventType work \
  --company Acme \
  --title Engineer \
  --startDate 2020-01-01 \
  --endDate 2022-12-31
```

Replace `<ledger-file>` with the path to your YAML event ledger file and provide the necessary event details.

#### Update Event

To update an existing event:

```sh
java -jar target/replayer-1.0-SNAPSHOT.jar update-event <ledger-file> 0 --title "Senior Engineer"
```

Replace `<ledger-file>` with the path to your YAML event ledger file, `0` with the index of the event you want to update, and provide the necessary updates.

### Example YAML Event Ledger

```yaml
- eventType: work
  company: Acme
  title: Engineer
  startDate: 2020-01-01
  endDate: 2022-12-31
```

### Contributing

Contributions are welcome! Please fork the repository and submit a pull request.

### License

This project is licensed under the MIT License.
