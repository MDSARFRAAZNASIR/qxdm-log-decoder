<!-- 
![Java CI](https://github.com/MDSARFRAAZNASIR/qxdm-log-decoder/actions/workflows/java-ci.yml/badge.svg)


# QXDM Log Decoder — Java Binary Parser

A Java 11 learning project demonstrating binary record parsing, input validation, streaming processing, hexadecimal conversion, and CSV export.

> **Project status:** The current implementation uses a synthetic binary format for educational and testing purposes. Compatibility with genuine Qualcomm QXDM log files has not yet been implemented or verified.

## Features

- Binary input using buffered streams
- Streaming record processing with a `Consumer<BinaryRecord>`
- Big-endian message ID and payload-length decoding
- Header and payload-length validation
- Maximum payload limit of 4096 bytes
- Hexadecimal payload output
- CSV export with a configurable output filename
- Automated tests for malformed input and boundary conditions
- Basic benchmark comparing list-based and streaming parsing

## Technology Stack

- Java 11
- Java I/O and NIO
- Collections and functional interfaces
- Command-line execution
- CSV output

## Synthetic Record Format

Each practice record contains:

| Field | Size |
|---|---:|
| Header (`AA 55`) | 2 bytes |
| Message ID | 2 bytes |
| Payload length | 2 bytes |
| Payload | Variable, up to 4096 bytes |

The message ID and payload length use big-endian byte order.

This format is invented for this project and does not represent a verified QXDM file specification.

## Run the Project

Compile all Java source files:

```bash
javac *.java
```

Run the decoder with an input file and output CSV filename:

```bash
java Main qxdm_decoder_practice_sample.bin final-report.csv
```

The program prints record information and writes decoded records to the specified CSV file.

## Run Tests

```bash
java ParserTest
java HexUtilsTest
```

The parser tests cover incomplete metadata, incomplete payloads, excessive payload lengths, invalid headers, empty input, multiple records, zero-length payloads, streaming, and the maximum allowed payload size.

## Project Structure

```text
qxdm-log-decoder/
├── Main.java
├── BinaryRecord.java
├── BinaryRecordParser.java
├── ParserTest.java
├── HexUtils.java
├── HexUtilsTest.java
├── CsvExporter.java
├── Benchmark.java
├── qxdm_decoder_practice_sample.bin
└── README.md
```

## Future Improvements

- Add verified support for a genuine diagnostic log format
- Export richer structured information to JSON
- Improve error reporting and automated test coverage
- Write output to a temporary file and publish it only after successful parsing
- Add repeatable performance benchmarks

## Disclaimer

This is an educational binary-parser project. It is not currently a verified decoder for genuine Qualcomm QXDM logs. -->


# QXDM Log Decoder — Binary Parsing Learning Project

[![Java CI](https://github.com/MDSARFRAAZNASIR/qxdm-log-decoder/actions/workflows/java-ci.yml/badge.svg)](https://github.com/MDSARFRAAZNASIR/qxdm-log-decoder/actions/workflows/java-ci.yml)

An educational Java project exploring binary parsing, diagnostic event analysis, and cellular handover troubleshooting concepts.

> **Important:** This project currently parses a custom synthetic binary format. It does not yet decode genuine Qualcomm QXDM log files. The diagnostic events used for analysis are also synthetic practice data.

## Project Goals

- Learn how binary data is structured and parsed in Java.
- Validate record headers, message IDs, payload lengths, and incomplete input.
- Convert parsed records into CSV reports.
- Understand diagnostic event timelines and basic handover-failure investigation.
- Practice automated testing, code quality checks, and GitHub Actions CI.

## Features

### Binary Parser
- Parses records from a streaming input.
- Supports both streaming and list-based parsing APIs.
- Validates record headers and payload lengths.
- Handles malformed records and incomplete input.
- Supports payloads up to 4096 bytes.

### CSV Export
- Exports parsed binary records into CSV format.
- Protects existing output reports when parsing fails.

### Diagnostic Event Analysis
- Represents timestamped diagnostic events.
- Sorts events chronologically.
- Tracks synthetic handover attempts using attempt identifiers.
- Flags selected event patterns for further investigation.

### Diagnostic Timeline Export
- Exports synthetic diagnostic events to CSV.
- Handles commas, quotation marks, and newlines in CSV fields.
- Includes automated tests for ordering and CSV escaping.

### Automated Quality Checks
- Java compilation.
- Parser, hexadecimal utility, and CSV exporter tests.
- Diagnostic analyzer and timeline exporter tests.
- Checkstyle validation through GitHub Actions.

## Technologies

- Java 11
- Git and GitHub
- GitHub Actions
- Checkstyle
- CSV and binary file processing

## Synthetic Binary Record Format

The educational parser uses this custom record structure:

| Field | Size | Description |
|---|---:|---|
| Header | 2 bytes | `AA 55` |
| Message ID | 2 bytes | Unsigned, big-endian |
| Payload length | 2 bytes | Unsigned, big-endian |
| Payload | Variable | Maximum 4096 bytes |

This structure is defined for learning purposes. It is **not a representation of the genuine QXDM file format**.

## Project Structure

```text
qxdm-log-decoder/
├── Main.java
├── BinaryRecord.java
├── BinaryRecordParser.java
├── ParserTest.java
├── Benchmark.java
├── CsvExporter.java
├── CsvExporterTest.java
├── HexUtils.java
├── HexUtilsTest.java
├── DiagnosticEvent.java
├── DiagnosticAnalyzer.java
├── DiagnosticAnalyzerTest.java
├── DiagnosticTimelineExporter.java
├── DiagnosticTimelineExporterTest.java
├── config/
│   └── checkstyle/
│       └── checkstyle.xml
└── .github/
    └── workflows/
        └── java-ci.yml
```

## Requirements

- JDK 11 or a compatible Java development environment.
- Git, if you want to clone the repository.

## Run Locally

Clone the repository:

```bash
git clone https://github.com/MDSARFRAAZNASIR/qxdm-log-decoder.git
cd qxdm-log-decoder
```

Compile the Java source files:

```bash
javac *.java
```

Run the automated tests:

```bash
java ParserTest
java HexUtilsTest
java CsvExporterTest
java DiagnosticAnalyzerTest
java DiagnosticTimelineExporterTest
```

Run the binary parser using an input file and an output CSV path:

```bash
java Main input.bin output.csv
```

Use a compatible synthetic binary input file for this command.

## Learning Cellular Diagnostics

The project introduces concepts related to cellular diagnostic analysis:

- Serving cells and neighboring cells.
- Radio measurements such as RSRP, RSRQ, and SINR.
- LTE handover procedures.
- Radio-link failure and recovery events.
- Timestamped diagnostic timelines.

For LTE radio resource control (RRC) behavior, consult the official [3GPP TS 36.331 specification](https://portal.3gpp.org/desktopmodules/Specifications/SpecificationDetails.aspx?specificationId=2440).

## Current Limitations

- Genuine Qualcomm QXDM `.isf`, `.dlf`, or other diagnostic log compatibility has not been implemented or verified.
- No genuine QXDM capture files were available during development.
- The diagnostic analyzer operates on synthetic event objects, not decoded modem messages.
- The analyzer identifies selected patterns for investigation; it does not establish the root cause of a network interruption.
- Actual diagnostic decoding requires reliable format documentation and suitable legitimate sample data.

## Future Learning

Potential future work includes studying documented diagnostic formats, understanding actual LTE RRC message encoding, and validating a decoder against legitimate sample data.

These are future learning goals, not currently implemented features.

## Author

**Md Sarfraaz Nasir**

GitHub: [MDSARFRAAZNASIR](https://github.com/MDSARFRAAZNASIR)

---

*This repository is an educational project for learning Java binary parsing and cellular diagnostic concepts. It is not an official Qualcomm product or a verified QXDM decoder.*
