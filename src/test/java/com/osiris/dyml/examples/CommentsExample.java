/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml.examples;

import com.osiris.dyml.DYModule;
import com.osiris.dyml.DYValue;
import com.osiris.dyml.DreamYaml;
import org.junit.jupiter.api.Test;

/**
 * Create a simple yaml file
 * with random person data and comments.
 */
public class CommentsExample {

    @Test
    void test() throws Exception {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/comments-example.yml");
        //yaml.load(); // We don't need to call this, since autoLoad is enabled by default.
        yaml.reset(); // DO NOT CALL THIS IN PRODUCTION, IT WILL REMOVE ALL THE INFORMATION FROM YOUR YAML FILE!
        // I am doing this only for the sake of testing!

        DYModule firstName = yaml.add("name").setDefValue("John").setComments("You can insert your", "multiline comments like this.");
        DYModule lastName = yaml.add("last-name").setDefValue("Goldman").setComment(
                "This is a multiline comment \n" +
                        "separated by javas \n" +
                        "next line character!");
        DYModule age = yaml.add("age").setDefValue(new DYValue(29).setComment("This is a side-comment/value-comment"))
                .setComment("This is a single line comment.");
        DYModule work = yaml.add("work").setDefValue("Reporter");
        DYModule parent = yaml.add("p1", "c2", "c3").setComments("Comments in", "a hierarchy.");

        yaml.save();

/*
# You can insert your
# multiline comments like this.
name: John
# This is a multiline comment
# separated by javas
# next line character!
last-name: Goldman
# This is a single line comment.
age: 29 # This is a side-comment/value-comment
work: Reporter
p1:
  c2:
      # Comments in
      # a hierarchy.
      c3:
*/
    }
}
