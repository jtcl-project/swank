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

public class SwkKeyListener implements KeyListener, SwkListener {

    Interp interp;
    String command = "puts key";
    ArrayList<SwkBinding> bindings;
    Component component;
    boolean consumeNextType = false;

    SwkKeyListener(Interp interp, Component component) {
        this.interp = interp;
        this.component = component;
        bindings = new ArrayList<SwkBinding>();
    }

    public ArrayList<SwkBinding> getBindings() {
        return bindings;
    }

    public void setCommand(String name) {
        command = new String(name);
    }

    public void setBinding(SwkBinding newBinding) {
        SwkBind.setBinding(bindings, newBinding);
    }

    public String getCommand() {
        return (command);
    }

    public void keyPressed(KeyEvent e) {
        processKey(e, SwkBinding.PRESS);
    }

    public void keyReleased(KeyEvent e) {
        processKey(e, SwkBinding.RELEASE);
    }

    public void keyTyped(KeyEvent e) {
        if (!(Character.getType(e.getKeyChar()) == Character.CONTROL)) {
            //      e.consume();
        }

        processKey(e, SwkBinding.TYPE);
    }

    public void processKey(KeyEvent e, int subtype) {
        // should do first pass of processEvent to see if any bindings will fire, if so consume event, otherwise don't
        // then actually processEvent.
        // e.consume();
        //System.out.println("process key "+e.toString());
        if ((subtype == SwkBinding.TYPE) && consumeNextType) {
            e.consume();
            consumeNextType = false;

            return;
        }

        BindEvent bEvent = new BindEvent(interp, (SwkListener) this,
                (EventObject) e, subtype);
        interp.getNotifier().queueEvent(bEvent, TCL.QUEUE_TAIL);

        //bEvent.sync();
        //processEvent(e,subtype);
    }

    public void processEvent(EventObject eventObject, Object obj, int subtype) {
        KeyEvent e = (KeyEvent) eventObject;

        //System.out.println("key event "+e.toString()); 
        SwkBinding binding;
        int buttonMask;
        boolean debug = false;
        int mods = e.getModifiersEx();
        char keyChar = e.getKeyChar();

        if (Character.isISOControl(keyChar)) {
            keyChar = (char) (keyChar + 96);
        }

        int keyCode = e.getKeyCode();
        int i;

        if (debug) {
            if (keyChar == KeyEvent.CHAR_UNDEFINED) {
                //System.out.println(keyCode+" undef "+subtype);
            } else {
                //System.out.println(keyCode+" "+keyChar+" "+subtype);
            }
        }

        if (Character.isISOControl(keyChar)) {
            keyChar = (char) (keyChar + 64);
        }

        if (subtype != SwkBinding.EXIT) {
            component.requestFocus();
        }

        KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);
        ArrayList<SwkBinding> bindings = null;
        Vector tagList = ((SwkWidget) component).getTagList();
        boolean nativeProcessEvent = true;
        boolean breakOut = false;

        if (subtype == SwkBinding.PRESS) {
            consumeNextType = false;
        }

        //System.out.println("taglist "+tagList.size()); 
        for (int j = 0; j < tagList.size(); j++) {
            String tag = (String) tagList.elementAt(j);
            bindings = null;

            //System.out.println("tag "+tag); 
            if (tag.equals(((SwkWidget) component).getName())) {
                bindings = this.bindings;
            } else if (tag.startsWith(".")) {
                try {
                    SwkWidget tagWidget = (SwkWidget) Widgets.get(interp, tag);
                    SwkKeyListener keyListener = tagWidget.getKeyListener();
                    bindings = keyListener.getBindings();
                } catch (Exception exc) {
                    System.out.println(exc.toString());
                }
            } else {
                bindings = BindCmd.getKeyBindings(tag);
            }

            if (bindings == null) {
                continue;
            }

            //System.out.println("bindings "+bindings.size()); 
            for (i = 0; i < bindings.size(); i++) {
                binding = (SwkBinding) bindings.get(i);

                //System.out.println("binding is "+binding.toString()+" "+binding.subtype+" "+subtype);    
                if (binding.subtype != subtype) {
                    continue;
                }

                if (!((binding.subtype == SwkBinding.PRESS)
                        && (binding.detail == 0))) {
                    if (!((binding.subtype == SwkBinding.RELEASE)
                            && (binding.detail == 0))) {
                        if (!((binding.subtype == SwkBinding.TYPE)
                                && (binding.detail == 0))) {
                            //System.out.println("event mods "+mods+" binding mods "+binding.mod);
                            if (binding.keyStroke == null) {
                                //System.out.println("chars "+(keyChar+0)+" "+binding.detail);
                                if (binding.detail != keyChar) {
                                    continue;
                                }

                                if (binding.mod != mods) {
                                    if ((binding.mod
                                            | InputEvent.SHIFT_DOWN_MASK) != mods) {
                                        continue;
                                    }
                                }
                            } else {
                                //System.out.println(binding.detail+" <<>> "+keyCode);
                                if (binding.detail != keyCode) {
                                    //System.out.println("keyCodes not equal");
                                    continue;
                                }

                                if (binding.mod != mods) {
                                    continue;
                                }
                            }
                        }

                        // second accounts for possibility of Caps-lock on
                        // if matched above at detail == keyChar then the case was
                        // right
                    }
                }

                if ((binding.command != null)
                        && (binding.command.length() != 0)) {
                    try {
                        //System.out.println("dokey "+binding.command);
                        BindCmd.doCmd(interp, binding.command, e);
                    } catch (TclException tclE) {
                        if (tclE.getCompletionCode() == TCL.BREAK) {
                            nativeProcessEvent = false;

                            //System.out.println("break");
                            e.consume();

                            if (subtype == SwkBinding.PRESS) {
                                //System.out.println("consume next");
                                consumeNextType = true;
                            }

                            breakOut = true;

                            break;
                        } else {
                            interp.addErrorInfo("\n    (\"binding\" script)");
                            interp.backgroundError();
                        }
                    }
                }
            }

            if (breakOut) {
                break;
            }
        }

        /*
        if (!nativeProcessEvent) {
        //System.out.println("don't processNative");
        }

        if (nativeProcessEvent && (subtype == SwkBinding.TYPE) && (component instanceof SwkJTextPane)) {
        if (!consumeNextType) {
        //System.out.println("processNative");
        ((SwkJTextPane) component).doKeyBinding(keyStroke,e,JComponent.WHEN_FOCUSED,true);
        }
        }
         */
    }
}
