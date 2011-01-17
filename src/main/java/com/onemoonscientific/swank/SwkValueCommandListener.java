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
public class SwkValueCommandListener implements ActionListener, VarTrace,
        SwkListener {

    Interp interp;
    String command = "";
    String value = "";
    String varName = "";
    JToggleButton component;
    boolean traceLock = false;

    SwkValueCommandListener(Interp interp, Component component) {
        this.interp = interp;
        this.component = (JToggleButton) component;
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
                    "SwkValueCommandListener: traceProc on event thread");
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
                    "SwkValueCommandListener: setFromVar on event thread");
        }

        if (!traceLock) {
            try {
                TclObject tobj = interp.getVar(varName, TCL.GLOBAL_ONLY);

                if (component instanceof SwkJRadioButton) {
                    if (tobj.toString().equals(value)) {
                        component.setSelected(false);
                        component.doClick();
                    } else {
                        component.setSelected(true);
                        component.doClick();
                    }
                } else {
                    if (tobj.toString().equals("0")) {
                        component.setSelected(true);
                        component.doClick();
                    } else {
                        component.setSelected(false);
                        component.doClick();
                    }
                }
            } catch (TclException tclE) {
            }
        }

        traceLock = false;
    }

    /**
     *
     * @param name
     * @throws TclException
     */
    public void setVarName(String name) throws TclException {
        if ((varName != null) && (!varName.equals(""))) {
            interp.untraceVar(varName, this, TCL.TRACE_WRITES
                    | TCL.GLOBAL_ONLY);
        }

        if (!name.equals("")) {
            interp.traceVar(name, this, TCL.TRACE_WRITES | TCL.GLOBAL_ONLY);
        }

        varName = name.intern();
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
     * @param name
     */
    public void setValue(String name) {
        value = name.intern();
    }

    /**
     *
     * @return
     */
    public String getValue() {
        return (value);
    }

    /**
     *
     * @param name
     */
    public void setCommand(String name) {
        command = name.intern();
    }

    /**
     *
     * @return
     */
    public String getCommand() {
        return (command);
    }

    public void actionPerformed(ActionEvent e) {
        BindEvent bEvent = new BindEvent(interp, (SwkListener) this,
                (EventObject) e, 0);
        interp.getNotifier().queueEvent(bEvent, TCL.QUEUE_TAIL);
    }

    public void processEvent(EventObject eventObject, Object obj, int subtype) {
        TclObject tobj = null;

        if ((varName != null) && (varName.length() != 0)) {
            if (component instanceof SwkJRadioButton) {
                if (value != null) {
                    if (((SwkJRadioButton) component).isSelected()) {
                        tobj = TclString.newInstance(value);
                    } else {
                        tobj = TclString.newInstance("");
                    }
                }
            } else if (component instanceof SwkJCheckBox) {
                if (((SwkJCheckBox) component).isSelected()) {
                    tobj = TclString.newInstance("1");
                } else {
                    tobj = TclString.newInstance("0");
                }
            }

            if (tobj != null) {
                try {
                    traceLock = true;
                    interp.setVar(varName, tobj, TCL.GLOBAL_ONLY);
                } catch (TclException tclException) {
                    return;
                }
            }
        }

        if ((command != null) && (command.length() != 0)) {
            try {
                interp.eval(command);
            } catch (TclException tclE) {
                interp.addErrorInfo("\n    (\"binding\" script)");
                interp.backgroundError();
            }
        }
    }
}
