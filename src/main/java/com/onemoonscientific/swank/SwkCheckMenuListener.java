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
public class SwkCheckMenuListener implements ActionListener, VarTrace,
        SwkListener {

    Interp interp;
    String command = "";
    String onValue = "1";
    String offValue = "0";
    String varName = "";
    JMenuItem component;
    boolean traceLock = false;

    SwkCheckMenuListener(Interp interp, Component component) {
        this.interp = interp;
        this.component = (JMenuItem) component;
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
                    "SwkCheckMenuListener: traceProc on event thread");
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
                    "SwkCheckMenuListener: setFromVar on event thread");
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

    /**
     *
     * @param interp
     * @param name
     * @throws TclException
     */
    public void setVarName(Interp interp, String name)
            throws TclException {
        if (EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkCheckMenuListener: setVarName on event thread");
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

        if ((name != null) && !name.equals("")) {
            interp.traceVar(name, this, TCL.TRACE_WRITES | TCL.GLOBAL_ONLY);
        }

        varName = name;
    }

    /**
     *
     * @return
     */
    public String getVarName() {
        return (varName);
    }

    /**
     *
     * @param value
     */
    public void setOnValue(String value) {
        if (!EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkCheckMenuListener: setOnValue not on event thread");
        }

        TclObject tObj;

        if (((SwkJCheckBoxMenuItem) component).isSelected()
                && !(value.equals("onValue"))) {
            actionPerformed(null);
        }

        onValue = value;
    }

    /**
     *
     * @return
     */
    public String getOffValue() {
        return (offValue);
    }

    /**
     *
     * @param value
     */
    public void setOffValue(String value) {
        if (!EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkCheckMenuListener: setOffValue not on event thread");
        }

        if (!((SwkJCheckBoxMenuItem) component).isSelected()
                && !(value.equals("offValue"))) {
            actionPerformed(null);
        }

        offValue = value;
    }

    /**
     *
     * @return
     */
    public String getOnValue() {
        return (onValue);
    }

    /**
     *
     * @param name
     */
    public void setCommand(String name) {
        command = name;
    }

    /**
     *
     * @return
     */
    public String getCommand() {
        return (command);
    }

    public void actionPerformed(ActionEvent e) {
        if (!EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkCheckMenuListener: actionPerformed not on event thread");
        }

        String value;

        if (((SwkJCheckBoxMenuItem) component).isSelected()) {
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

    /**
     *
     * @param eventObject
     * @param obj
     * @param subtype
     */
    public void processEvent(EventObject eventObject, Object obj, int subtype) {
        if (EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkCheckMenuListener: processEvent on event thread");
        }

        if ((command != null) && (command.length() != 0)) {
            try {
                interp.eval(command);
            } catch (TclException tclException) {
                interp.addErrorInfo("\n    (\"binding\" script)");
                interp.backgroundError();
            }
        }

        traceLock = false;
    }
}
