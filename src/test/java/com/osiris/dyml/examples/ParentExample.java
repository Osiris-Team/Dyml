package com.osiris.dyml.examples;

import com.osiris.dyml.DYModule;
import com.osiris.dyml.DreamYaml;
import org.junit.jupiter.api.Test;

/**
 * Create a more complex yaml file
 * with multiple parents and children.
 */
public class ParentExample {

    @Test
    void test() throws Exception {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir")+"/src/test/parent-example.yml",
                true);
        yaml.load();

        DYModule p1 = yaml.add("p1").setComments("Create a more complex yaml file", "with multiple parents and children.");
        DYModule p1C1 = yaml.add("p1","c1").setDefValue("You can arrange your");
        DYModule p1C2 = yaml.add("p1","c2").setDefValue("keys and values");
        DYModule p1C3 = yaml.add("p1","c3").setDefValue("as you like!");
        DYModule p2C3 = yaml.add("p2", "c1", "c2", "c3").setDefValue("awesome!"); // Always order objects from parent to child, otherwise you will get errors!
        // DYModule notAllowed = yaml.addDef("p2", "c1", "c2"); <- ERROR because you try to access c2 even though c3 already was added
        // If you want to access the child's values you need to pass them one after another like this:
        DYModule p3C1 = yaml.add("p3", "c1").setDefValue("v1");
        DYModule p3C2 = yaml.add("p3", "c1", "c2").setDefValue("v2");
        DYModule p3C3 = yaml.add("p3", "c1", "c2", "c3").setDefValue("v3");

        yaml.save();

        /*
         # Create a more complex yaml file
         # with multiple parents and children.
         name: John
         # This is a single line comment.
         last name: Goldman
         age: 29
         work: Reporter
         */

    }
}
