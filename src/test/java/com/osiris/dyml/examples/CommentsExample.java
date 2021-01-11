/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml.examples;

import com.osiris.dyml.DYModule;
import com.osiris.dyml.DreamYaml;
import org.junit.jupiter.api.Test;

/**
 * Create a simple yaml file
 * with random person data and comments.
 */
public class CommentsExample {

    @Test
    void test() throws Exception {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir")+"/src/test/comments-example.yml");
        yaml.load();

        DYModule firstName = yaml.add("name").setDefValue("John").setComments("Insert your multiline", "comments like this.");
        DYModule lastName = yaml.add("last-name").setDefValue("Goldman").setComment("This is a single line comment.");
        DYModule age = yaml.add("age").setDefValue("29");
        DYModule work = yaml.add("work").setDefValue("Reporter");

        yaml.save();

        /*
         # Insert your multiline
         # comments like this.
         name: John
         # This is a single line comment.
         last-name: Goldman
         age: 29
         work: Reporter
         */

    }
}
