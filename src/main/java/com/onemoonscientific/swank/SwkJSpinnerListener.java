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
import javax.swing.event.*;
import javax.swing.text.*;

/**
 * 
 * @author brucejohnson
 */
public class SwkJSpinnerListener implements ChangeListener, VarTrace,
        SwkListener {

    Interp interp;
    String command = "";
    String varName = "";
    final JSpinner component;
    boolean traceLock = false;

    SwkJSpinnerListener(final Interp interp, final Component component) {
        this.interp = interp;
        this.component = (JSpinner) component;
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
                    "SwkJSpinnerListener: traceProc on event thread");
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
                    "Warning: SpinnerListener setFrom Var on EventThread");
        }

        if (!traceLock) {
            try {
                TclObject tobj = interp.getVar(varName, TCL.GLOBAL_ONLY).duplicate();
                SpinnerModel model = component.getModel();
                if (tobj != null) {
                    final String sValue;
                    final Number number;
                    if ((model == null) || !(model instanceof SpinnerNumberModel)) {
                        sValue = tobj.toString().trim();
                        number = null;
                    } else {
                        boolean gotInt = false;
                        double dValue = 0.0;
                        int iValue = 0;
                        try {
                            iValue = TclInteger.get(interp, tobj);
                            gotInt = true;
                        } catch (TclException dE) {
                            dValue = TclDouble.get(interp, tobj);
                        }
                        sValue = null;
                        if (gotInt) {
                           number = new Integer(iValue);
                        } else {
                           number = new Double(dValue);
                        }
                    }
                    try {
                        SwingUtilities.invokeLater(new Runnable() {

                            public void run() {
                                if (sValue == null) {
                                    component.setValue(number);
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
                    "Warning: SpinnerListener setFrom Var on EventThread");
        }

        if ((varName != null) && (!varName.equals(""))) {
            interp.untraceVar(varName, this, TCL.TRACE_WRITES
                    | TCL.GLOBAL_ONLY);
        }

        if ((name != null) && !name.equals("")) {
            try {
                TclObject tobj = interp.getVar(name, TCL.GLOBAL_ONLY).duplicate();
                SpinnerModel model = component.getModel();
             if (tobj != null) {
                    final String sValue;
                    final Number number;
                    if ((model == null) || !(model instanceof SpinnerNumberModel)) {
                        sValue = tobj.toString().trim();
                        number = null;
                    } else {
                        boolean gotInt = false;
                        double dValue = 0.0;
                        int iValue = 0;
                        try {
                            iValue = TclInteger.get(interp, tobj);
                            gotInt = true;
                        } catch (TclException dE) {
                            dValue = TclDouble.get(interp, tobj);
                        }
                        sValue = null;
                        if (gotInt) {
                           number = new Integer(iValue);
                        } else {
                           number = new Double(dValue);
                        }
                    }
                    try {
                        SwingUtilities.invokeLater(new Runnable() {

                            public void run() {
                                if (sValue == null) {
                                    component.setValue(number);
                                } else {
                                    component.setValue(sValue);
                                }
                            }
                        });
                    } catch (Exception e) {
                        System.out.println("coudn't set item in spinner " + sValue);
                    }
                }
            } catch (TclException tclException) {
            }

            interp.traceVar(name, this, TCL.TRACE_WRITES | TCL.GLOBAL_ONLY);
        }

        varName = name;
        interp.resetResult();
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

    /**
     *
     * @param editor
     * @param e
     */
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

    /**
     *
     */
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

    /**
     *
     * @param s1
     */
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

    /**
     *
     * @param eventObject
     * @param obj
     * @param subtype
     */
    public void processEvent(EventObject eventObject, Object obj, int subtype) {
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
