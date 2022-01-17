/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml.examples;

import com.osiris.dyml.Yaml;
import com.osiris.dyml.YamlSection;
import org.junit.jupiter.api.Test;

/**
 * Create a more complex yaml file
 * with multiple parents and children.
 */
public class ParentExample {

    @Test
    void test() throws Exception {
        Yaml yaml = new Yaml(System.getProperty("user.dir") + "/src/test/parent-example.yml", true);
        yaml.load();
        yaml.reset(); // DO NOT CALL THIS IN PRODUCTION, IT WILL REMOVE ALL THE INFORMATION FROM YOUR YAML FILE!
        // I am doing this only for the sake of testing!

        YamlSection p1 = yaml.put("p1").setComments("Create a more complex yaml file", "with multiple parents and children.");
        YamlSection p1C1 = yaml.put("p1", "c1").setDefValues("You can arrange your");
        YamlSection p1C2 = yaml.put("p1", "c2").setDefValues("keys and values");
        YamlSection p1C3 = yaml.put("p1", "c3").setDefValues("as you like!");
        YamlSection p2C3 = yaml.put("p2", "c1", "c2", "c3").setDefValues("awesome!"); // Always order objects from parent to child, otherwise you will get errors!
        // DYModule notAllowed = yaml.addDef("p2", "c1", "c2"); <- ERROR because you try to access c2 even though c3 already was added
        // If you want to access the child's values you need to pass them one after another like this:
        YamlSection p3C1 = yaml.put("p3", "c1").setDefValues("v1");
        YamlSection p3C2 = yaml.put("p3", "c1", "c2").setDefValues("v2");
        YamlSection p3C3 = yaml.put("p3", "c1", "c2", "c3").setDefValues("v3");

        yaml.printInEdit();
        yaml.save();

        /*
# Create a more complex yaml file
# with multiple parents and children.
p1:
  c1: You can arrange your
  c2: keys and values
  c3: as you like!
p2:
  c1:
    c2:
      c3: awesome!
p3:
  c1: v1
    c2: v2
      c3: v3
         */

    }
}
