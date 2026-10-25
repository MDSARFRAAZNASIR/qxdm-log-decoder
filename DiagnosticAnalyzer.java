
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DiagnosticAnalyzer {

    private DiagnosticAnalyzer() {
    }

    public static List<DiagnosticEvent> sortEvents(
            List<DiagnosticEvent> events) {

        List<DiagnosticEvent> sorted =
                new ArrayList<>(events);

        Collections.sort(
                sorted,
                Comparator.comparingLong(
                        DiagnosticEvent::getTimestampMillis));

        return sorted;
    }

    public static List<String> analyze(
            List<DiagnosticEvent> events) {

        List<DiagnosticEvent> sorted = sortEvents(events);
        List<String> findings = new ArrayList<>();

        Map<String, DiagnosticEvent> pending =
                new HashMap<>();

        for (DiagnosticEvent event : sorted) {
            String attemptId = event.getAttemptId();

            switch (event.getEventType()) {
                case HANDOVER_COMMAND:
                    if (hasAttemptId(attemptId)) {
                        pending.put(attemptId, event);
                    }
                    break;

                case HANDOVER_COMPLETE:
                    if (hasAttemptId(attemptId)) {
                        pending.remove(attemptId);
                    }
                    break;

                case RADIO_LINK_FAILURE:
                    if (hasAttemptId(attemptId)
                            && pending.containsKey(attemptId)) {

                        DiagnosticEvent command =
                                pending.remove(attemptId);

                        findings.add(
                                "Radio-link failure at timestamp "
                                + event.getTimestampMillis()
                                + " followed handover attempt "
                                + attemptId
                                + " from cell "
                                + command.getServingCell()
                                + " toward cell "
                                + command.getTargetCell()
                                + ". Investigate related signaling "
                                + "and recovery events.");
                    }
                    break;

                default:
                    break;
            }
        }

        return findings;
    }

    private static boolean hasAttemptId(String attemptId) {
        return attemptId != null && !attemptId.trim().isEmpty();
    }
}
