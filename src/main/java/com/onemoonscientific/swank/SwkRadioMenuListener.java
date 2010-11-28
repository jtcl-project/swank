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


public class SwkRadioMenuListener implements ActionListener, VarTrace,
    SwkListener {
    Interp interp;
    String command = "";
    String value = "";
    String varName = "";
    JMenuItem component;
    boolean traceLock = false;

    SwkRadioMenuListener(Interp interp, Component component) {
        this.interp = interp;
        this.component = (JMenuItem) component;
    }

    public void traceProc(Interp interp, String string1, String string2,
        int flags) throws TclException {
        if (EventQueue.isDispatchThread()) {
            System.out.println(
                "SwkRadioMenuListener: traceProc on event thread");
        }

        setFromVar(interp);
    }

    public void setFromVar(Interp interp) throws TclException {
        if (EventQueue.isDispatchThread()) {
            System.out.println(
                "SwkRadioMenuListener: setFromVar on event thread");
        }

        if (!traceLock) {
            TclObject wObj = (TclObject) Widgets.getWidget(interp,component.getName());

            if (wObj == null) {
                return;
            }

            try {
                TclObject tobj = interp.getVar(varName, TCL.GLOBAL_ONLY);

                if (tobj.toString().equals(value)) {
                    SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                component.setSelected(true);
                            }
                        });
                }
            } catch (TclException tclE) {
            }
        }

        traceLock = false;
    }

    public void setVarName(Interp interp, String name)
        throws TclException {
        // ButtonGroup bgroup;
        // FIXME  some of this should be on event thread
        if ((varName != null) && (!varName.equals(""))) {
            ButtonGroup bgroup = (ButtonGroup) SwkJRadioButton.bgroupTable.get(varName);
            interp.untraceVar(varName, this, TCL.TRACE_WRITES |
                TCL.GLOBAL_ONLY);

            if (bgroup != null) {
                bgroup.remove(component);
            }
        }

        TclObject tObj = null;

        if ((name != null) && !name.equals("")) {
            try {
                tObj = interp.getVar(name, TCL.GLOBAL_ONLY);
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

        varName = name.intern();
    }

    public String getVarName() {
        return (varName);
    }

    public void setValue(String name) {
        value = name;
    }

    public String getValue() {
        return (value);
    }

    public void setCommand(String name) {
        command = name.intern();
    }

    public String getCommand() {
        return (command);
    }

    public void actionPerformed(ActionEvent e) {
        if (!EventQueue.isDispatchThread()) {
            System.out.println(
                "SwkCheckMenuListener: actionPerformed not on event thread");
        }

        String myValue;

        if (((SwkJRadioButtonMenuItem) component).isSelected()) {
            myValue = value;
        } else {
            myValue = "";
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

    public void processEvent(EventObject eventObject, Object obj, int subtype) {
        ActionEvent e = (ActionEvent) eventObject;

        if (EventQueue.isDispatchThread()) {
            System.out.println(
                "SwkRadioMenuListener: processEvent on event thread");
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
