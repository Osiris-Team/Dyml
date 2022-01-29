package examples.dyml;

import com.osiris.dyml.Dyml;
import com.osiris.dyml.exceptions.IllegalListException;
import com.osiris.dyml.exceptions.YamlReaderException;
import com.osiris.dyml.exceptions.YamlWriterException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

public class SimpleExample {
    @Test
    void test() throws YamlReaderException, IOException, IllegalListException, YamlWriterException {

        // 1. Load
        Dyml dyml = Dyml.from("key1 value\n" +
                "  child value\n" +
                "key2 value\n"); // Can be String/InputStream or File with .fromFile()
        System.out.println("BEFORE:");
        dyml.debugPrint(System.out);

        // 2. Modify
        Dyml key1 = dyml.get("key1");
        key1.key = "my-key";
        key1.value.set("my-value");
        key1.comments = Arrays.asList("First comment line", "Second comment line");
        key1.add(0, "new-key").value.set("value");

        System.out.println("AFTER:");
        dyml.debugPrint(System.out);

        // 3. Save
        String text = dyml.toText(); // Save to file with dyml.toFile(file);

        // Note for Dyml devs: Cannot assertEquals because line separators are different on Windows
    }
}
