package examples.dyml;

import com.osiris.dyml.Dyml;
import com.osiris.dyml.exceptions.IllegalListException;
import com.osiris.dyml.exceptions.YamlReaderException;
import com.osiris.dyml.exceptions.YamlWriterException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class FeaturesExample {
    @Test
    void test() throws YamlReaderException, IOException, IllegalListException, YamlWriterException {
        Dyml dyml = new Dyml(); // You can pass over a String/InputStream or File
        dyml.put("important").value.set("Everything else that is not explicitly mentioned in this file is not supported");
        Dyml key = dyml.put("key");
        key.value.set("value");
        key.addComments("Comments and", "multiline comments support.");
        //key.addComments("Comments and\nmultiline comments support."); // Does the same as above
        dyml.put("g0").addComments("Complex hierarchies supported.");
        dyml.put("g0", "g1a", "g2a").value.set("wow!");
        dyml.put("g0", "g1a", "g2b").value.set("<3");
        dyml.put("g0", "g1b").value.set("great!");
        dyml.debugPrint(System.out);
        String text = dyml.saveToText(); // Save to file with dyml.toFile(file);
        System.out.println(text);
    }
}
