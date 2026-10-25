
public class BinaryRecord {
    private final int messageId;
    private final byte[] payload;

    public BinaryRecord(int messageId, byte[] payload) {
        this.messageId = messageId;
        this.payload = payload.clone();
    }

    public int getMessageId() {
        return messageId;
    }

    public int getLength() {
        return payload.length;
    }

    public byte[] getPayload() {
        return payload.clone();
    }
}
