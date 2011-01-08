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

public class SwkRadioButtonListener implements ActionListener, VarTrace,
        SwkListener {

    Interp interp;
    JToggleButton component;
    CommandVarListenerSettings buttonSettings = new CommandVarListenerSettings();

    CommandVarListenerSettings getButtonSettings() {
        buttonSettings.setEnabled(component.isEnabled());
        buttonSettings.setSelected(component.isSelected());
        return buttonSettings;
    }

    SwkRadioButtonListener(Interp interp, Component component) {
        this.interp = interp;
        this.component = (JToggleButton) component;
    }

    public void traceProc(Interp interp, String string1, String string2,
            int flags) throws TclException {
        if (EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkRadioButtonListener: traceProc on event thread");
        }

        setFromVar(interp);
    }

   public void setFromVar(Interp interp) throws TclException {
        if (EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkRadioButtonListener: setFromVar on event thread");
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

    public boolean setVarName(Interp interp, String name)
            throws TclException {
        //  ButtonGroup bgroup;
        // FIXME  some of this should be on event thread
        if (!buttonSettings.getVarName().equals("")) {
            ButtonGroup bgroup = (ButtonGroup) SwkJRadioButton.bgroupTable.get(buttonSettings.getVarName());
            interp.untraceVar(buttonSettings.getVarName(), this, TCL.TRACE_WRITES
                    | TCL.GLOBAL_ONLY);

            if (bgroup != null) {
                bgroup.remove(component);
            }
        }

        TclObject tObj = null;
        boolean state = false;
        if (name == null) {
            name = "";
        }
        if (!name.equals("")) {
            try {
                tObj = interp.getVar(name, TCL.GLOBAL_ONLY);
                state = tObj.toString().equals(buttonSettings.getValue()); //If the value doesn't match the state then deselect
            } catch (TclException tclException) {
                interp.resetResult();
                tObj = TclString.newInstance("");
                interp.setVar(name, tObj, TCL.GLOBAL_ONLY);
            }

            ButtonGroup bgroup = (ButtonGroup) SwkJRadioButton.bgroupTable.get(name);

            if (bgroup == null) {
                bgroup = new ButtonGroup();
                SwkJRadioButton.bgroupTable.put(name, bgroup);
            }

            final ButtonGroup bgroup2 = bgroup;
            interp.traceVar(name, this, TCL.TRACE_WRITES | TCL.GLOBAL_ONLY);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    bgroup2.add(component);
                }
            });
        }
        buttonSettings = buttonSettings.getWithVarName(name);
        return state;
    }

    public String getVarName() {
        return (buttonSettings.getVarName());
    }

    public void setValue(String newValue) {
       if (!EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkCheckButtonListener: setOnValue not on event thread");
        }

        TclObject tObj;

        if (((SwkJRadioButton) component).isSelected()
                && !(newValue.equals(buttonSettings.getValue()))) {
            actionPerformed(null);
        }

        buttonSettings = buttonSettings.getWithValue(newValue);
    }

    public String getValue() {
        return (buttonSettings.getValue());
    }

    public String getCommand() {
        return (buttonSettings.getCommand());
    }
    public void setCommand(String name) {
        buttonSettings = buttonSettings.getWithCommand(name);
    }


    public void actionPerformed(ActionEvent e) {
        if (!EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkCheckButtonListener: actionPerformed not on event thread");
        }


        if ((buttonSettings.getVarName() != null) && (!buttonSettings.getVarName().equals(""))) {
            SetStringVarEvent strEvent = new SetStringVarEvent(interp, buttonSettings.getVarName(),
                    null, buttonSettings.getValue());
            interp.getNotifier().queueEvent(strEvent, TCL.QUEUE_TAIL);
        }

        BindEvent bEvent = new BindEvent(interp, (SwkListener) this,
                (EventObject) e, 0);
        interp.getNotifier().queueEvent(bEvent, TCL.QUEUE_TAIL);
    }
    public void tclActionVar(CommandVarListenerSettings buttonSettings) throws TclException {
        final TclObject tObj;
        final String value;
           if (buttonSettings.isSelected()) {
            value = buttonSettings.getValue();
           } else {
            value = "";
           }
            tObj = TclString.newInstance(value);
            interp.setVar(buttonSettings.getVarName(), tObj, TCL.GLOBAL_ONLY);
    }
    public void tclAction(CommandVarListenerSettings buttonSettings) throws TclException {
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
                    "SwkRadioButtonListener: processEvent on event thread");
        }

        if (buttonSettings.getCommand().length() != 0) {
            try {
                interp.eval(buttonSettings.getCommand());
            } catch (TclException tclE) {
                interp.addErrorInfo("\n    (\"binding\" script)");
                interp.backgroundError();
            }
        }
    }
}
