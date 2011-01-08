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
import java.awt.event.*;
import java.util.*;

public class SwkWindowListener extends WindowAdapter implements SwkListener {

    Interp interp;
    String command = "puts component";
    ArrayList<SwkBinding> bindings;
    Window component;

    SwkWindowListener(Interp interp, Window component) {
        this.interp = interp;
        this.component = component;
        bindings = new ArrayList<SwkBinding>();
    }

    public ArrayList<SwkBinding> getBindings() {
        return bindings;
    }

    public void setCommand(String name) {
        command = name;
    }

    public void setBinding(SwkBinding newBinding) {
        SwkBinding binding = null;

        if (!newBinding.add) {
            for (int i = 0; i < bindings.size(); i++) {
                binding = (SwkBinding) bindings.get(i);

                if (binding.equals(newBinding)) {
                    bindings.set(i, newBinding);

                    return;
                }
            }
        }

        bindings.add(newBinding);
    }

    public String getCommand() {
        return (command);
    }

    public void windowActivated(WindowEvent e) {
        processWindow(e, SwkBinding.ACTIVATED);
    }

    public void windowDeactivated(WindowEvent e) {
        processWindow(e, SwkBinding.DEACTIVATED);
    }

    public void processWindow(WindowEvent e, int subtype) {
        BindEvent bEvent = new BindEvent(interp, (SwkListener) this,
                (EventObject) e, subtype);
        interp.getNotifier().queueEvent(bEvent, TCL.QUEUE_TAIL);
    }

    public void processEvent(EventObject eventObject, Object obj, int subtype) {
        if (!(eventObject instanceof WindowEvent)) {
            return;
        }
        WindowEvent e = (WindowEvent) eventObject;
        ArrayList<SwkBinding> tagBindings = null;
        Vector tagList = ((SwkWidget) component).getTagList();

        SwkBinding binding;
        for (int j = 0; j < tagList.size(); j++) {
            tagBindings = null;

            String tag = (String) tagList.elementAt(j);
            if (tag.equals(((SwkWidget) component).getName())) {
                tagBindings = this.bindings;
            } else if (tag.startsWith(".")) {
                try {
                    tagBindings = ((SwkJFrame) Widgets.get(interp, tag)).getWindowListener().getBindings();
                } catch (TclException tclE) {
                }
            } else {
                tagBindings = BindCmd.getActivationBindings(tag);
            }

            if (tagBindings == null) {
                continue;
            }

            for (int i = 0; i < tagBindings.size(); i++) {
                binding = (SwkBinding) tagBindings.get(i);
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
