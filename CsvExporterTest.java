
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CsvExporterTest {

    public static void main(String[] args) throws IOException {
        Path output = Path.of("csv-test-output.csv");

        BinaryRecord record = new BinaryRecord(
                1,
                new byte[] { 0x0A, 0x14 }
        );

        try (BufferedWriter writer =
                     CsvExporter.open(output.toString())) {
            CsvExporter.writeRecord(writer, record);
        }

        System.out.println(
                Files.readString(output)
        );

        System.out.println("CSV export test completed!");
    }
}
