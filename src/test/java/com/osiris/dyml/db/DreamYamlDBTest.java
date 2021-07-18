package com.osiris.dyml.db;

import com.osiris.dyml.exceptions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class DreamYamlDBTest {

    @Test
    void testCodingStyle() throws IOException, DuplicateKeyException, DYReaderException, IllegalListException, NotLoadedException, IllegalKeyException, DYWriterException {
        DreamYamlDB db = new DreamYamlDB(new File(System.getProperty("user.dir") + "/src/test/test-db.yml"));
        db.load();
        DYTable tableUsers = db.putTable("users");
        DYColumn name = tableUsers.putColumn("name");
        DYColumn age = tableUsers.putColumn("age");
        db.save();

        // Fill columns with mock data
        name.addDef("John").addDef("Samantha").addDef("Peter");
        age.addDef("31").addDef("19").addDef("22");
        db.save();

        // Retrieve information
        name.get(1);
    }
}