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

public class SwkScrollAdjustmentListener implements AdjustmentListener,
        SwkListener {

    Interp interp;
    String command = null;
    Component component;

    SwkScrollAdjustmentListener(Interp interp, Component component) {
        this.interp = interp;
        this.component = component;
    }

    public void setCommand(String name) {
        command = name.intern();
    }

    public String getCommand() {
        return (command);
    }

    public void adjustmentValueChanged(AdjustmentEvent e) {
        SwkJScrollBar swkjscrollbar = (SwkJScrollBar) component;

        if (!swkjscrollbar.lastSetFromSet) {
            final int value = e.getValue();
            double fx1 = (1.0 * value) / (((SwkJScrollBar) component).getMaximum()
                    - ((SwkJScrollBar) component).getMinimum());
            Double fxd = new Double(fx1);
            BindEvent bEvent = new BindEvent(interp, (SwkListener) this,
                    (EventObject) e, fxd, 0);
            interp.getNotifier().queueEvent(bEvent, TCL.QUEUE_TAIL);
        } else {
            swkjscrollbar.lastSetFromSet = false;
        }
    }

    public void processEvent(EventObject eventObject, Object obj, int subtype) {
        AdjustmentEvent e = (AdjustmentEvent) eventObject;

        // FIXME first part below should be on Swing ET
        Double fxd = (Double) (obj);
        double fx1 = fxd.doubleValue();

        //  System.out.println("Moved to "+fx1);
        if ((command != null) && (command.length() != 0)) {
            try {
                interp.eval(command + " moveto " + fx1);
            } catch (TclException tclE) {
                System.out.println("error " + tclE.getMessage());
                interp.addErrorInfo("\n    (\"binding\" script)");
                interp.backgroundError();
            }
        }
    }
}
