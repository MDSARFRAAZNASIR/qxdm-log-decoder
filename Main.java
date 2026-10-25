
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println(
                "Usage: java Main <binary-file> <output.csv>"
            );
            return;
        }

        Path inputFile = Path.of(args[0]);
        Path outputFile = Path.of(args[1]);

        if (!Files.isRegularFile(inputFile)
                || !Files.isReadable(inputFile)) {
            System.err.println(
                "Error: Input file does not exist "
                + "or is not readable: " + inputFile
            );
            return;
        }

        if (inputFile.toAbsolutePath().normalize()
                .equals(outputFile.toAbsolutePath().normalize())) {
            System.err.println(
                "Error: Input and output files must be different."
            );
            return;
        }

        BinaryRecordParser parser = new BinaryRecordParser();
        int[] recordCount = {0};

        try (
            InputStream input = new BufferedInputStream(
                Files.newInputStream(inputFile),
                64 * 1024
            );

            BufferedWriter csvWriter =
                CsvExporter.open(outputFile.toString())
        ) {
            System.out.println("Streaming Binary Record Decoder");
            System.out.println("================================");
            System.out.println("Input: " + inputFile);
            System.out.println("Output: " + outputFile);
            System.out.println();

            parser.parse(input, record -> {
                try {
                    System.out.println(
                        "Message ID: " + record.getMessageId()
                    );

                    System.out.println(
                        "Payload length: " + record.getLength()
                    );

                    System.out.println(
                        "Payload (HEX): "
                        + HexUtils.toHex(record.getPayload())
                    );

                    CsvExporter.writeRecord(csvWriter, record);
                    recordCount[0]++;

                    System.out.println("---------------------");

                } catch (IOException e) {
                    throw new java.io.UncheckedIOException(e);
                }
            });

            System.out.println("Total records: " + recordCount[0]);
            System.out.println("CSV export completed successfully.");

        } catch (Exception e) {
            System.err.println(
                "Error: " + e.getMessage()
            );
        }
    }
    
}





