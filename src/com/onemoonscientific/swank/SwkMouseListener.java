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

import java.lang.*;

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;


public class SwkMouseListener implements MouseListener, SwkListener {
    Interp interp;
    String command = "puts mouse";
    Vector bindings;
    Component component;

    SwkMouseListener(Interp interp, Component component) {
        this.interp = interp;
        this.component = component;
        bindings = new Vector();
    }

    public void setCommand(String name) {
        command = name.intern();
    }

    public Vector getBindings() {
        return bindings;
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

    public String getCommand() {
        return (command);
    }

    public void mouseClicked(MouseEvent e) {
        processMouse(e, SwkBinding.CLICK);
    }

    public void mousePressed(MouseEvent e) {
        processMouse(e, SwkBinding.PRESS);
    }

    public void mouseReleased(MouseEvent e) {
        processMouse(e, SwkBinding.RELEASE);
    }

    public void mouseEntered(MouseEvent e) {
        processMouse(e, SwkBinding.ENTER);
    }

    public void mouseExited(MouseEvent e) {
        processMouse(e, SwkBinding.EXIT);
    }

    public void processMouse(MouseEvent e, int subtype) {
        BindEvent bEvent = new BindEvent(interp, (SwkListener) this,
                (EventObject) e, subtype);
        interp.getNotifier().queueEvent(bEvent, TCL.QUEUE_TAIL);
    }

    public void processEvent(EventObject eventObject, int subtype) {
        MouseEvent e = (MouseEvent) eventObject;

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

        Vector bindings = null;
        Vector tagList = ((SwkWidget) component).getTagList();

        for (int j = 0; j < tagList.size(); j++) {
            bindings = null;

            String tag = (String) tagList.elementAt(j);

            if (tag.equals(((SwkWidget) component).getName())) {
                bindings = this.bindings;
            } else if (tag.startsWith(".")) {
                try {
                    bindings = ((SwkWidget) Widgets.get(interp, tag)).getMouseListener()
                                .getBindings();
                } catch (TclException tclE) {
                }
            } else {
                bindings = BindCmd.getMouseBindings(tag);
            }

            if (bindings == null) {
                continue;
            }

            for (i = 0; i < bindings.size(); i++) {
                binding = (SwkBinding) bindings.elementAt(i);

                if (binding.type != SwkBinding.MOUSE) {
                    continue;
                }

                if (binding.subtype != subtype) {
                    continue;
                }

                if ((subtype != SwkBinding.ENTER) &&
                        (subtype != SwkBinding.EXIT)) {
                    if ((e.getClickCount() > 0) &&
                            (binding.count != e.getClickCount())) {
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

                if ((binding.command != null) &&
                        (binding.command.length() != 0)) {
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
            }
        }
    }
}
