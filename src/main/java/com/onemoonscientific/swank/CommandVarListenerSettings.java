package com.onemoonscientific.swank;

/**
 *
 * @author brucejohnson
 */
public class CommandVarListenerSettings {

    private final Object value;
    private final String varName;
    private final String command;
    private boolean enabled = true;
    private boolean selected = true;

    CommandVarListenerSettings() {
        value = "1";
        varName = "";
        command = "";
    }

    CommandVarListenerSettings(final String value, final String varName, final String command) {
        this.value = value;
        this.varName = varName;
        this.command = command;
    }
    CommandVarListenerSettings(final Object value, final String varName, final String command) {
        this.value = value;
        this.varName = varName;
        this.command = command;
    }

    CommandVarListenerSettings(final double value, final String varName, final String command) {
        this.value = value;
        this.varName = varName;
        this.command = command;
    }

    /**
     *
     * @return
     */
    public String getValue() {
        return value.toString();
    }

    /**
     *
     * @return
     */
    public double getDValue() {
        if (value instanceof Double) {
            return ((Double) value).doubleValue();
        } else {
            return 0.0;
        }
    }

    /**
     *
     * @return
     */
    public String getVarName() {
        return varName;
    }

    /**
     *
     * @return
     */
    public String getCommand() {
        return command;
    }

    /**
     *
     * @return
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     *
     * @return
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     *
     * @param newVarName
     * @return
     */
    public CommandVarListenerSettings getWithVarName(final String newVarName) {
        CommandVarListenerSettings newValue = new CommandVarListenerSettings(getValue(), newVarName, getCommand());
        return newValue;
    }

    /**
     *
     * @param newValue
     * @return
     */
    public CommandVarListenerSettings getWithValue(final String newValue) {
        CommandVarListenerSettings newSettings = new CommandVarListenerSettings(newValue, getVarName(), getCommand());
        return newSettings;
    }
    /**
     *
     * @param newValue
     * @return
     */
    public CommandVarListenerSettings getWithValue(final double newValue) {
        CommandVarListenerSettings newSettings = new CommandVarListenerSettings(newValue, getVarName(), getCommand());
        return newSettings;
    }

    /**
     *
     * @param newCommand
     * @return
     */
    public CommandVarListenerSettings getWithCommand(final String newCommand) {
        CommandVarListenerSettings newSettings = new CommandVarListenerSettings(getValue(), getVarName(), newCommand);
        return newSettings;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
