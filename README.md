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

This is an educational binary-parser project. It is not currently a verified decoder for genuine Qualcomm QXDM logs.
