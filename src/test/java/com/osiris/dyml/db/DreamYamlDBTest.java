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
        db.load();

        // Define the table:
        DYTable tableUsers = db.putTable("users");

        // Define the columns:
        DYColumn colName = tableUsers.putColumn("name");
        DYColumn colAge = tableUsers.putColumn("age");
        db.save();

        // Fill columns with mock data:
        colName.addDef("John").addDef("Samantha").addDef("Peter");
        colAge.addDef("31").addDef((String) null).addDef("22");
        db.save();


        // There are multiple ways of retrieving information:
        // For example directly from the specific column:
        Assertions.assertEquals("John", colName.get(0).asString());
        Assertions.assertEquals("31", colAge.get(0).asString());
        // The DYTable class also got some methods:
        Assertions.assertEquals("John", tableUsers.getRowAsList(0).get(0).asString()); // Returns the row at index 0, with the value from the column at index 1
        Assertions.assertEquals("31", tableUsers.getRowAsList(0).get(1).asString()); // Returns the row at index 0, with the value from the column at index 1
        // It can also be retrieved like this:
        Assertions.assertEquals("John", tableUsers.getRow(0).getValueFromColumn(colName).asString());
        Assertions.assertEquals("31", tableUsers.getRow(0).getValueFromColumn(colAge).asString());

    }
}