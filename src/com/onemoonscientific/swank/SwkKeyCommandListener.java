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


public class SwkKeyCommandListener implements ActionListener, SwkListener {
    Interp interp;
    Component component;
    SwkBinding binding;

    SwkKeyCommandListener(Interp interp, SwkBinding binding, Component component) {
        this.interp = interp;
        this.component = component;
        this.binding = binding;
    }

    public void actionPerformed(ActionEvent e) {
        BindEvent bEvent = new BindEvent(interp, (SwkListener) this,
                (EventObject) e, 0);
        interp.getNotifier().queueEvent(bEvent, TCL.QUEUE_TAIL);
    }

    public void processEvent(EventObject eventObject, Object obj, int subtype) {
        ActionEvent e = (ActionEvent) eventObject;

        if ((binding.command != null) && (binding.command.length() != 0)) {
            try {
                //System.out.println("doCmd "+binding.command);
                BindCmd.doCmd(interp, binding, component, e);
            } catch (TclException tclE) {
                if (tclE.getCompletionCode() == TCL.BREAK) {
                    return;
                } else {
                    interp.addErrorInfo("\n    (\"binding\" script)");
                    interp.backgroundError();
                }
            }
        }
    }
}
