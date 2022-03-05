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
 * Create a simple yaml file
 * with random person data and comments.
 */
public class CommentsExample {

    @Test
    void test() throws Exception {
        Yaml yaml = new Yaml(System.getProperty("user.dir") + "/src/test/comments-example.yml");
        yaml.load();
        yaml.reset(); // DO NOT CALL THIS IN PRODUCTION, IT WILL REMOVE ALL THE INFORMATION FROM YOUR YAML FILE!
        // I am doing this only for the sake of testing!

        YamlSection firstName = yaml.put("name").setDefValues("John").setComments("You can insert your", "multiline comments like this.");
        YamlSection lastName = yaml.put("last-name").setDefValues("Goldman").setComments(
                "This is a multiline comment \n" +
                        "separated by javas \n" +
                        "next line character!");
        YamlSection age = yaml.put("age").setDefValues("29").addDefSideComments("This is a side-comment/value-comment")
                .setComments("This is a single line comment.");
        YamlSection work = yaml.put("work").setDefValues("Reporter");
        YamlSection parent = yaml.put("p1", "c2", "c3").setComments("Comments in", "a hierarchy.");

        yaml.saveAndLoad();

        // How to get comments?
        firstName.getComments(); // Returns this modules key/top-comments
        age.getSideComments(); // Returns this sections' side-comment

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
