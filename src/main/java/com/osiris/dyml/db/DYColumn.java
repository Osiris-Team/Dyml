package com.osiris.dyml.db;

import com.osiris.dyml.DreamYaml;

public class DYColumn{
    DreamYaml yaml;
    String name;

    public DYColumn(DreamYaml yaml, String name) {
        this.yaml = yaml;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
