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

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import tcl.lang.*;

/**
 *
 * @author brucejohnson
 */
public class SwkMouseWheelListener implements MouseWheelListener, SwkListener {

    Interp interp;
    String command = "puts mouse";
    ArrayList<SwkBinding> bindings;
    Component component;

    SwkMouseWheelListener(Interp interp, Component component) {
        this.interp = interp;
        this.component = component;
        bindings = new ArrayList<SwkBinding>();
    }

    /**
     *
     * @param name
     */
    public void setCommand(String name) {
        command = name.intern();
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
     * @param newBinding
     */
    public void setBinding(SwkBinding newBinding) {
        SwkBind.setBinding(bindings, newBinding);
        Collections.sort(bindings);
    }

    /**
     *
     * @return
     */
    public String getCommand() {
        return (command);
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        processMouse(e, SwkBinding.MOUSEWHEEL);
    }

 

    /**
     *
     * @param e
     * @param subtype
     */
    public void processMouse(MouseWheelEvent e, int subtype) {
        BindEvent bEvent = new BindEvent(interp, (SwkListener) this,
                (EventObject) e, subtype);
        interp.getNotifier().queueEvent(bEvent, TCL.QUEUE_TAIL);
    }

    /**
     *
     * @param eventObject
     * @param obj
     * @param subtype
     */
    public void processEvent(EventObject eventObject, Object obj, int subtype) {
        if (!(eventObject instanceof MouseWheelEvent)) {
            return;
        }
        MouseWheelEvent e = (MouseWheelEvent) eventObject;

        if (e.isConsumed()) {
            return;
        }

        SwkBinding binding;
        int mods = e.getModifiersEx();
        int button = e.getButton();
        int i;

        if (subtype != SwkBinding.EXIT) {
            //component.requestFocus ();
        }

        ArrayList<SwkBinding> tagBindings = null;
        Vector tagList = ((SwkWidget) component).getTagList();

        for (int j = 0; j < tagList.size(); j++) {
            tagBindings = null;

            String tag = (String) tagList.elementAt(j);

            if (tag.equals(((SwkWidget) component).getName())) {
                tagBindings = this.bindings;
            } else if (tag.startsWith(".")) {
                try {
                    tagBindings = ((SwkWidget) Widgets.get(interp, tag)).getMouseListener().getBindings();
                } catch (TclException tclE) {
                }
            } else {
                tagBindings = BindCmd.getMouseBindings(tag);
            }

            if (tagBindings == null) {
                continue;
            }
            SwkBinding lastBinding = null;
            for (i = 0; i < tagBindings.size(); i++) {
                binding = tagBindings.get(i);

                if (binding.type != SwkBinding.MOUSEWHEEL) {
                    continue;
                }

                if (binding.subtype != subtype) {
                    continue;
                }
                if ((subtype != SwkBinding.ENTER)
                        && (subtype != SwkBinding.EXIT)) {
                    if ((e.getClickCount() > 0)
                            && (binding.count > e.getClickCount())) {
                        continue;
                    }

                    if (!SwkMouseMotionListener.checkButtons(binding.detail,
                            button)) {
                        continue;
                    }

                    if (!SwkMouseMotionListener.checkMods(binding.mod, mods)) {
                        continue;
                    }
                }
                if (binding.sameButClick(lastBinding)) {
                    continue;
                }

                if ((binding.command != null)
                        && (binding.command.length() != 0)) {
                    try {
                        //System.out.println("doCmdMouse "+eventObject.toString()+" "+binding.command);
                        BindCmd.doCmd(interp, binding.command, e);
                    } catch (TclException tclE) {
                        if (tclE.getCompletionCode() == TCL.BREAK) {
                            e.consume();

                            return;
                        } else {
                            interp.addErrorInfo("\n    (\"binding\" script)");
                            interp.backgroundError();
                        }
                    }
                }
                lastBinding = binding;
            }
        }
    }
}
