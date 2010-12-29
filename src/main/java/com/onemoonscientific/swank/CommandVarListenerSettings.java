package com.onemoonscientific.swank;

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

    public String getValue() {
        return value.toString();
    }

    public double getDValue() {
        if (value instanceof Double) {
            return ((Double) value).doubleValue();
        } else {
            return 0.0;
        }
    }

    public String getVarName() {
        return varName;
    }

    public String getCommand() {
        return command;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isSelected() {
        return selected;
    }

    public CommandVarListenerSettings getWithVarName(final String newVarName) {
        CommandVarListenerSettings newValue = new CommandVarListenerSettings(getValue(), newVarName, getCommand());
        return newValue;
    }

    public CommandVarListenerSettings getWithValue(final String newValue) {
        CommandVarListenerSettings newSettings = new CommandVarListenerSettings(newValue, getVarName(), getCommand());
        return newSettings;
    }
    public CommandVarListenerSettings getWithValue(final double newValue) {
        CommandVarListenerSettings newSettings = new CommandVarListenerSettings(newValue, getVarName(), getCommand());
        return newSettings;
    }

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
