package com.osiris.dyml;

import java.util.List;

/**
 * Provides all the information necessary
 * to parse a {@link Yaml} or {@link Dyml} object.
 */
public interface ParseableNode {
    String getKey();

    List<String> getValues();

    ParseableNode getParent();

    List<ParseableNode> getChildren();

    String toString(List<ParseableNode> nodes);

    <T extends ParseableNode> List<T> toNodesList(String s);
}
