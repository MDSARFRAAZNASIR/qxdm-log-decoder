
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class DiagnosticTimelineExporter {

    private DiagnosticTimelineExporter() {
    }

    public static void exportCsv(
            List<DiagnosticEvent> events,
            Path outputPath) throws IOException {

        List<DiagnosticEvent> sorted =
                DiagnosticAnalyzer.sortEvents(events);

        try (BufferedWriter writer = Files.newBufferedWriter(
                outputPath, StandardCharsets.UTF_8)) {

            writer.write(
                    "timestampMillis,eventType,servingCell,"
                    + "targetCell,attemptId,description");
            writer.newLine();

            for (DiagnosticEvent event : sorted) {
                writer.write(Long.toString(
                        event.getTimestampMillis()));
                writer.write(",");
                writer.write(csv(event.getEventType().name()));
                writer.write(",");
                writer.write(csv(event.getServingCell()));
                writer.write(",");
                writer.write(csv(event.getTargetCell()));
                writer.write(",");
                writer.write(csv(event.getAttemptId()));
                writer.write(",");
                writer.write(csv(event.getDescription()));
                writer.newLine();
            }
        }
    }

    private static String csv(String value) {
        if (value == null) {
            return "";
        }

        String escaped = value.replace("\"", "\"\"");

        if (escaped.contains(",")
                || escaped.contains("\"")
                || escaped.contains("\n")
                || escaped.contains("\r")) {
            return "\"" + escaped + "\"";
        }

        return escaped;
    }
}
