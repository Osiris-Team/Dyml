package com.osiris.dyml.db;

import com.osiris.dyml.exceptions.DYReaderException;
import com.osiris.dyml.exceptions.DuplicateKeyException;
import com.osiris.dyml.exceptions.IllegalListException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class DreamYamlDBTest {

    @Test
    void testCodingStyle() throws IOException, DuplicateKeyException, DYReaderException, IllegalListException {
        DreamYamlDB db = new DreamYamlDB("test-db");
    }
}