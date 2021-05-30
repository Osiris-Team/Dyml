package com.osiris.dyml.utils;

import com.osiris.dyml.DYModule;
import com.osiris.dyml.DreamYaml;

import java.io.*;
import java.util.List;

/**
 * Contains methods which
 * extend DreamYaml's functionality.
 */
public class UtilsDreamYaml {
    private static final String n = System.lineSeparator();
    private final DreamYaml yaml;

    /**
     * Contains methods which
     * extend DreamYaml's functionality.
     */
    public UtilsDreamYaml(DreamYaml yaml) {
        this.yaml = yaml;
    }

    /**
     * Util method for printing the modules information got by {@link #getLoadedModulesInformationAsString()}
     * to an {@link PrintStream}.
     */
    public void printLoaded(PrintStream out) {
        out.println(getLoadedModulesInformationAsString());
    }

    /**
     * Util method for printing the modules information got by {@link #getInEditModulesInformationAsString()}
     * to an {@link PrintStream}.
     */
    public void printInEdit(PrintStream out) {
        out.println(getInEditModulesInformationAsString());
    }

    /**
     * Util method for printing the modules information got by {@link #getUnifiedModulesInformationAsString()}
     * to an {@link PrintStream}.
     */
    public void printUnified(PrintStream out) {
        out.println(getUnifiedModulesInformationAsString());
    }

    /**
     * Util method for writing the modules information got by {@link #getLoadedModulesInformationAsString()}
     * to an {@link OutputStream}.
     */
    public void writeLoaded(OutputStream out) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out))) {
            writer.write(getLoadedModulesInformationAsString() + n);
        }
    }

    /**
     * Util method for writing the modules information got by {@link #getInEditModulesInformationAsString()}
     * to an {@link OutputStream}.
     */
    public void writeInEdit(OutputStream out) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out))) {
            writer.write(getInEditModulesInformationAsString() + n);
        }
    }

    /**
     * Util method for writing the modules information got by {@link #getUnifiedModulesInformationAsString()}
     * to an {@link OutputStream}.
     */
    public void writeUnified(OutputStream out) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out))) {
            writer.write(getUnifiedModulesInformationAsString() + n);
        }
    }

    /**
     * Util method for returning the modules information
     * formatted as {@link String}.
     */
    public String getLoadedModulesInformationAsString() {
        return "LOADED modules from '" + yaml.getFile().getName() + "' file:" + n + getModulesInformationFromListAsString(yaml.getAllLoaded());
    }

    /**
     * Util method for returning the modules information
     * formatted as {@link String}.
     */
    public String getInEditModulesInformationAsString() {
        return "ADDED modules from '" + yaml.getFile().getName() + "' file:" + n + getModulesInformationFromListAsString(yaml.getAllInEdit());
    }

    /**
     * Util method for returning the modules information
     * formatted as {@link String}.
     */
    public String getUnifiedModulesInformationAsString() {
        return "UNIFIED modules from '" + yaml.getFile().getName() + "' file:" + n + getModulesInformationFromListAsString(new UtilsDYModule().createUnifiedList(yaml.getAllInEdit(), yaml.getAllLoaded()));
    }

    /**
     * Util method for returning the modules information from a {@link List}
     * formatted as {@link String}.
     */
    public String getModulesInformationFromListAsString(List<DYModule> modules) {
        StringBuilder s = new StringBuilder();
        for (DYModule module :
                modules) {
            s.append(module.getModuleInformationAsString()).append(n);
        }
        return s.toString();
    }

}
