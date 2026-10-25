
public final class HexUtils {

    private static final char[] HEX =
            "0123456789ABCDEF".toCharArray();

    private HexUtils() {
        // Utility class; no objects needed.
    }

    public static String toHex(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException(
                    "Byte array cannot be null");
        }

        char[] result = new char[bytes.length * 3];

        for (int i = 0; i < bytes.length; i++) {
            int value = bytes[i] & 0xFF;

            int position = i * 3;
            result[position] = HEX[value >>> 4];
            result[position + 1] = HEX[value & 0x0F];
            result[position + 2] = ' ';
        }

        return new String(result).trim();
    }
}
