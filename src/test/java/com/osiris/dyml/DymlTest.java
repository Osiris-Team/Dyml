package com.osiris.dyml;

import com.osiris.dyml.exceptions.IllegalListException;
import com.osiris.dyml.exceptions.YamlReaderException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class DymlTest {

    @Test
    void testCodeStyle() throws IOException, YamlReaderException, IllegalListException {
        Dyml dyml = Dyml.from(" Project Name\n" +
                "architect amihaiemil\n" +
                " This is a test comment with no line breaks!\n" +
                "devops rultor 0pdd\n" +
                " This is a multi-lined comment!\n" +
                " See it goes on!\n" +
                "developers amihaiemil salikjan SherifWally\n" +
                "\n" +
                " Project Name\n" +
                "architect1 amihaiemil\n" +
                " This is a test comment with no line breaks!\n" +
                "devops1 rultor 0pdd\n" +
                " This is a multi-lined comment!\n" +
                " See it goes on!\n" +
                "developers1 amihaiemil salikjan SherifWally\n" +
                "\n" +
                " Project Name\n" +
                "architect2 amihaiemil\n" +
                " This is a test comment with no line breaks!\n" +
                "devops2 rultor 0pdd\n" +
                " This is a multi-lined comment!\n" +
                " See it goes on!\n" +
                "developers2 amihaiemil salikjan SherifWally\n" +
                "\n" +
                " Project Name\n" +
                "architect3 amihaiemil\n" +
                " This is a test comment with no line breaks!\n" +
                "devops3 rultor 0pdd\n" +
                " This is a multi-lined comment!\n" +
                " See it goes on!\n" +
                "developers3 amihaiemil salikjan SherifWally");
        dyml.printSections(System.out);
    }
}