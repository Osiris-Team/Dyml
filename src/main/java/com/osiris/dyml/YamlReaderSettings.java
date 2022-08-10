package com.osiris.dyml;

public class YamlReaderSettings {
    public static YamlReaderSettings GLOBAL = new YamlReaderSettings();
    /**
     * Enabled by default. Convenience method for toggling post-processing.<br>
     * When disabled none of the post-processing options gets run, no matter if they are enabled/disabled. <br>
     * Post-Processing happens inside {@link #load()}. <br>
     * Some available options are: <br>
     * {@link #isTrimCommentsEnabled} <br>
     * {@link #isTrimLoadedValuesEnabled} <br>
     * {@link #isRemoveQuotesFromLoadedValuesEnabled} <br>
     * etc...
     */
    public boolean isPostProcessingEnabled = true;
    /**
     * Enabled by default. Part of post-processing. <br>
     * Trims the loaded {@link YamlValue}. Example: <br>
     * <pre>
     * String before: '  hello there  '
     * String after: 'hello there'
     * Result: removed 4 spaces.
     * </pre>
     */
    public boolean isTrimLoadedValuesEnabled = true;
    /**
     * Enabled by default. Part of post-processing. <br>
     * Removes quotation marks ("" or '') from the loaded {@link YamlValue}. Example: <br>
     * <pre>
     * String before: "hello there"
     * String after: hello there
     * Result: removed 2 quotation-marks.
     * </pre>
     */
    public boolean isRemoveQuotesFromLoadedValuesEnabled = true;
    /**
     * Enabled by default. Part of post-processing. <br>
     * If {@link YamlValue#asString()} returns null, the whole {@link YamlValue} gets removed from the modules values list. <br>
     */
    public boolean isRemoveLoadedNullValuesEnabled = true;
    /**
     * Enabled by default. Part of post-processing. <br>
     * Trims the comments. Example: <br>
     * <pre>
     * String before: '    hello there  '
     * String after: 'hello there'
     * Result: removed 4 spaces.
     * </pre>
     */
    public boolean isTrimCommentsEnabled = true;
    // Modules:
    /**
     * Enabled by default. <br>
     * Null values return their default values as fallback.<br>
     * See {@link YamlSection#getValueAt(int)} for details.
     */
    public boolean isReturnDefaultWhenValueIsNullEnabled = true;
    /**
     * Enabled by default. <br>
     * If there are no values to write, write the default values.
     */
    public boolean isWriteDefaultValuesWhenEmptyEnabled = true;
    /**
     * Enabled by default. <br>
     * If there are no comments to write, write the default comments.
     */
    public boolean isWriteDefaultCommentsWhenEmptyEnabled = true;
}
