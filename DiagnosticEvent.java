
public final class DiagnosticEvent {

    public enum EventType {
        MEASUREMENT_REPORT,
        HANDOVER_COMMAND,
        HANDOVER_COMPLETE,
        RADIO_LINK_FAILURE,
        REESTABLISHMENT_STARTED
    }

    private final long timestampMillis;
    private final EventType eventType;
    private final String servingCell;
    private final String targetCell;
    private final String description;
    private final String attemptId;

    public DiagnosticEvent(
            long timestampMillis,
            EventType eventType,
            String servingCell,
            String targetCell,
            String description) {
        this(timestampMillis, eventType, servingCell,
                targetCell, description, null);
    }

    public DiagnosticEvent(
            long timestampMillis,
            EventType eventType,
            String servingCell,
            String targetCell,
            String description,
            String attemptId) {

        if (timestampMillis < 0) {
            throw new IllegalArgumentException(
                    "Timestamp cannot be negative");
        }

        if (eventType == null) {
            throw new IllegalArgumentException(
                    "Event type cannot be null");
        }

        this.timestampMillis = timestampMillis;
        this.eventType = eventType;
        this.servingCell = servingCell;
        this.targetCell = targetCell;
        this.description = description;
        this.attemptId = attemptId;
    }

    public long getTimestampMillis() {
        return timestampMillis;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getServingCell() {
        return servingCell;
    }

    public String getTargetCell() {
        return targetCell;
    }

    public String getDescription() {
        return description;
    }

    public String getAttemptId() {
        return attemptId;
    }
}

