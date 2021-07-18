package com.osiris.dyml.db;

import com.osiris.dyml.DYModule;

public class DYColumn{
    private DYModule columnModule;
    String name;

    public DYColumn(DYModule columnModule, String name) {
        this.columnModule = columnModule;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
