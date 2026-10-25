
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class DiagnosticTimelineExporterTest {

    public static void main(String[] args) throws Exception {
        Path output = Files.createTempFile(
                "diagnostic-timeline-", ".csv");

        try {
            testChronologicalCsvAndEscaping(output);
            System.out.println(
                    "All DiagnosticTimelineExporter tests passed.");
        } finally {
            Files.deleteIfExists(output);
        }
    }

    private static void testChronologicalCsvAndEscaping(
            Path output) throws Exception {

        List<DiagnosticEvent> events = Arrays.asList(
                event(2000,
                        DiagnosticEvent.EventType.HANDOVER_COMMAND,
                        "attempt-1",
                        "Command, issued"),
                event(1000,
                        DiagnosticEvent.EventType.MEASUREMENT_REPORT,
                        null,
                        "Signal said \"weak\""));

        DiagnosticTimelineExporter.exportCsv(events, output);

        List<String> lines = Files.readAllLines(
                output, StandardCharsets.UTF_8);

        check(lines.size() == 3,
                "Expected a header and two event rows");

        check(lines.get(0).startsWith("timestampMillis,eventType"),
                "CSV header is incorrect");

        check(lines.get(1).startsWith(
                        "1000,MEASUREMENT_REPORT"),
                "Events should be exported chronologically");

        check(lines.get(1).contains("\"Signal said \"\"weak\"\"\""),
                "Quotes inside CSV fields should be escaped");

        check(lines.get(2).contains("\"Command, issued\""),
                "Commas inside CSV fields should be quoted");
    }

    private static DiagnosticEvent event(
            long timestamp,
            DiagnosticEvent.EventType type,
            String attemptId,
            String description) {

        return new DiagnosticEvent(
                timestamp,
                type,
                "Cell-A",
                "Cell-B",
                description,
                attemptId);
    }

    private static void check(
            boolean condition,
            String message) {

        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
