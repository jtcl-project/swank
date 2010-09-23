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


public class SwkMouseMotionListener implements MouseMotionListener, SwkListener {
    Interp interp;
    String command = "puts mouse";
    ArrayList<SwkBinding> bindings;
    Component component;
    boolean processing = false;

    SwkMouseMotionListener(Interp interp, Component component) {
        this.interp = interp;
        this.component = component;
        bindings = new ArrayList<SwkBinding>();
    }

    public void setCommand(String name) {
        command = name.intern();
    }
    public ArrayList<SwkBinding> getBindings() {
        return bindings;
    }

    public void setBinding(SwkBinding newBinding) {
        SwkBind.setBinding(bindings,newBinding);
    }

    public String getCommand() {
        return (command);
    }

    public void mouseMoved(MouseEvent e) {
        // System.out.println("move "+e);
        if (processing) {
            e.consume();
        } else {
            //processing = true;
            BindEvent bEvent = new BindEvent(interp, (SwkListener) this,
                    (EventObject) e, 0);
            interp.getNotifier().queueEvent(bEvent, TCL.QUEUE_TAIL);
        }
    }

    public void mouseDragged(MouseEvent e) {
        //System.out.println("drag "+e);
        if (processing) {
            e.consume();
        } else {
            //processing = true;
            BindEvent bEvent = new BindEvent(interp, (SwkListener) this,
                    (EventObject) e, 0);
            interp.getNotifier().queueEvent(bEvent, TCL.QUEUE_TAIL);
        }
    }

    public void processEvent(EventObject eventObject, Object obj, int subtype) {
        try {
            MouseEvent e = (MouseEvent) eventObject;

            if (e.isConsumed()) {
                return;
            }

            SwkBinding binding;
            int mods = e.getModifiersEx();

            int i;

            //  System.out.println(e.toString());
            int buttons = e.getButton();

            // System.out.println("button "+e.getButton());
            ArrayList<SwkBinding> bindings = null;
            Vector tagList = ((SwkWidget) component).getTagList();

            for (int j = 0; j < tagList.size(); j++) {
                String tag = (String) tagList.elementAt(j);

                if (tag.startsWith(".")) {
                    bindings = this.bindings;
                } else {
                    bindings = BindCmd.getMouseMotionBindings(tag);
                }

                if (bindings == null) {
                    continue;
                }

                for (i = 0; i < bindings.size(); i++) {
                    binding = bindings.get(i);

                    // System.out.println("binding is "+binding.toString()+" "+binding.subtype+" bmod "+binding.mod+" mod "+mods);
                    if (binding.subtype != SwkBinding.MOTION) {
                        continue;
                    }

                    if (!checkButtonState(e, binding.mod, mods)) {
                        continue;
                    }

                    if (!checkMods(binding.mod, mods)) {
                        continue;
                    }

                    if ((binding.command != null) &&
                            (binding.command.length() != 0)) {
                        try {
                            //   System.out.println("doCmd "+binding.command);
                            processing = true;
                            BindCmd.doCmd(interp, binding.command, e);
                        } catch (TclException tclE) {
                            if (tclE.getCompletionCode() == TCL.BREAK) {
                                e.consume();

                                return;
                            } else {
                                interp.addErrorInfo(
                                    "\n    (\"binding\" script)");
                                interp.backgroundError();
                            }
                        }
                    }
                }
            }
        } finally {
            processing = false;
        }
    }

    public static boolean checkMods(int bmod, int emod) {
        //   System.out.println("buttonstate "+bmod+" "+emod);
        int buttonsOn = InputEvent.BUTTON1_DOWN_MASK |
            InputEvent.BUTTON2_DOWN_MASK | InputEvent.BUTTON3_DOWN_MASK;
        bmod = bmod | buttonsOn;
        emod = emod | buttonsOn;

        return (bmod == (emod & bmod));
    }

    public static boolean checkButtonState(InputEvent e, int bmod, int emod) {
        //  System.out.println("buttonstate "+bmod+" "+emod);
        boolean result = true;
        int buttonsOn = InputEvent.BUTTON1_DOWN_MASK |
            InputEvent.BUTTON2_DOWN_MASK | InputEvent.BUTTON3_DOWN_MASK;
        bmod = bmod & buttonsOn;
        emod = emod & buttonsOn;

        if ((emod != 0) && (e.getID() == MouseEvent.MOUSE_MOVED)) {
            emod = 0;
        }

        return (bmod == emod);
    }

    public static boolean checkButtons(int detail, int button) {
        boolean result = true;

        // System.out.println("buttons "+detail+" "+button);
        if ((detail == InputEvent.BUTTON1_MASK) &&
                (button != MouseEvent.BUTTON1)) {
            result = false;
        } else if ((detail == InputEvent.BUTTON2_MASK) &&
                (button != MouseEvent.BUTTON2)) {
            result = false;
        } else if ((detail == InputEvent.BUTTON3_MASK) &&
                (button != MouseEvent.BUTTON3)) {
            result = false;
        } else {
            result = true;
        }

        // System.out.println(result);
        return result;
    }
}
