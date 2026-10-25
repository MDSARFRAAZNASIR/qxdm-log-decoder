
import java.util.Arrays;
import java.util.List;

public class DiagnosticAnalyzerTest {

    public static void main(String[] args) {
        testChronologicalSorting();
        testHandoverFailurePattern();
        testSuccessfulHandover();
        System.out.println(
                "All DiagnosticAnalyzer tests passed.");
    }

    private static void testChronologicalSorting() {
        List<DiagnosticEvent> events = Arrays.asList(
                event(3000,
                        DiagnosticEvent.EventType.RADIO_LINK_FAILURE),
                event(1000,
                        DiagnosticEvent.EventType.MEASUREMENT_REPORT));

        List<DiagnosticEvent> sorted =
                DiagnosticAnalyzer.sortEvents(events);

        check(sorted.get(0).getTimestampMillis() == 1000,
                "Events should be sorted chronologically");

        check(events.get(0).getTimestampMillis() == 3000,
                "Original list should remain unchanged");
    }

    private static void testHandoverFailurePattern() {
        List<DiagnosticEvent> events = Arrays.asList(
                event(1000,
                        DiagnosticEvent.EventType.HANDOVER_COMMAND),
                event(3000,
                        DiagnosticEvent.EventType.RADIO_LINK_FAILURE));

        List<String> findings =
                DiagnosticAnalyzer.analyze(events);

        check(findings.size() == 1,
                "Expected one handover-related finding");
    }

    private static void testSuccessfulHandover() {
        List<DiagnosticEvent> events = Arrays.asList(
                event(1000,
                        DiagnosticEvent.EventType.HANDOVER_COMMAND),
                event(2000,
                        DiagnosticEvent.EventType.HANDOVER_COMPLETE),
                event(3000,
                        DiagnosticEvent.EventType.RADIO_LINK_FAILURE));

        List<String> findings =
                DiagnosticAnalyzer.analyze(events);

        check(findings.isEmpty(),
                "Failure after completion should not match "
                + "this simplified pattern");
    }

    private static DiagnosticEvent event(
            long timestamp,
            DiagnosticEvent.EventType type) {

        return new DiagnosticEvent(
                timestamp,
                type,
                "Cell-A",
                "Cell-B",
                "Synthetic practice event");
    }

    private static void check(
            boolean condition,
            String message) {

        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
