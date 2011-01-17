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
import javax.swing.*;
import javax.swing.event.*;

/**
 *
 * @author brucejohnson
 */
public class SwkSliderChangeListener implements ChangeListener, VarTrace {

    Interp interp;
    Component component;
    boolean actionLock = false;
    CommandVarListenerSettings buttonSettings = new CommandVarListenerSettings();

    CommandVarListenerSettings getButtonSettings() {
        buttonSettings.setEnabled(component.isEnabled());
        return buttonSettings;
    }

    SwkSliderChangeListener(Interp interp, Component component) {
        this.interp = interp;
        this.component = component;
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
                    "tp is ettttttttttttttttttttttttttttttttttttttttttt");
        }

        // if the variable is removed restore it
        if ((flags & TCL.TRACE_UNSETS) != 0) {
            if (!buttonSettings.getVarName().equals("")) {
                tclActionVar(buttonSettings);
                interp.traceVar(buttonSettings.getVarName(), this,
                        TCL.TRACE_UNSETS | TCL.TRACE_WRITES | TCL.GLOBAL_ONLY);
            }
        } else {
            setFromVar(interp, false);
        }
    }

    /**
     *
     * @param interp
     * @param override
     * @throws TclException
     */
    public void setFromVar(Interp interp, boolean override)
            throws TclException {
        TclObject tobj = null;
        if (EventQueue.isDispatchThread()) {
            System.out.println(
                    "sfv is ettttttttttttttttttttttttttttttttttttttttttt");
        }

        actionLock = true;

        try {
            tobj = interp.getVar(buttonSettings.getVarName(), TCL.GLOBAL_ONLY);
        } catch (TclException tclE) {
        }

        if (tobj != null) {
            // FIXME following is kluge for when slider and entry are both bound
            // to same variable
            if (!tobj.toString().equals("")) {
                try {
                    final double value = TclDouble.get(interp, tobj);
                    buttonSettings = buttonSettings.getWithValue(value);
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

    /**
     * 
     * @param interp
     * @param name
     * @return
     * @throws TclException
     */
    public Double setVarName(Interp interp, String name)
            throws TclException {
        actionLock = true;

        if (EventQueue.isDispatchThread()) {
            System.out.println(
                    "svn is ettttttttttttttttttttttttttttttttttttttttttt");
        }
        if (!buttonSettings.getVarName().equals("")) {
            interp.untraceVar(buttonSettings.getVarName(), this, TCL.TRACE_WRITES
                    | TCL.GLOBAL_ONLY);
        }

        Double dValue = null;
        if (!name.equals("")) {
            try {
                TclObject tObj;
                tObj = interp.getVar(name, TCL.GLOBAL_ONLY);
                if (!tObj.toString().equals("")) {
                    dValue = TclDouble.get(interp, tObj);
                }
            } catch (TclException tclE) {
                TclObject tObj;
                dValue = ((SwkJSlider) component).getDValue();

                if ((((SwkJSlider) component).resolution >= 1.0)
                        && (((SwkJSlider) component).digits == 0)) {
                    tObj = TclInteger.newInstance((int) dValue.doubleValue());

                } else {
                    tObj = TclDouble.newInstance(dValue.doubleValue());

                }
                interp.setVar(name, tObj, TCL.GLOBAL_ONLY);
                interp.resetResult();
            }

            interp.traceVar(name, this,
                    TCL.TRACE_UNSETS | TCL.TRACE_WRITES | TCL.GLOBAL_ONLY);
        }
        actionLock = false;
        buttonSettings = buttonSettings.getWithVarName(name);
        return dValue;
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
     */
    public void setVarValueET() {
        if (!EventQueue.isDispatchThread()) {
            System.out.println(
                    "svvet not ettttttttttttttttttttttttttttttttttttttttttt");
        }
        actionLock = true;
        if ((((SwkJSlider) component).resolution >= 1.0)
                && (((SwkJSlider) component).digits == 0)) {
            SetIntVarEvent intEvent = new SetIntVarEvent(interp, buttonSettings.getVarName(), null,
                    (int) (buttonSettings.getDValue()));
            interp.getNotifier().queueEvent(intEvent, TCL.QUEUE_TAIL);
        } else {
            SetDoubleVarEvent dvEvent = new SetDoubleVarEvent(interp, buttonSettings.getVarName(),
                    null, buttonSettings.getDValue());
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

        double value = ((SwkJSlider) component).getDValue();
        buttonSettings = buttonSettings.getWithValue(value);
        if (buttonSettings.getVarName().length() != 0) {
            setVarValueET();
        }

        if (!actionLock && (buttonSettings.getCommand() != null) && (buttonSettings.getCommand().length() != 0)) {
            String cmd = null;

            if ((((SwkJSlider) component).resolution >= 1.0)
                    && (((SwkJSlider) component).digits == 0)) {
                cmd = buttonSettings.getCommand() + " " + ((int) (buttonSettings.getDValue()));
            } else {
                cmd = buttonSettings.getCommand() + " " + buttonSettings.getDValue();
            }

            BindEvent bEvent = new BindEvent(interp, cmd);
            interp.getNotifier().queueEvent(bEvent, TCL.QUEUE_TAIL);
        }
    }

    /**
     *
     */
    public void updateSettingsValue() {
        buttonSettings = buttonSettings.getWithValue(((SwkJSlider) component).getDValue());
    }

    /**
     *
     * @param buttonSettings
     * @throws TclException
     */
    public void tclActionVar(CommandVarListenerSettings buttonSettings) throws TclException {
        final TclObject tObj;
        final double value;
        value = buttonSettings.getDValue();
        if ((((SwkJSlider) component).resolution >= 1.0)
                && (((SwkJSlider) component).digits == 0)) {
            tObj = TclInteger.newInstance((int) value);
        } else {
            tObj = TclDouble.newInstance(value);
        }
        interp.setVar(buttonSettings.getVarName(), tObj, TCL.GLOBAL_ONLY);
    }
}
