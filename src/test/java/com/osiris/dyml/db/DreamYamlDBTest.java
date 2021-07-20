package com.osiris.dyml.db;

import com.osiris.dyml.exceptions.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class DreamYamlDBTest {

    @Test
    void testCodingStyle() throws IOException, DuplicateKeyException, DYReaderException, IllegalListException, NotLoadedException, IllegalKeyException, DYWriterException {
        DreamYamlDB db = new DreamYamlDB(new File(System.getProperty("user.dir") + "/src/test/test-db.yml"));
        db.getYaml().setDebugEnabled(true);
        db.load();

        // Define the table:
        DYTable tableUsers = db.putTable("users");

        // Define the columns:
        tableUsers.putColumn("name");
        tableUsers.putColumn("age");
        // db.save() // Already called inside the putColumn() method above.

        // Fill columns with mock data:
        tableUsers.addDefRow("John", "31");
        tableUsers.addDefRow("Samantha", null);
        tableUsers.addDefRow("Peter", "22");
        db.save();


        // There are multiple ways of retrieving information:
        // For example directly from the specific column:
        Assertions.assertEquals("John", tableUsers.getColumn("name").get(0).asString());
        Assertions.assertEquals("31", tableUsers.getColumn("age").get(0).asString());
        // The DYTable class also got some methods:
        Assertions.assertEquals("John", tableUsers.getRowAsList(0).get(0).asString()); // Returns the row at index 0, with the value from the column at index 1
        Assertions.assertEquals("31", tableUsers.getRowAsList(0).get(1).asString()); // Returns the row at index 0, with the value from the column at index 1

        // Queries:
        //Assertions.assertEquals("John", tableUsers.getValuesEqualTo("name", "John").get(0).getValues().get(0).asString());
        //Assertions.assertEquals(31, tableUsers.getValuesEqualTo("age", "31").get(0).getValues().get(1).asInt());

    }
}