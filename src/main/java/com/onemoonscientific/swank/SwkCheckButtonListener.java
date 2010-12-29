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

import java.lang.*;

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

public class SwkCheckButtonListener implements ActionListener, VarTrace,
        SwkListener {

    Interp interp;
    JToggleButton component;
    boolean traceLock = false;
    ButtonSettings buttonSettings = new ButtonSettings();
 
    public static class ButtonSettings {
        final private String onValue;
        final private String offValue;
        final private String varName;
        final private String command;
        private boolean enabled = true;
        private boolean selected = true;
        ButtonSettings() {
           onValue = "1";
           offValue = "0";
           varName = "";
           command = "";
        }
        ButtonSettings(final String onValue, final String offValue, final String varName, final String command) {
           this.onValue = onValue;
           this.offValue = offValue;
           this.varName = varName;
           this.command = command;
        }
        public String getOnValue() {
            return onValue;
        }
        public String getOffValue() {
            return offValue;
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
        public ButtonSettings getWithVarName( final String newVarName) {
            ButtonSettings newValue = new ButtonSettings(getOnValue(), getOffValue(), newVarName, getCommand());
            return newValue;
        }
        public ButtonSettings getWithOnValue( final String newOnValue) {
            ButtonSettings newValue = new ButtonSettings(newOnValue, getOffValue(), getVarName(), getCommand());
            return newValue;
        }
        public ButtonSettings getWithOffValue( final String newOffValue) {
            ButtonSettings newValue = new ButtonSettings(getOnValue(),newOffValue,  getVarName(), getCommand());
            return newValue;
        }
        public ButtonSettings getWithCommand( final String newCommand) {
            ButtonSettings newValue = new ButtonSettings(getOnValue(),getOffValue(),  getVarName(), newCommand);
            return newValue;
        }
    }
    ButtonSettings getButtonSettings() {
        buttonSettings.enabled = component.isEnabled();
        buttonSettings.selected = component.isSelected();
        return buttonSettings;
    }
    SwkCheckButtonListener(Interp interp, Component component) {
        this.interp = interp;
        this.component = (JToggleButton) component;
    }

    public void traceProc(Interp interp, String string1, String string2,
            int flags) throws TclException {
        if (EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkCheckButtonListener: traceProc on event thread");
        }

        setFromVar(interp);
    }

    public void setFromVar(Interp interp) throws TclException {
        if (EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkCheckButtonListener: setFromVar on event thread");
        }

        if (!buttonSettings.getVarName().equals("")) {
            {
                TclObject tobj = interp.getVar(buttonSettings.getVarName(), TCL.GLOBAL_ONLY);
                final boolean state;

                if (tobj.toString().equals(buttonSettings.getOnValue())) {
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

        traceLock = false;
    }

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

                if (tObj.toString().equals(buttonSettings.getOnValue())) {
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

                if ( !buttonSettings.getOffValue().equals("")) {
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
    }
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

                if (tObj.toString().equals(buttonSettings.getOnValue())) {
                    state = true;
                 }
                if (tObj.toString().equals(buttonSettings.getOffValue())) { //Added for the configure case where checkbutton should become disabled
                    state = false;
                }
            } catch (TclException tclException) {
                interp.resetResult();

                if ( !buttonSettings.getOffValue().equals("")) {
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

    public String getVarName() {
        return buttonSettings.getVarName();
    }

    public void setOnValue(String value) {
        if (!EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkCheckButtonListener: setOnValue not on event thread");
        }

        TclObject tObj;

        if (((SwkJCheckBox) component).isSelected()
                && !(value.equals(buttonSettings.getOnValue()))) {
            actionPerformed(null);
        }

        buttonSettings = buttonSettings.getWithOnValue(value);
    }

    public String getOffValue() {
        return buttonSettings.getOffValue();
    }

    public void setOffValue(String value) {
        if (!EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkCheckButtonListener: setOffValue not on event thread");
        }

        if (!((SwkJCheckBox) component).isSelected()
                && !(value.equals(buttonSettings.getOffValue()))) {
            actionPerformed(null);
        }

        buttonSettings = buttonSettings.getWithOffValue(value);
    }

    public String getOnValue() {
        return buttonSettings.getOnValue();
    }

    public void setCommand(String name) {
        buttonSettings = buttonSettings.getWithCommand(name);
    }

    public String getCommand() {
        return buttonSettings.getCommand();
    }

    public void actionPerformed(ActionEvent e) {
        if (!EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkCheckButtonListener: actionPerformed not on event thread");
        }

        String value;
        if (((SwkJCheckBox) component).isSelected()) {
            value = buttonSettings.getOnValue();
        } else {
            value = buttonSettings.getOffValue();
        }

        traceLock = true;

        if (!buttonSettings.getVarName().equals("")) {
            SetStringVarEvent strEvent = new SetStringVarEvent(interp, buttonSettings.getVarName(),
                    null, value);
            interp.getNotifier().queueEvent(strEvent, TCL.QUEUE_TAIL);
        }

        BindEvent bEvent = new BindEvent(interp, (SwkListener) this,
                (EventObject) e, 0);
        interp.getNotifier().queueEvent(bEvent, TCL.QUEUE_TAIL);
    }

    public void tclActionVar(ButtonSettings buttonSettings) throws TclException {
        final TclObject tObj;
        final String value;
           if (buttonSettings.isSelected()) {
            value = buttonSettings.getOnValue();
           } else {
            value = buttonSettings.getOffValue();
           }
            tObj = TclString.newInstance(value);
            interp.setVar(buttonSettings.getVarName(), tObj, TCL.GLOBAL_ONLY);
    }
    public void tclAction(ButtonSettings buttonSettings) throws TclException {
        tclActionVar(buttonSettings);
        if (buttonSettings.getCommand().length() != 0) {
            try {
                interp.eval(buttonSettings.getCommand());
            } catch (TclException tclException) {
                interp.addErrorInfo("\n    (\"binding\" script)");
                interp.backgroundError();
            }
        }
    }

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
        traceLock = false;
    }
}
