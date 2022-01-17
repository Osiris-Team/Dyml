package com.osiris.dyml.examples;

import com.osiris.dyml.Yaml;
import com.osiris.dyml.YamlValue;
import com.osiris.dyml.exceptions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class FeaturesExample {

    @Test
    void test() throws IOException, DuplicateKeyException, YamlReaderException, IllegalListException, NotLoadedException, IllegalKeyException, YamlWriterException {
        Yaml yaml = new Yaml(System.getProperty("user.dir") + "/src/test/features.yml", true);
        yaml.load();

        yaml.put("the-show-off-list").setDefValues("completely written from scratch without any extra dependency", "fastest YAML reader and writer currently available (see benchmarks below)", "not a single static method and very memory efficient");
        yaml.put("supports-hyphen-separation").setDefValues("awesome!");
        yaml.put("or separation by spaces").setDefValues(new YamlValue("great!").setComment("side-comments supported!"));
        yaml.put("and.dots.like.this").setDefValues("wow!");

        yaml.put("m1-g0", "m1-g1", "m1-g2").setDefValues("wow!");
        yaml.put("m1-g0", "m1-g1", "m2-g2").setDefValues("<3");
        yaml.put("m1-g0", "m2-g1")
                .addDefValues(new YamlValue("v1").setComment("This is a side-comment in a list"))
                .addDefValues(new YamlValue("v2").setComment("This is also a side-comment, for the value below"));

        yaml.put("not supported").setDefValues(
                "everything else that is not explicitly mentioned in this file"
        );

        yaml.save();
    }
}
