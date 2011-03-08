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

/**
 *
 * @author brucejohnson
 */
public class SwkFocusListener implements FocusListener, SwkListener {

    Interp interp;
    String command = "puts Focus";
    Component component;
    ArrayList<SwkBinding> bindings;

    SwkFocusListener(Interp interp, Component component) {
        this.interp = interp;
        this.component = component;
        bindings = new ArrayList<SwkBinding>();
    }

    /**
     *
     * @return
     */
    public ArrayList<SwkBinding> getBindings() {
        return bindings;
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

    public void focusGained(FocusEvent e) {
        if (interp != null) {
            BindEvent bEvent = new BindEvent(interp, (SwkListener) this,
                    (EventObject) e, SwkBinding.IN);
            interp.getNotifier().queueEvent(bEvent, TCL.QUEUE_TAIL);
        }
    }

    public void focusLost(FocusEvent e) {
        if (interp != null) {
            BindEvent bEvent = new BindEvent(interp, (SwkListener) this,
                    (EventObject) e, SwkBinding.OUT);
            interp.getNotifier().queueEvent(bEvent, TCL.QUEUE_TAIL);
        }
    }

    /**
     *
     * @param eventObject
     * @param obj
     * @param subtype
     */
    public void processEvent(EventObject eventObject, Object obj, int subtype) {
        if (!(eventObject instanceof FocusEvent)) {
            return;
        }
        FocusEvent e = (FocusEvent) eventObject;

        ArrayList<SwkBinding> eventBindings = null;
        Vector tagList = ((SwkWidget) component).getTagList();

        SwkBinding binding;
        for (int j = 0; j < tagList.size(); j++) {
            eventBindings = null;

            String tag = (String) tagList.elementAt(j);
            if (tag.equals(((SwkWidget) component).getName())) {
                eventBindings = this.bindings;
            } else if (tag.startsWith(".")) {
                try {
                    eventBindings = ((SwkWidget) Widgets.get(interp, tag)).getFocusListener().getBindings();
                } catch (TclException tclE) {
                }
            } else {
                eventBindings = BindCmd.getFocusBindings(tag);
            }

            if (eventBindings == null) {
                continue;
            }

            for (int i = 0; i < eventBindings.size(); i++) {
                binding = (SwkBinding) eventBindings.get(i);
                if (binding.subtype != subtype) {
                    continue;
                }


                if ((binding.command != null) && (binding.command.length() != 0)) {
                    try {
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
    }
}
