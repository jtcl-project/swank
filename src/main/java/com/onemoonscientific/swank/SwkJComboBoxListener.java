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

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.*;
import java.util.*;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.text.*;
import tcl.lang.Interp;
import tcl.lang.TCL;
import tcl.lang.TclException;
import tcl.lang.TclObject;
import tcl.lang.TclString;
import tcl.lang.VarTrace;

public class SwkJComboBoxListener implements ActionListener, VarTrace,
        SwkListener {

    Interp interp;
    String command = "";
    String value = "";
    String varName = "";
    final JComboBox component;
    boolean traceLock = false;
    boolean actionDisabled = false;

    SwkJComboBoxListener(final Interp interp, final Component component) {
        this.interp = interp;
        this.component = (JComboBox) component;
    }

    public void traceProc(Interp interp, String string1, String string2,
            int flags) throws TclException {
        if (EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkJComboBoxListener: traceProc on event thread");
        }

        setFromVar(interp);
    }

    public void setFromVar(Interp interp) throws TclException {
        if (EventQueue.isDispatchThread()) {
            System.out.println(
                    "Warning: ComboBoxListener setFrom Var on EventThread");
        }

        if (!traceLock) {
            try {
                TclObject tobj = interp.getVar(varName, TCL.GLOBAL_ONLY).duplicate();

                if (tobj != null) {
                    final String item = tobj.toString().trim();

                    if (item.length() > 0) {
                        actionDisabled = true;

                        try {
                            SwingUtilities.invokeLater(new Runnable() {

                                public void run() {
                                    component.setSelectedItem(item);
                                }
                            });
                        } catch (Exception e) {
                            System.out.println("coudn't set item in combo "
                                    + item);
                        }
                    }
                }
            } catch (TclException tclE) {
            } finally {
                actionDisabled = false;
            }
        }

        traceLock = false;
    }

    public void setVarName(Interp interp, String name)
            throws TclException {
        if (EventQueue.isDispatchThread()) {
            System.out.println(
                    "Warning: ComboBoxListener setFrom Var on EventThread");
        }

        if ((varName != null) && (!varName.equals(""))) {
            interp.untraceVar(varName, this, TCL.TRACE_WRITES
                    | TCL.GLOBAL_ONLY);
        }

        if ((name != null) && (name != "")) {
            try {
                TclObject tobj = interp.getVar(name, TCL.GLOBAL_ONLY).duplicate();

                if (tobj != null) {
                    final String item = tobj.toString().trim();

                    if (item.length() > 0) {
                        //actionDisabled = true;
                        try {
                            SwingUtilities.invokeLater(new Runnable() {

                                public void run() {
                                    component.setSelectedItem(item);
                                }
                            });
                        } catch (Exception e) {
                            System.out.println("coudn't set item in combo "
                                    + item);
                        }
                    }
                }
            } catch (TclException tclException) {
                int count = (new GetItemCount()).exec();
                String firstItem = "";

                if (count > 0) {
                    firstItem = (new GetItemAt()).exec(0);
                }

                TclObject tobj = TclString.newInstance(firstItem);
                interp.setVar(name, tobj, TCL.GLOBAL_ONLY);
            }

            interp.traceVar(name, this, TCL.TRACE_WRITES | TCL.GLOBAL_ONLY);
        }

        varName = name;
        interp.resetResult();
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

    public void actionPerformed(ActionEvent e) {
        if (e.getModifiers() == 0) {
            return;
        }
        //if (!((SwkWidget) component).isCreated()) {
        //   return;
        //}

        String s1 = component.getSelectedItem().toString();

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
                    "Warning: ComboBoxListener setVarValue on EventThread");
        }

        if ((varName != null) && (varName.length() != 0)) {
            Object obj = component.getSelectedItem();

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
                    "Warning: ComboBoxListener setVarValue on EventThread");
        }

        if ((varName != null) && (varName.length() != 0)) {
            //  Object obj = component.getSelectedItem();
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
         //   System.out.println("Process Event" + this);
        if (EventQueue.isDispatchThread()) {
            System.out.println(
                    "Warning: ComboBoxListener processEvent is on EventThread");
        }

        // FIXME some of this should remain on Swing ET
        // XXX Silly hack to keep from firing command when adding items to combobox
        if (actionDisabled) {
            actionDisabled = false;

            return;
        }

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

    private class GetItemCount extends GetValueOnEventThread {

        int intResult;

        int exec() {
            execOnThread();

            return intResult;
        }

        @Override
        public void run() {
            intResult = component.getItemCount();
        }
    }

    private class GetItemAt extends GetValueOnEventThread {

        String result;
        int index = 0;

        String exec(int index) {
            this.index = index;
            execOnThread();

            return result;
        }

        @Override
        public void run() {
            result = component.getItemAt(index).toString();
        }
    }
}
