
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Benchmark {

    private static final int RECORDS = 100_000;
    private static final int PAYLOAD_LENGTH = 4;

    private static final Path FILE =
        Path.of("benchmark-data.bin");

    private static final class Result {
        long count;
        long elapsedNanos;
        long peakHeapDelta;
    }

    public static void main(String[] args) throws Exception {
        BinaryRecordParser parser = new BinaryRecordParser();

        System.out.println("Generating benchmark file...");
        createFile();

        System.out.println("File size: " + Files.size(FILE)
                + " bytes");
        System.out.println("Expected records: " + RECORDS);

        // Warm up both parsing paths before measuring.
        System.out.println("\nWarming up...");
        runList(parser);
        runStreaming(parser);

        System.gc();
        Thread.sleep(200);

        System.out.println("\nMeasuring list-based parsing...");
        Result listResult = runList(parser);

        System.gc();
        Thread.sleep(200);

        System.out.println("Measuring streaming parsing...");
        Result streamResult = runStreaming(parser);

        System.out.println("\n========== RESULTS ==========");

        printResult("List-based", listResult);
        printResult("Streaming", streamResult);

        boolean sameCount =
            listResult.count == streamResult.count
            && listResult.count == RECORDS;

        System.out.println("\nRecord counts match: " + sameCount);

        if (!sameCount) {
            throw new IllegalStateException(
                "Benchmark results do not match"
            );
        }

        System.out.println("Benchmark completed successfully.");
    }

    private static void createFile() throws IOException {
        try (OutputStream out = new BufferedOutputStream(
                Files.newOutputStream(FILE), 64 * 1024)) {

            for (int i = 0; i < RECORDS; i++) {
                int id = i & 0xFFFF;

                // Practice header: AA 55
                out.write(0xAA);
                out.write(0x55);

                // Two-byte big-endian message ID
                out.write((id >>> 8) & 0xFF);
                out.write(id & 0xFF);

                // Two-byte big-endian payload length
                out.write(0);
                out.write(PAYLOAD_LENGTH);

                // Four deterministic payload bytes
                out.write((i >>> 24) & 0xFF);
                out.write((i >>> 16) & 0xFF);
                out.write((i >>> 8) & 0xFF);
                out.write(i & 0xFF);
            }
        }
    }

    private static Result runList(
            BinaryRecordParser parser) throws Exception {

        Result result = new Result();
        System.gc();
        Thread.sleep(100);

        long baseline = usedHeap();
        Sampler sampler = new Sampler(baseline);
        sampler.start();

        long start = System.nanoTime();

        try (InputStream input = new BufferedInputStream(
                Files.newInputStream(FILE), 64 * 1024)) {

            List<BinaryRecord> records = parser.parse(input);
            result.count = records.size();

            result.elapsedNanos = System.nanoTime() - start;
            sampler.stop();

            // Keep the list alive while measuring retained records.
            if (records.size() > 0) {
                result.count = records.size();
            }
        } finally {
            sampler.stop();
        }

        result.peakHeapDelta = sampler.peakDelta();
        return result;
    }

    private static Result runStreaming(
            BinaryRecordParser parser) throws Exception {

        Result result = new Result();
        System.gc();
        Thread.sleep(100);

        long baseline = usedHeap();
        Sampler sampler = new Sampler(baseline);
        sampler.start();

        long start = System.nanoTime();

        try (InputStream input = new BufferedInputStream(
                Files.newInputStream(FILE), 64 * 1024)) {

            parser.parse(input, record -> result.count++);

            result.elapsedNanos = System.nanoTime() - start;
        } finally {
            sampler.stop();
        }

        result.peakHeapDelta = sampler.peakDelta();
        return result;
    }

    private static long usedHeap() {
        return ManagementFactory.getMemoryMXBean()
            .getHeapMemoryUsage().getUsed();
    }

    private static void printResult(
            String name, Result result) {

        System.out.printf(
            "%s: records=%d, time=%.2f ms, "
                + "approx. sampled heap increase=%.2f MiB%n",
            name,
            result.count,
            result.elapsedNanos / 1_000_000.0,
            result.peakHeapDelta / (1024.0 * 1024.0)
        );
    }

    private static final class Sampler {
        private final long baseline;
        private final AtomicBoolean running =
            new AtomicBoolean(false);
        private final AtomicLong peak = new AtomicLong();

        private Thread thread;

        Sampler(long baseline) {
            this.baseline = baseline;
            peak.set(baseline);
        }

        void start() {
            running.set(true);

            thread = new Thread(() -> {
                while (running.get()) {
                    long used = usedHeap();
                    peak.accumulateAndGet(used, Math::max);

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }

                peak.accumulateAndGet(usedHeap(), Math::max);
            });

            thread.setDaemon(true);
            thread.start();
        }

        void stop() throws InterruptedException {
            if (running.getAndSet(false) && thread != null) {
                thread.join();
            }
        }

        long peakDelta() {
            return Math.max(0, peak.get() - baseline);
        }
    }
}
