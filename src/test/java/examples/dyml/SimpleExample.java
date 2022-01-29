package examples.dyml;

import com.osiris.dyml.Dyml;
import com.osiris.dyml.SmartString;
import com.osiris.dyml.exceptions.IllegalListException;
import com.osiris.dyml.exceptions.YamlReaderException;
import com.osiris.dyml.exceptions.YamlWriterException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class SimpleExample {
    @Test
    void test() throws YamlReaderException, IOException, IllegalListException, YamlWriterException {
        String input, output;
        input = "key1 value\n" +
                "  child value\n" +
                "key2 value\n";

        // 1. Load
        Dyml dyml = Dyml.from(input); // Can be String/InputStream or File with .fromFile()
        System.out.println("BEFORE:");
        dyml.printSections(System.out);

        // 2. Modify
        Dyml key1 = dyml.get("key1");
        key1.key = "my-key";
        key1.value.set("my-value");
        //key1.comments = Arrays.asList("First comment line", "Second comment line");
        key1.children.add(new Dyml("bad-boi", new SmartString("value"), null));

        System.out.println("AFTER:");
        dyml.printSections(System.out);

        // 3. Save
        output = dyml.asString(); // Save to file with dyml.toFile(file);

        Assertions.assertEquals("" +
                " First comment line\n" +
                " Second comment line\n" +
                "my-key my-value\n" +
                "  child value\n" +
                "key2 value\n", output);
    }
}
