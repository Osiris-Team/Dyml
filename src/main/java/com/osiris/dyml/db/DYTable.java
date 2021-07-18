package com.osiris.dyml.db;

import com.osiris.dyml.DYModule;
import com.osiris.dyml.DreamYaml;
import com.osiris.dyml.exceptions.DuplicateKeyException;
import com.osiris.dyml.exceptions.IllegalKeyException;
import com.osiris.dyml.exceptions.NotLoadedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DYTable {
    private final DYModule tableModule;

    public DYTable(DYModule tableModule) {
        Objects.requireNonNull(tableModule);
        this.tableModule = tableModule;
    }

    public DYModule getTableModule() {
        return tableModule;
    }

    public String getName() {
        return tableModule.getLastKey();
    }

    /**
     * See {@link DreamYaml#add(String...)} for details.
     */
    public DYColumn addColumn(String name) throws NotLoadedException, IllegalKeyException, DuplicateKeyException {
        return new DYColumn(tableModule.getYaml().add("tables", getName(), name));
    }

    /**
     * See {@link DreamYaml#put(String...)} for details.
     */
    public DYColumn putColumn(String name) throws NotLoadedException, IllegalKeyException {
        return new DYColumn(tableModule.getYaml().put("tables", getName(), name));
    }

    public DYTable removeColumn(DYColumn column) {
        Objects.requireNonNull(column);
        tableModule.getYaml().remove("tables", getName(), column.getName());
        return this;
    }

    public DYColumn getColumn(String name) {
        for (DYColumn c :
                getColumns()) {
            if (c.getName().equals(name))
                return c;
        }
        return null;
    }

    public DYColumn getColumnAtIndex(int index) {
        return getColumns().get(index);
    }

    public List<DYColumn> getColumns() {
        List<DYColumn> columns = new ArrayList<>();
        for (DYModule columnModule :
                tableModule.getChildModules()) {
            columns.add(new DYColumn(columnModule));
        }
        return columns;
    }
}
