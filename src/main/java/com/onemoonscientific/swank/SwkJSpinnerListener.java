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

public class SwkJSpinnerListener implements ChangeListener, VarTrace,
        SwkListener {

    Interp interp;
    String command = "";
    String value = "";
    String varName = "";
    final JSpinner component;
    boolean traceLock = false;

    SwkJSpinnerListener(final Interp interp, final Component component) {
        this.interp = interp;
        this.component = (JSpinner) component;
    }

    public void traceProc(Interp interp, String string1, String string2,
            int flags) throws TclException {
        if (EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkJSpinnerListener: traceProc on event thread");
        }

        setFromVar(interp);
    }

    public void setFromVar(Interp interp) throws TclException {
        if (EventQueue.isDispatchThread()) {
            System.out.println(
                    "Warning: SpinnerListener setFrom Var on EventThread");
        }

        if (!traceLock) {
            try {
                TclObject tobj = interp.getVar(varName, TCL.GLOBAL_ONLY).duplicate();
                SpinnerModel model = component.getModel();
                if (tobj != null) {
                    final String sValue;
                    final double dValue;
                    if ((model == null) || !(model instanceof SpinnerNumberModel)) {
                        sValue = tobj.toString().trim();
                        dValue = 0.0;
                    } else {
                        dValue = TclDouble.get(interp, tobj);
                        sValue = null;
                    }
                    try {
                        SwingUtilities.invokeLater(new Runnable() {

                            public void run() {
                                if (sValue == null) {
                                    component.setValue(new Double(dValue));
                                } else {
                                    component.setValue(sValue);
                                }
                            }
                        });
                    } catch (Exception e) {
                        System.out.println("coudn't set item in spinner " + sValue);
                    }
                }
            } catch (TclException tclE) {
            } finally {
            }
        }

        traceLock = false;
    }

    public void setVarName(Interp interp, String name)
            throws TclException {
        if (EventQueue.isDispatchThread()) {
            System.out.println(
                    "Warning: SpinnerListener setFrom Var on EventThread");
        }

        if ((varName != null) && (!varName.equals(""))) {
            interp.untraceVar(varName, this, TCL.TRACE_WRITES
                    | TCL.GLOBAL_ONLY);
        }

        if ((name != null) && (name != "")) {
            try {
                TclObject tobj = interp.getVar(name, TCL.GLOBAL_ONLY).duplicate();

                if (tobj != null) {
                    SpinnerModel model = component.getModel();
                    final String sValue;
                    final double dValue;
                    if ((model == null) || !(model instanceof SpinnerNumberModel)) {
                        sValue = tobj.toString().trim();
                        dValue = 0.0;
                    } else {
                        dValue = TclDouble.get(interp, tobj);
                        sValue = null;
                    }

                    try {
                        SwingUtilities.invokeLater(new Runnable() {

                            public void run() {
                                if (sValue == null) {
                                    component.setValue(new Double(dValue));
                                } else {
                                    component.setValue(sValue);
                                }

                            }
                        });
                    } catch (Exception e) {
                        System.out.println("coudn't set item in spinner "
                                + sValue);
                    }
                }
            } catch (TclException tclException) {
            }

            interp.traceVar(name, this, TCL.TRACE_WRITES | TCL.GLOBAL_ONLY);
        }

        varName = name;
        interp.resetResult();
    }

    public String getVarName() {
        return (varName);
    }

    public void setCommand(String name) {
        command = name;
    }

    public String getCommand() {
        return (command);
    }

    public void keyReleased(JTextComponent editor, KeyEvent e) {
        String s1 = editor.getText();

        if ((varName != null) && (!varName.equals(""))) {
            SetStringVarEvent strEvent = new SetStringVarEvent(interp, varName,
                    null, s1);
            traceLock = true;
            interp.getNotifier().queueEvent(strEvent, TCL.QUEUE_TAIL);
        }

    }

    public void stateChanged(ChangeEvent e) {
        String s1 = component.getValue().toString();

        if ((varName != null) && (!varName.equals(""))) {
            SetStringVarEvent strEvent = new SetStringVarEvent(interp, varName,
                    null, s1);
            traceLock = true;
            interp.getNotifier().queueEvent(strEvent, TCL.QUEUE_TAIL);
        }

        BindEvent bEvent = new BindEvent(interp, (SwkListener) this,
                (EventObject) e, 0);
        interp.getNotifier().queueEvent(bEvent, TCL.QUEUE_TAIL);
    }

    public void setVarValue() {
        TclObject tobj = null;

        if (EventQueue.isDispatchThread()) {
            System.out.println(
                    "Warning: SpinnerListener setVarValue on EventThread");
        }

        if ((varName != null) && (varName.length() != 0)) {
            Object obj = component.getValue();

            if (obj != null) {
                tobj = TclString.newInstance(obj.toString());

                if (tobj != null) {
                    try {
                        traceLock = true;
                        interp.setVar(varName, tobj, TCL.GLOBAL_ONLY);
                    } catch (TclException tclException) {
                        return;
                    }
                }
            }
        }
    }

    public void setVarValue(String s1) {
        TclObject tobj = null;

        if (EventQueue.isDispatchThread()) {
            System.out.println(
                    "Warning: SpinnerListener setVarValue on EventThread");
        }

        if ((varName != null) && (varName.length() != 0)) {
            //  Object obj = component.getValue();
            if (s1 != null) {
                tobj = TclString.newInstance(s1);

                if (tobj != null) {
                    try {
                        traceLock = true;
                        interp.setVar(varName, tobj, TCL.GLOBAL_ONLY);
                    } catch (TclException tclException) {
                        return;
                    }
                }
            }
        }
    }

    public void processEvent(EventObject eventObject, Object obj, int subtype) {
        ChangeEvent e = (ChangeEvent) eventObject;
        //   System.out.println("Process Event" + this);
        if (EventQueue.isDispatchThread()) {
            System.out.println(
                    "Warning: SpinnerListener processEvent is on EventThread");
        }

        // FIXME some of this should remain on Swing ET

        //   setVarValue();
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
