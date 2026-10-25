
import java.util.Arrays;
import java.util.List;

public class DiagnosticAnalyzerTest {

    public static void main(String[] args) {
        testChronologicalSorting();
        testOverlappingHandoverAttempts();
        testSuccessfulHandover();
        testFailureWithoutAttemptIdIsIgnored();

        System.out.println(
                "All DiagnosticAnalyzer tests passed.");
    }

    private static void testChronologicalSorting() {
        List<DiagnosticEvent> events = Arrays.asList(
                event(3000,
                        DiagnosticEvent.EventType.RADIO_LINK_FAILURE,
                        "attempt-A"),
                event(1000,
                        DiagnosticEvent.EventType.MEASUREMENT_REPORT,
                        null));

        List<DiagnosticEvent> sorted =
                DiagnosticAnalyzer.sortEvents(events);

        check(sorted.get(0).getTimestampMillis() == 1000,
                "Events should be sorted chronologically");

        check(events.get(0).getTimestampMillis() == 3000,
                "Original list should remain unchanged");
    }

    private static void testOverlappingHandoverAttempts() {
        List<DiagnosticEvent> events = Arrays.asList(
                event(1000,
                        DiagnosticEvent.EventType.HANDOVER_COMMAND,
                        "attempt-A"),
                event(2000,
                        DiagnosticEvent.EventType.HANDOVER_COMMAND,
                        "attempt-B"),
                event(3000,
                        DiagnosticEvent.EventType.RADIO_LINK_FAILURE,
                        "attempt-A"),
                event(4000,
                        DiagnosticEvent.EventType.HANDOVER_COMPLETE,
                        "attempt-B"),
                event(5000,
                        DiagnosticEvent.EventType.RADIO_LINK_FAILURE,
                        "attempt-B"));

        List<String> findings =
                DiagnosticAnalyzer.analyze(events);

        check(findings.size() == 1,
                "Only the unresolved attempt-A should be flagged");

        check(findings.get(0).contains("attempt-A"),
                "Finding should identify attempt-A");

        check(!findings.get(0).contains("attempt-B"),
                "Finding should not identify attempt-B");
    }

    private static void testSuccessfulHandover() {
        List<DiagnosticEvent> events = Arrays.asList(
                event(1000,
                        DiagnosticEvent.EventType.HANDOVER_COMMAND,
                        "attempt-C"),
                event(2000,
                        DiagnosticEvent.EventType.HANDOVER_COMPLETE,
                        "attempt-C"),
                event(3000,
                        DiagnosticEvent.EventType.RADIO_LINK_FAILURE,
                        "attempt-C"));

        List<String> findings =
                DiagnosticAnalyzer.analyze(events);

        check(findings.isEmpty(),
                "Failure after completion should not match "
                + "this simplified pattern");
    }

    private static void testFailureWithoutAttemptIdIsIgnored() {
        List<DiagnosticEvent> events = Arrays.asList(
                event(1000,
                        DiagnosticEvent.EventType.HANDOVER_COMMAND,
                        null),
                event(2000,
                        DiagnosticEvent.EventType.RADIO_LINK_FAILURE,
                        null));

        List<String> findings =
                DiagnosticAnalyzer.analyze(events);

        check(findings.isEmpty(),
                "Events without attempt IDs should not be correlated");
    }

    private static DiagnosticEvent event(
            long timestamp,
            DiagnosticEvent.EventType type,
            String attemptId) {

        return new DiagnosticEvent(
                timestamp,
                type,
                "Cell-A",
                "Cell-B",
                "Synthetic practice event",
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
