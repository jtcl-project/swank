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

import java.lang.*;

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;


public class SwkDocumentListener implements DocumentListener, VarTrace,
    TraceLock {
    Interp interp;
    String varName = null;
    JTextComponent jtext;
    boolean traceLock = false;
    boolean eventLock = false;
    boolean updateLock = false;

    SwkDocumentListener(Interp interp, JTextComponent jtext) {
        this.interp = interp;
        this.jtext = jtext;
    }

    public void setEventLock(boolean newValue) {
        eventLock = newValue;
    }

    public void setUpdateLock(boolean newValue) {
        updateLock = newValue;
    }

    public void setTraceLock(boolean newValue) {
        traceLock = newValue;
    }

    public void traceProc(Interp interp, String string1, String string2,
        int flags) throws TclException {
        if (traceLock) {
            traceLock = false;

            return;
        }

        if (EventQueue.isDispatchThread()) {
            System.out.println("SwkDocumentListener: traceProc on event thread");
        }

        setFromVar(interp);
    }

    public void setFromVar(Interp interp) throws TclException {
        if (EventQueue.isDispatchThread()) {
            System.out.println(
                "SwkDocumentListener: setFromVar on event thread");
        }

        if (varName != null) {
            try {
                TclObject tobj = interp.getVar(varName, TCL.GLOBAL_ONLY);
                final String s1 = tobj.toString();
                SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            setEventLock(true);
                            setUpdateLock(true);
                            jtext.setText(s1);
                        }
                    });
            } catch (TclException tclE) {
            }
        }
    }

    public void setVarName(String name) throws TclException {
        if (EventQueue.isDispatchThread()) {
            System.out.println(
                "SwkDocumentListener: setVarName on event thread");
        }

        //      System.out.println("setVarName is called" + name);
        if ((varName != null) && (!varName.equals(""))) {
            interp.untraceVar(varName, this, TCL.TRACE_WRITES |
                TCL.GLOBAL_ONLY);
        }

        if ((name != null) && (name != "")) {
            try {
                TclObject tobj = interp.getVar(name, TCL.GLOBAL_ONLY);
                final String s1 = tobj.toString();
                SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            setEventLock(true);
                            setUpdateLock(true);
                            jtext.setText(s1);
                        }
                    });
            } catch (TclException tclException) {
                interp.resetResult();

                TclObject tobj = TclString.newInstance("");
                interp.setVar(name, tobj, TCL.GLOBAL_ONLY);
            }

            interp.traceVar(name, this, TCL.TRACE_WRITES | TCL.GLOBAL_ONLY);
        }

        varName = name;
    }

    public String getVarName() {
        return (varName);
    }

    public void updateVar(DocumentEvent docEvent) {
        if (!EventQueue.isDispatchThread()) {
            return;
        }

        if (varName == null) {
            return;
        }

        Document doc = docEvent.getDocument();
        String string = null;

        try {
            string = doc.getText(0, doc.getLength());
        } catch (BadLocationException e) {
            return;
        }

        SetStringVarEvent dvEvent = new SetStringVarEvent(interp, this,
                varName, null, string);

        //       System.out.println("updateVar is called"+varName + " "+string + " " + this.toString());
        interp.getNotifier().queueEvent(dvEvent, TCL.QUEUE_TAIL);
    }

    public void insertUpdate(DocumentEvent docEvent) {
        //      System.out.println("InsertUpdate :");
        if (!eventLock) {
            updateVar(docEvent);

            //setEventLock(true);
        } else {
            setEventLock(false);
        }
    }

    public void removeUpdate(DocumentEvent docEvent) {
        if (!updateLock) {
            updateVar(docEvent);

            //setEventLock(true);
        } else {
            setUpdateLock(false);
        }
    }

    public void changedUpdate(DocumentEvent docEvent) {
        updateVar(docEvent);
    }
}
