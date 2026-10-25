
public class HexUtilsTest {

    public static void main(String[] args) {
        byte[] payload = {
            0x0A, 0x14, 0x01, 0x02, (byte) 0xFF
        };

        String actual = HexUtils.toHex(payload);
        String expected = "0A 14 01 02 FF";

        if (!expected.equals(actual)) {
            throw new AssertionError(
                    "Expected: " + expected
                    + ", but got: " + actual);
        }

        System.out.println("PASS: Hexadecimal conversion");
        System.out.println("Payload: " + actual);
        System.out.println("All hex tests passed!");
    }
}
