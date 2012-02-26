/*
 *
 *
 * Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file \"LICENSE\" for information on usage and redistribution
 * of this file.
 * IN NO EVENT SHALL THE AUTHORS OR DISTRIBUTORS BE LIABLE TO
 * ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR
 * CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OF THIS
 * SOFTWARE, ITS DOCUMENTATION, OR ANY DERIVATIVES THEREOF,
 * EVEN IF THE AUTHORS HAVE BEEN ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * THE AUTHORS AND DISTRIBUTORS SPECIFICALLY DISCLAIM ANY
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, AND NON-INFRINGEMENT.  THIS SOFTWARE
 * IS PROVIDED ON AN "AS IS" BASIS, AND THE AUTHORS AND
 * DISTRIBUTORS HAVE NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 *
 */
package com.onemoonscientific.swank;

import tcl.lang.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 *
 * @author brucejohnson
 */
public class SwkCheckButtonListener implements ActionListener, VarTrace,
        SwkListener {

    Interp interp;
    AbstractButton component;

    ButtonSettings getButtonSettings() {
        buttonSettings.setEnabled(component.isEnabled());
        buttonSettings.setSelected(component.isSelected());
        return buttonSettings;
    }
    ButtonSettings buttonSettings = new ButtonSettings();

    /**
     *
     */
    public static class ButtonSettings extends CommandVarListenerSettings {

        final private String offValue;

        ButtonSettings() {
            super();
            offValue = "0";
        }

        ButtonSettings(final String value, final String offValue, final String varName, final String command) {
            super(value, varName, command);
            this.offValue = offValue;
        }

        /**
         *
         * @return
         */
        public String getOffValue() {
            return offValue;
        }

        /**
         *
         * @param newOffValue
         * @return
         */
        public ButtonSettings getWithOffValue(final String newOffValue) {
            ButtonSettings newValue = new ButtonSettings(getValue(), newOffValue, getVarName(), getCommand());
            return newValue;
        }

        @Override
        public ButtonSettings getWithVarName(final String newVarName) {
            ButtonSettings newValue = new ButtonSettings(getValue(), getOffValue(), newVarName, getCommand());
            return newValue;
        }

        @Override
        public ButtonSettings getWithValue(final String newValue) {
            ButtonSettings newSettings = new ButtonSettings(newValue, getOffValue(), getVarName(), getCommand());
            return newSettings;
        }

        @Override
        public ButtonSettings getWithCommand(final String newCommand) {
            ButtonSettings newSettings = new ButtonSettings(getValue(), getOffValue(), getVarName(), newCommand);
            return newSettings;
        }
    }

    SwkCheckButtonListener(Interp interp, Component component) {
        this.interp = interp;
        this.component = (AbstractButton) component;
    }

    /**
     *
     * @param interp
     * @param string1
     * @param string2
     * @param flags
     * @throws TclException
     */
    public void traceProc(Interp interp, String string1, String string2,
            int flags) throws TclException {
        if (EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkCheckButtonListener: traceProc on event thread");
        }

        setFromVar(interp);
    }

    /**
     *
     * @param interp
     * @throws TclException
     */
    public void setFromVar(Interp interp) throws TclException {
        if (EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkCheckButtonListener: setFromVar on event thread");
        }

        if (!buttonSettings.getVarName().equals("")) {
            {
                TclObject tobj = interp.getVar(buttonSettings.getVarName(), TCL.GLOBAL_ONLY);
                final boolean state;

                if (tobj.toString().equals(buttonSettings.getValue())) {
                    state = true;
                } else {
                    state = false;
                }

                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        component.setSelected(state);
                    }
                });
            }
        }
    }

    /**
     *
     * @param interp
     * @param name
     * @throws TclException
     */
    public void setVarName2(Interp interp, String name)
            throws TclException {
        if (EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkCheckButtonListener: setVarName on event thread");
        }

        TclObject tObj = null;

        if ((name != null) && !name.equals("")) {
            try {
                tObj = interp.getVar(name, TCL.GLOBAL_ONLY);

                if (tObj.toString().equals(buttonSettings.getValue())) {
                    final boolean state = true;
                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            component.setSelected(state);
                        }
                    });
                }
                if (tObj.toString().equals(buttonSettings.getOffValue())) { //Added for the configure case where checkbutton should become disabled
                    final boolean state = false;
                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            component.setSelected(state);
                        }
                    });
                }
            } catch (TclException tclException) {
                interp.resetResult();

                if (!buttonSettings.getOffValue().equals("")) {
                    tObj = TclString.newInstance(buttonSettings.getOffValue());
                } else {
                    tObj = TclString.newInstance("0");
                }

                interp.setVar(name, tObj, TCL.GLOBAL_ONLY);
            }
        }

        if (!buttonSettings.getVarName().equals("")) {
            interp.untraceVar(buttonSettings.getVarName(), this, TCL.TRACE_WRITES
                    | TCL.GLOBAL_ONLY);
        }

        if ((name != null) && !name.equals("")) {
            interp.traceVar(name, this, TCL.TRACE_WRITES | TCL.GLOBAL_ONLY);
        }
        buttonSettings = buttonSettings.getWithVarName(name);
    }

    /**
     *
     * @param interp
     * @param name
     * @return
     * @throws TclException
     */
    public boolean setVarName(Interp interp, String name)
            throws TclException {
        if (EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkCheckButtonListener: setVarName on event thread");
        }
        boolean state = false;
        TclObject tObj = null;
        if (name == null) {
            name = "";
        }

        if (!name.equals("")) {
            try {
                tObj = interp.getVar(name, TCL.GLOBAL_ONLY);

                if (tObj.toString().equals(buttonSettings.getValue())) {
                    state = true;
                }
                if (tObj.toString().equals(buttonSettings.getOffValue())) { //Added for the configure case where checkbutton should become disabled
                    state = false;
                }
            } catch (TclException tclException) {
                interp.resetResult();

                if (!buttonSettings.getOffValue().equals("")) {
                    tObj = TclString.newInstance(buttonSettings.getOffValue());
                } else {
                    tObj = TclString.newInstance("0");
                }

                interp.setVar(name, tObj, TCL.GLOBAL_ONLY);
            }
        }

        if (!buttonSettings.getVarName().equals("")) {
            interp.untraceVar(buttonSettings.getVarName(), this, TCL.TRACE_WRITES
                    | TCL.GLOBAL_ONLY);
        }

        if (!name.equals("")) {
            interp.traceVar(name, this, TCL.TRACE_WRITES | TCL.GLOBAL_ONLY);
        }
        buttonSettings = buttonSettings.getWithVarName(name);
        return state;
    }

    /**
     *
     * @return
     */
    public String getVarName() {
        return buttonSettings.getVarName();
    }

    /**
     *
     * @param value
     */
    public void setOnValue(String value) {
        if (!EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkCheckButtonListener: setOnValue not on event thread");
        }

        TclObject tObj;

        if (component.isSelected()
                && !(value.equals(buttonSettings.getValue()))) {
            actionPerformed(null);
        }

        buttonSettings = buttonSettings.getWithValue(value);
    }

    /**
     *
     * @return
     */
    public String getOffValue() {
        return buttonSettings.getOffValue();
    }

    /**
     *
     * @param value
     */
    public void setOffValue(String value) {
        if (!EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkCheckButtonListener: setOffValue not on event thread");
        }

        if (!component.isSelected()
                && !(value.equals(buttonSettings.getOffValue()))) {
            actionPerformed(null);
        }

        buttonSettings = buttonSettings.getWithOffValue(value);
    }

    /**
     *
     * @return
     */
    public String getOnValue() {
        return buttonSettings.getValue();
    }

    /**
     *
     * @param name
     */
    public void setCommand(String name) {
        buttonSettings = buttonSettings.getWithCommand(name);
    }

    /**
     *
     * @return
     */
    public String getCommand() {
        return buttonSettings.getCommand();
    }

    public void actionPerformed(ActionEvent e) {
        if (!EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkCheckButtonListener: actionPerformed not on event thread");
        }

        String value;
        if (component.isSelected()) {
            value = buttonSettings.getValue();
        } else {
            value = buttonSettings.getOffValue();
        }

        if (!buttonSettings.getVarName().equals("")) {
            SetStringVarEvent strEvent = new SetStringVarEvent(interp, buttonSettings.getVarName(),
                    null, value);
            interp.getNotifier().queueEvent(strEvent, TCL.QUEUE_TAIL);
        }

        BindEvent bEvent = new BindEvent(interp, (SwkListener) this,
                (EventObject) e, 0);
        interp.getNotifier().queueEvent(bEvent, TCL.QUEUE_TAIL);
    }

    /**
     *
     * @param buttonSettings
     * @throws TclException
     */
    public void tclActionVar(ButtonSettings buttonSettings) throws TclException {
        final TclObject tObj;
        final String value;
        if (buttonSettings.isSelected()) {
            value = buttonSettings.getValue();
        } else {
            value = buttonSettings.getOffValue();
        }
        tObj = TclString.newInstance(value);
        interp.setVar(buttonSettings.getVarName(), tObj, TCL.GLOBAL_ONLY);
    }

    /**
     *
     * @param buttonSettings
     * @throws TclException
     */
    public void tclAction(ButtonSettings buttonSettings) throws TclException {
        if (buttonSettings.getCommand().length() != 0) {
            try {
                interp.eval(buttonSettings.getCommand());
            } catch (TclException tclException) {
                interp.addErrorInfo("\n    (\"binding\" script)");
                interp.backgroundError();
            }
        }
    }

    /**
     *
     * @param eventObject
     * @param obj
     * @param subtype
     */
    public void processEvent(EventObject eventObject, Object obj, int subtype) {
        if (EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkCheckButtonListener: processEvent on event thread");
        }
        try {
            tclAction(getButtonSettings());
        } catch (TclException tclException) {
            interp.addErrorInfo("\n    (\"binding\" script)");
            interp.backgroundError();
        }
    }
}
