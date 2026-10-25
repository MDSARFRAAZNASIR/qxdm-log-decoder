
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ParserTest {

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }

        System.out.println("PASS: " + message);
    }

    public static void main(String[] args) throws Exception {
        BinaryRecordParser parser = new BinaryRecordParser();

        // Test 1: Incomplete metadata
        byte[] incompleteMetadata = {
            (byte) 0xAA, 0x55, 0x00, 0x01
        };

        try {
            parser.parse(new ByteArrayInputStream(incompleteMetadata));
            throw new AssertionError("Incomplete metadata was accepted");
        } catch (IOException expected) {
            check(true, "Incomplete metadata rejected");
        }

        // Test 2: Incomplete payload
        byte[] incompletePayload = {
            (byte) 0xAA, 0x55,
            0x00, 0x01,
            0x00, 0x02,
            0x0A
        };

        try {
            parser.parse(new ByteArrayInputStream(incompletePayload));
            throw new AssertionError("Incomplete payload was accepted");
        } catch (IOException expected) {
            check(true, "Incomplete payload rejected");
        }

        // Test 3: Excessive payload length
        byte[] excessiveLength = {
            (byte) 0xAA, 0x55,
            0x00, 0x01,
            0x10, 0x01
        };

        try {
            parser.parse(new ByteArrayInputStream(excessiveLength));
            throw new AssertionError("Excessive length was accepted");
        } catch (IOException expected) {
            check(true, "Excessive payload length rejected");
        }

        // Test 4: One valid record
        byte[] valid = {
            (byte) 0xAA, 0x55,
            0x00, 0x01,
            0x00, 0x02,
            0x0A, 0x14
        };

        List<BinaryRecord> records = parser.parse(
            new ByteArrayInputStream(valid)
        );

        check(records.size() == 1, "One record parsed");
        check(records.get(0).getMessageId() == 1, "Message ID is 1");
        check(records.get(0).getLength() == 2, "Payload length is 2");

        byte[] payload = records.get(0).getPayload();

        check((payload[0] & 0xFF) == 10, "First payload byte is 0A");
        check((payload[1] & 0xFF) == 20, "Second payload byte is 14");

        // Test 5: Invalid header
        byte[] invalid = {0x00, 0x00};

        try {
            parser.parse(new ByteArrayInputStream(invalid));
            throw new AssertionError("Invalid header was accepted");
        } catch (IOException expected) {
            check(true, "Invalid header rejected");
        }

        // New test 6: Empty input
        testEmptyInput();

        // New test 7: Multiple records in order
        testMultipleRecords();

        // New test 8: Zero-length payload
        testZeroLengthPayload();

        System.out.println();
        testStreamingApi();
        testMaximumPayloadLength();
        System.out.println("All tests passed!");
    }

    
private static void testStreamingApi() throws Exception {
    byte[] data = {
        (byte) 0xAA, 0x55, 0x00, 0x01, 0x00, 0x01, 0x11,
        (byte) 0xAA, 0x55, 0x00, 0x02, 0x00, 0x01, 0x22
    };

    BinaryRecordParser parser = new BinaryRecordParser();
    int[] count = {0};

    try (InputStream input = new ByteArrayInputStream(data)) {
        parser.parse(input, record -> {
            count[0]++;

            System.out.println(
                "Streaming record ID: " + record.getMessageId()
            );
        });
    }

    check(count[0] == 2, "Streaming API processes two records");
}


private static void testMaximumPayloadLength() throws Exception {
    int maxLength = 4096;

    byte[] data = new byte[6 + maxLength];

    data[0] = (byte) 0xAA;
    data[1] = 0x55;
    data[2] = 0x00;
    data[3] = 0x01;
    data[4] = (byte) (maxLength >>> 8);
    data[5] = (byte) maxLength;

    BinaryRecordParser parser = new BinaryRecordParser();

    try (InputStream input = new ByteArrayInputStream(data)) {
        List<BinaryRecord> records = parser.parse(input);

        check(
            records.size() == 1
                && records.get(0).getLength() == maxLength,
            "Maximum payload length of 4096 accepted"
        );
    }
}



    private static void testEmptyInput() throws Exception {
        BinaryRecordParser parser = new BinaryRecordParser();

        try (InputStream input =
                 new ByteArrayInputStream(new byte[0])) {

            List<BinaryRecord> records = parser.parse(input);

            check(records.isEmpty(), "Empty input handled");
        }
    }

    private static void testMultipleRecords() throws Exception {
        byte[] data = {
            (byte) 0xAA, 0x55, 0x00, 0x01, 0x00, 0x01, 0x11,
            (byte) 0xAA, 0x55, 0x00, 0x02, 0x00, 0x01, 0x22
        };

        BinaryRecordParser parser = new BinaryRecordParser();

        try (InputStream input = new ByteArrayInputStream(data)) {
            List<BinaryRecord> records = parser.parse(input);

            check(records.size() == 2, "Two records parsed");

            check(
                records.get(0).getMessageId() == 1
                    && records.get(1).getMessageId() == 2,
                "Multiple records parsed in order"
            );
        }
    }

    private static void testZeroLengthPayload() throws Exception {
        byte[] data = {
            (byte) 0xAA, 0x55, 0x12, 0x34, 0x00, 0x00
        };

        BinaryRecordParser parser = new BinaryRecordParser();

        try (InputStream input = new ByteArrayInputStream(data)) {
            List<BinaryRecord> records = parser.parse(input);

            check(
                records.size() == 1
                    && records.get(0).getMessageId() == 0x1234
                    && records.get(0).getLength() == 0
                    && records.get(0).getPayload().length == 0,
                "Zero-length payload accepted"
            );
        }
    }

    
private static void testUnsignedMessageId() throws Exception {
    byte[] data = {
        (byte) 0xAA, 0x55,
        (byte) 0xFF, (byte) 0xFF,
        0x00, 0x00
    };

    BinaryRecordParser parser = new BinaryRecordParser();

    List<BinaryRecord> records = parser.parse(
        new ByteArrayInputStream(data)
    );

    check(
        records.size() == 1
            && records.get(0).getMessageId() == 65535,
        "Unsigned message ID 65535 decoded correctly"
    );
}

private static void testStreamingRejectsInvalidHeader()
        throws Exception {
    byte[] data = {0x00, 0x00};

    BinaryRecordParser parser = new BinaryRecordParser();
    int[] count = {0};

    try {
        parser.parse(
            new ByteArrayInputStream(data),
            record -> count[0]++
        );

        throw new AssertionError(
            "Streaming parser accepted an invalid header"
        );
    } catch (IOException expected) {
        check(
            count[0] == 0,
            "Streaming API rejects invalid header"
        );
    }
}

private static void testValidRecordBeforeMalformedRecord()
        throws Exception {
    byte[] data = {
        // Valid record: ID 1, one-byte payload
        (byte) 0xAA, 0x55, 0x00, 0x01, 0x00, 0x01, 0x11,

        // Invalid header follows the valid record
        0x00, 0x00
    };

    BinaryRecordParser parser = new BinaryRecordParser();
    int[] count = {0};

    try {
        parser.parse(
            new ByteArrayInputStream(data),
            record -> count[0]++
        );

        throw new AssertionError(
            "Malformed second record was accepted"
        );
    } catch (IOException expected) {
        check(
            count[0] == 1,
            "Valid record emitted before malformed record is rejected"
        );
    }
}

}
