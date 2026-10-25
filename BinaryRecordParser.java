import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BinaryRecordParser {

    private static final int HEADER_1 = 0xAA;
    private static final int HEADER_2 = 0x55;
    private static final int MAX_PAYLOAD_LENGTH = 4096;

    // Streaming API: process each record immediately.
    public void parse(
            InputStream input,
            Consumer<BinaryRecord> consumer) throws IOException {

        if (input == null || consumer == null) {
            throw new IllegalArgumentException(
                "Input stream and consumer must not be null"
            );
        }

        while (true) {
            int first = input.read();

            // End of file between complete records
            if (first == -1) {
                return;
            }

            int second = input.read();

            if (second == -1) {
                throw new EOFException(
                    "Incomplete record header"
                );
            }

            // Validate the record header
            if (first != HEADER_1 || second != HEADER_2) {
                throw new IOException(
                    String.format(
                        "Invalid header: %02X %02X",
                        first, second
                    )
                );
            }

            // Read the two-byte message ID and two-byte length
            byte[] metadata = readExactly(
                input, 4, "record metadata"
            );

            int messageId = unsignedShort(metadata, 0);
            int length = unsignedShort(metadata, 2);

            // Validate before allocating the payload
            if (length > MAX_PAYLOAD_LENGTH) {
                throw new IOException(
                    "Payload too large: " + length
                );
            }



            byte[] payload = readExactly(
                input, length, "record payload"
            );

            BinaryRecord record =
                new BinaryRecord(messageId, payload);

            // Hand off this record before reading the next one
            consumer.accept(record);
        }
    }

    // Compatibility API for existing tests or callers that need a List.
    // Note: this method intentionally stores all parsed records.
    public List<BinaryRecord> parse(InputStream input)
            throws IOException {

        List<BinaryRecord> records = new ArrayList<>();
        parse(input, records::add);
        return records;
    }

    // private byte[] readExactly(
    //         InputStream input, int length, String field)
    //         throws IOException {

    //     byte[] data = new byte[length];
    //     int offset = 0;

    //     while (offset < length) {
    //         int count = input.read(
    //             data, offset, length - offset
    //         );

    //         if (count == -1) {
    //             throw new EOFException(
    //                 "Incomplete " + field
    //             );
    //         }

    //         offset += count;
    //     }

    //     return data;
    // }
    
private byte[] readExactly(
        InputStream input,
        int length,
        String field
) throws IOException {

    byte[] data = new byte[length];
    int offset = 0;

    while (offset < length) {
        int count = input.read(
            data, offset, length - offset
        );

        if (count == -1) {
            throw new EOFException(
                "Incomplete " + field
            );
        }

        if (count == 0) {
            // Fallback: try reading a single byte.
            int value = input.read();

            if (value == -1) {
                throw new EOFException(
                    "Incomplete " + field
                );
            }

            data[offset++] = (byte) value;
        } else {
            offset += count;
        }
    }

    return data;
}


    private int unsignedShort(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 8)
                | (data[offset + 1] & 0xFF);
    }
}



