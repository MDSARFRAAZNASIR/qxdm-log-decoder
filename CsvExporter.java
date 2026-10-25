
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class CsvExporter {

    private CsvExporter() {
    }

    public static BufferedWriter open(String fileName)
            throws IOException {

        Path path = Paths.get(fileName);

        BufferedWriter writer = Files.newBufferedWriter(
                path,
                StandardCharsets.UTF_8
        );

        writer.write("Message ID,Payload Length,Payload HEX");
        writer.newLine();

        return writer;
    }

    public static void writeRecord(
            BufferedWriter writer,
            BinaryRecord record) throws IOException {

        writer.write(Integer.toString(record.getMessageId()));
        writer.write(",");
        writer.write(Integer.toString(record.getPayload().length));
        writer.write(",");
        writer.write("\"");
        writer.write(HexUtils.toHex(record.getPayload()));
        writer.write("\"");
        writer.newLine();
    }
}
