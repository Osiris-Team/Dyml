/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package examples.yaml;

import com.osiris.dyml.Yaml;
import org.junit.jupiter.api.Test;

/**
 * Create a simple configuration file
 * with random person data.
 */
public class SerializationExample {

    class Person{
        public int age;
        public String name;

        public Person(int age, String name) {
            this.age = age;
            this.name = name;
        }
    }

    @Test
    void test() throws Exception {

        // Java object to YAML like so:
        Yaml yaml = new Yaml("", ""); // You can find every examples yaml file here https://github.com/Osiris-Team/Dream-Yaml/tree/main/src/test
        yaml.load();
        yaml.put("person").putJavaChildSection(new Person(23, "John"));
        yaml.save();
        // person:
        //   age: 23
        //   name: John

        // YAML to Java object like so:
        Person person = yaml.put("person").as(Person.class);
    }
}
