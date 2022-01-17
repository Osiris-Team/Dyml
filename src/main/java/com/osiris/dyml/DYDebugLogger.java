package com.osiris.dyml;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Pass this object to a {@link Yaml},
 * to configure its logging properties.
 */
public class DYDebugLogger {
    private PrintStream printOut;
    private FileOutputStream fileOut;

    public DYDebugLogger(PrintStream printOut) {
        this(printOut, null);
    }

    /**
     * A simple logger, that is able to write/print to a file/console.
     *
     * @param printOut If you want to print the log to a console, pass over its {@link PrintStream} here. Null to disable.
     * @param fileOut  If you want to write the log to a file, pass over its {@link FileOutputStream} here. Null to disable.
     */
    public DYDebugLogger(PrintStream printOut, FileOutputStream fileOut) {
        this.printOut = printOut;
        this.fileOut = fileOut;
    }

    /**
     * Adds debugging details to the provided message. <br>
     * Note that it wont throw an {@link Exception} when there are issues with writing/printing.
     * and writes it to the {@link #printOut}.
     *
     * @param object if you want to provide details to the object this message is about.
     */
    public void log(Object object, String message) {
        // Print and write to file
        try {
            if (printOut != null) {
                StringBuilder builder = new StringBuilder();
                //builder.append("[").append(new Date().toString()).append("]");
                builder.append("[").append(object.getClass().getSimpleName()).append("]");
                builder.append(" ").append(message);
                builder.append(System.lineSeparator());
                printOut.print(builder);
            }
            if (fileOut != null) {
                StringBuilder builder = new StringBuilder();
                //builder.append("[").append(new Date().toString()).append("]");
                builder.append("[").append(object.getClass().getSimpleName()).append("]");
                builder.append(" ").append(message);
                builder.append(System.lineSeparator());
                fileOut.write(builder.toString().getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public OutputStream getPrintOut() {
        return printOut;
    }

    public void setPrintOut(PrintStream printOut) {
        this.printOut = printOut;
    }

    public FileOutputStream getFileOut() {
        return fileOut;
    }

    public void setFileOut(FileOutputStream fileOut) {
        this.fileOut = fileOut;
    }

    public boolean isEnabled() {
        return printOut != null || fileOut != null;
    }
}
