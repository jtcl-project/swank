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


public class SwkFocusListener implements FocusListener, SwkListener {
    Interp interp;
    String command = "puts Focus";
    Component component;
    Vector bindings;

    SwkFocusListener(Interp interp, Component component) {
        this.interp = interp;
        this.component = component;
        bindings = new Vector();
    }

    public void setCommand(String name) {
        command = name;
    }

    public String getCommand() {
        return (command);
    }

    public void setBinding(SwkBinding newBinding) {
        SwkBinding binding = null;

        if (!newBinding.add) {
            for (int i = 0; i < bindings.size(); i++) {
                binding = (SwkBinding) bindings.elementAt(i);

                if (binding.equals(newBinding)) {
                    bindings.setElementAt(newBinding, i);

                    return;
                }
            }
        }

        bindings.addElement(newBinding);
    }

    public void focusGained(FocusEvent e) {
        BindEvent bEvent = new BindEvent(interp, (SwkListener) this,
                (EventObject) e, SwkBinding.IN);
        interp.getNotifier().queueEvent(bEvent, TCL.QUEUE_TAIL);
    }

    public void focusLost(FocusEvent e) {
        BindEvent bEvent = new BindEvent(interp, (SwkListener) this,
                (EventObject) e, SwkBinding.OUT);
        interp.getNotifier().queueEvent(bEvent, TCL.QUEUE_TAIL);
    }

    public void processEvent(EventObject eventObject, int subtype) {
        FocusEvent e = (FocusEvent) eventObject;

        SwkBinding binding;
        int buttonMaswk;
        int i;

        for (i = 0; i < bindings.size(); i++) {
            binding = (SwkBinding) bindings.elementAt(i);

            if (binding.subtype != subtype) {
                continue;
            }

            if ((binding.command != null) && (binding.command.length() != 0)) {
                try {
                    interp.eval(binding.command);
                } catch (TclException tclE) {
                    interp.addErrorInfo("\n    (\"binding\" script)");
                    interp.backgroundError();
                }
            }
        }
    }
}
