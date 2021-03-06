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
import java.util.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author brucejohnson
 */
public class SwkAppListener implements PropertyChangeListener, SwkListener {

    Interp interp;
    String command = "puts App";
    ArrayList<SwkBinding> bindings;

    SwkAppListener(Interp interp) {
        this.interp = interp;
        bindings = new ArrayList<SwkBinding>();
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
     * @param newBinding
     */
    public void setBinding(SwkBinding newBinding) {
        SwkBind.setBinding(bindings, newBinding);
    }

    /**
     *
     * @param eventObject
     * @param obj
     * @param subtype
     */
    public void processEvent(EventObject eventObject, Object obj, int subtype) {
        SwkBinding binding;
        int buttonMaswk;
        int i;

        for (i = 0; i < bindings.size(); i++) {
            binding = bindings.get(i);
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

    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();
        if (prop.equals("activeWindow")) {
            Component newComp = (Component) e.getNewValue();
            Component oldComp = (Component) e.getOldValue();
            BindEvent bEvent = null;
            if (newComp == null) {
                bEvent = new BindEvent(interp, (SwkListener) this, (EventObject) e, SwkBinding.OUT);
            } else if (oldComp == null) {
                bEvent = new BindEvent(interp, (SwkListener) this, (EventObject) e, SwkBinding.IN);
            }
            if (bEvent != null) {
                interp.getNotifier().queueEvent(bEvent, TCL.QUEUE_TAIL);
            }
        }
    }
}
