/*

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

public class SwkSliderChangeListener implements ChangeListener, VarTrace {

    Interp interp;
    String command = null;
    String varName = "";
    double value = 0.0;
    Component component;
    boolean actionLock = false;

    SwkSliderChangeListener(Interp interp, Component component) {
        this.interp = interp;
        this.component = component;
    }

    public void setCommand(String name) {
        command = name.intern();
    }

    public String getCommand() {
        return (command);
    }

    public void traceProc(Interp interp, String string1, String string2,
            int flags) throws TclException {
        if (EventQueue.isDispatchThread()) {
            System.out.println(
                    "tp is ettttttttttttttttttttttttttttttttttttttttttt");
        }

        // if the variable is removed restore it
        if ((flags & TCL.TRACE_UNSETS) != 0) {
            if ((varName != null) && (!varName.equals(""))) {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        setVarValueET();
                    }
                });
                interp.traceVar(varName, this,
                        TCL.TRACE_UNSETS | TCL.TRACE_WRITES | TCL.GLOBAL_ONLY);
            }
        } else {
            setFromVar(interp, false);
        }
    }

    public void setFromVar(Interp interp, boolean override)
            throws TclException {
        TclObject tobj = null;
        if (EventQueue.isDispatchThread()) {
            System.out.println(
                    "sfv is ettttttttttttttttttttttttttttttttttttttttttt");
        }

        actionLock = true;

        try {
            tobj = interp.getVar(varName, TCL.GLOBAL_ONLY);
        } catch (TclException tclE) {
        }

        if (tobj != null) {
            // FIXME following is kluge for when slider and entry are both bound
            // to same variable
            if (!tobj.toString().equals("")) {
                try {
                    value = TclDouble.get(interp, tobj);

                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            ((SwkJSlider) component).setDValue(value);
                        }
                    });
                } catch (TclException tclE) {
                    if (override) {
                        SwingUtilities.invokeLater(new Runnable() {

                            public void run() {
                                setVarValueET();
                            }
                        });
                    } else {
                        throw new TclException(interp, "can't assign non-numeric value to scale variable");
                    }
                }
            }
        }
    }

    public Double setVarName(Interp interp, String name)
            throws TclException {
        actionLock = true;

        if (EventQueue.isDispatchThread()) {
            System.out.println(
                    "svn is ettttttttttttttttttttttttttttttttttttttttttt");
        }
        if ((varName != null) && (!varName.equals(""))) {
            interp.untraceVar(varName, this, TCL.TRACE_WRITES
                    | TCL.GLOBAL_ONLY);
        }

        varName = name.intern();
        Double dValue = null;
        if (!name.equals("")) {
            try {
                TclObject tobj;
                tobj = interp.getVar(varName, TCL.GLOBAL_ONLY);
                if (!tobj.toString().equals("")) {
                    dValue = TclDouble.get(interp, tobj);
                }
            } catch (TclException tclE) {
                TclObject tobj;
                dValue = ((SwkJSlider) component).getDValue();;
                tobj = TclDouble.newInstance(dValue);
                interp.setVar(varName, tobj, TCL.GLOBAL_ONLY);
                interp.resetResult();
            }

            interp.traceVar(name, this,
                    TCL.TRACE_UNSETS | TCL.TRACE_WRITES | TCL.GLOBAL_ONLY);
        }
        actionLock = false;
        return dValue;
    }

    public String getVarName() {
        return (varName);
    }

    public void setVarValueET() {
        if (!EventQueue.isDispatchThread()) {
            System.out.println(
                    "svvet not ettttttttttttttttttttttttttttttttttttttttttt");
        }
        actionLock = true;

        if ((((SwkJSlider) component).resolution >= 1.0)
                && (((SwkJSlider) component).digits == 0)) {
            SetIntVarEvent intEvent = new SetIntVarEvent(interp, varName, null,
                    (int) value);
            interp.getNotifier().queueEvent(intEvent, TCL.QUEUE_TAIL);
        } else {
            SetDoubleVarEvent dvEvent = new SetDoubleVarEvent(interp, varName,
                    null, value);
            interp.getNotifier().queueEvent(dvEvent, TCL.QUEUE_TAIL);
        }

        actionLock = false;
    }

    public void stateChanged(ChangeEvent e) {
        if (!EventQueue.isDispatchThread()) {
            System.out.println(
                    "sc not ettttttttttttttttttttttttttttttttttttttttttt");
        }

        if (!((SwkWidget) component).isCreated()) {
            return;
        }

        if (actionLock) {
            actionLock = false;

            return;
        }

        value = ((SwkJSlider) component).getDValue();

        if ((varName != null) && (varName.length() != 0)) {
            setVarValueET();
        }

        if (!actionLock && (command != null) && (command.length() != 0)) {
            String cmd = null;

            if ((((SwkJSlider) component).resolution >= 1.0)
                    && (((SwkJSlider) component).digits == 0)) {
                cmd = command + " " + ((int) value);
            } else {
                cmd = command + " " + value;
            }

            BindEvent bEvent = new BindEvent(interp, cmd);
            interp.getNotifier().queueEvent(bEvent, TCL.QUEUE_TAIL);
        }
    }
}
