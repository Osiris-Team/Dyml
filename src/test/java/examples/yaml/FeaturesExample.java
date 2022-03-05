package examples.yaml;

import com.osiris.dyml.Yaml;
import com.osiris.dyml.exceptions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class FeaturesExample {

    @Test
    void test() throws IOException, DuplicateKeyException, YamlReaderException, IllegalListException, NotLoadedException, IllegalKeyException, YamlWriterException {
        Yaml yaml = new Yaml(System.getProperty("user.dir") + "/src/test/features.yml", true);
        yaml.load(); // Also supports InputStreams and Strings as input

        yaml.put("important").setDefValues("Everything else that is not explicitly mentioned in this file is not supported");

        yaml.put("supports-lists").setCountTopLineBreaks(1).setDefValues("Hello World!", "2nd value").setComments("Comments and", "multiline comments support.");
        yaml.put("supports-hyphen-separation").setDefValues("awesome!");
        yaml.put("or separation by spaces").setDefValues("great!").addDefSideComments("side-comments supported!");
        yaml.put("and.dots.like.this").setDefValues("wow!");

        yaml.put("g0").setCountTopLineBreaks(1);
        yaml.put("g0", "g1a", "g2a").setDefValues("wow!");
        yaml.put("g0", "g1a", "g2b").setDefValues("<3");
        yaml.put("g0", "g1b")
                .addDefValues("v1").addDefSideComments("This is a side-comment in a list")
                .addDefValues("v2").addDefSideComments("This is also a side-comment, for the value below");

        yaml.save();
    }
}
