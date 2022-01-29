/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package examples.yaml;

import com.osiris.dyml.Yaml;
import com.osiris.dyml.YamlSection;
import org.junit.jupiter.api.Test;

/**
 * Recommendation how to use the DreamYaml api.
 */
public class CodingStyleExample {

    // If you only got a few modules and you want quick access across your code, you can add them as static fields and load your yaml file once at startup
    // If you also want to keep this values up to date you can add a DYWatcher with an DYAction which does that.
    public static YamlSection FIRST_NAME;
    public static YamlSection LAST_NAME;
    public static YamlSection AGE;
    public static YamlSection PROFESSION;

    // If you prefer encapsulating the modules you can do so, but remember that you will have to load your yaml file every time you create this class
    // This will ensure you always are using the latest values.
    private YamlSection firstName;
    private YamlSection lastName;
    private YamlSection age;
    private YamlSection work;

    @Test
    void test() throws Exception {
        Yaml yaml = new Yaml(System.getProperty("user.dir") + "/src/test/coding-style-example.yml");
        yaml.load();
        yaml.reset(); // DO NOT CALL THIS IN PRODUCTION, IT WILL REMOVE ALL THE INFORMATION FROM YOUR YAML FILE!
        // I am doing this only for the sake of testing!

        FIRST_NAME = yaml.put("name").setDefValues("John");
        LAST_NAME = yaml.put("last-name").setDefValues("Goldman");
        AGE = yaml.put("age").setDefValues("29");
        PROFESSION = yaml.put("work").setDefValues("Reporter");

        firstName = yaml.put("encapsulated", "name").setDefValues("John");
        lastName = yaml.put("encapsulated", "last-name").setDefValues("Goldman");
        age = yaml.put("encapsulated", "age").setDefValues("29");
        work = yaml.put("encapsulated", "work").setDefValues("Reporter");

        yaml.save(true);

        /*
name: John
last-name: Goldman
age: 29
work: Reporter
encapsulated:
  name: John
  last-name: Goldman
  age: 29
  work: Reporter
         */
    }

    // Getters for encapsulated modules:

    public YamlSection getFirstName() {
        return firstName;
    }

    public YamlSection getLastName() {
        return lastName;
    }

    public YamlSection getAge() {
        return age;
    }

    public YamlSection getWork() {
        return work;
    }
}
