
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

        boolean handoverPending = false;

        for (DiagnosticEvent event : sorted) {
            switch (event.getEventType()) {
                case HANDOVER_COMMAND:
                    handoverPending = true;
                    break;

                case HANDOVER_COMPLETE:
                    handoverPending = false;
                    break;

                case RADIO_LINK_FAILURE:
                    if (handoverPending) {
                        findings.add(
                                "Radio-link failure occurred after "
                                + "a handover command and before "
                                + "a recorded handover completion "
                                + "at timestamp "
                                + event.getTimestampMillis()
                                + ". Investigate related signaling "
                                + "and recovery events.");

                        handoverPending = false;
                    }
                    break;

                default:
                    break;
            }
        }

        return findings;
    }
}
