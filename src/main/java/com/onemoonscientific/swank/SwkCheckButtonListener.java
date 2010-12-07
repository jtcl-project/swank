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
    String command = "";
    String onValue = "1";
    String offValue = "0";
    String varName = "";
    JToggleButton component;
    boolean traceLock = false;

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

        if (!traceLock && (varName != null) && (!varName.equals(""))) {
            {
                TclObject tobj = interp.getVar(varName, TCL.GLOBAL_ONLY);
                final boolean state;

                if (tobj.toString().equals(onValue)) {
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

    public void setVarName(Interp interp, String name)
            throws TclException {
        if (EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkCheckButtonListener: setVarName on event thread");
        }

        TclObject tObj = null;

        if ((name != null) && !name.equals("")) {
            try {
                tObj = interp.getVar(name, TCL.GLOBAL_ONLY);

                if (tObj.toString().equals(onValue)) {
                    final boolean state = true;
                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            component.setSelected(state);
                        }
                    });
                }
                if (tObj.toString().equals(offValue)) { //Added for the configure case where checkbutton should become disabled
                    final boolean state = false;
                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            component.setSelected(state);
                        }
                    });
                }
            } catch (TclException tclException) {
                interp.resetResult();

                if ((offValue != null) && !offValue.equals("")) {
                    tObj = TclString.newInstance(offValue);
                } else {
                    tObj = TclString.newInstance("0");
                }

                interp.setVar(name, tObj, TCL.GLOBAL_ONLY);
            }
        }

        if ((varName != null) && (!varName.equals(""))) {
            interp.untraceVar(varName, this, TCL.TRACE_WRITES
                    | TCL.GLOBAL_ONLY);
        }

        if (!name.equals("")) {
            interp.traceVar(name, this, TCL.TRACE_WRITES | TCL.GLOBAL_ONLY);
        }

        varName = name;
    }

    public String getVarName() {
        return (varName);
    }

    public void setOnValue(String value) {
        if (!EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkCheckButtonListener: setOnValue not on event thread");
        }

        TclObject tObj;

        if (((SwkJCheckBox) component).isSelected()
                && !(value.equals(onValue))) {
            actionPerformed(null);
        }

        onValue = value;
    }

    public String getOffValue() {
        return (offValue);
    }

    public void setOffValue(String value) {
        if (!EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkCheckButtonListener: setOffValue not on event thread");
        }

        if (!((SwkJCheckBox) component).isSelected()
                && !(value.equals(offValue))) {
            actionPerformed(null);
        }

        offValue = value;
    }

    public String getOnValue() {
        return (onValue);
    }

    public void setCommand(String name) {
        command = name;
    }

    public String getCommand() {
        return (command);
    }

    public void actionPerformed(ActionEvent e) {
        if (!EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkCheckButtonListener: actionPerformed not on event thread");
        }

        String value;

        if (((SwkJCheckBox) component).isSelected()) {
            value = onValue;
        } else {
            value = offValue;
        }

        traceLock = true;

        if ((varName != null) && (!varName.equals(""))) {
            SetStringVarEvent strEvent = new SetStringVarEvent(interp, varName,
                    null, value);
            interp.getNotifier().queueEvent(strEvent, TCL.QUEUE_TAIL);
        }

        BindEvent bEvent = new BindEvent(interp, (SwkListener) this,
                (EventObject) e, 0);
        interp.getNotifier().queueEvent(bEvent, TCL.QUEUE_TAIL);
    }

    public void tclAction() {
        if ((command != null) && (command.length() != 0)) {
            try {
                interp.eval(command);
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

        tclAction();
        traceLock = false;
    }
}
