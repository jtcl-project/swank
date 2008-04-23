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

import javax.swing.*;


/** This class implements the Jacl bind command.
 * @author Bruce A. Johnson
 * @version %I%, %G%
 */
public class BindCmd implements Command {
    // This Hashtable stores class level virtual bindings.
    public static Hashtable virtualTable = new Hashtable();

    // This Hashtable stores class level focus bindings.
    public static Hashtable focusTable = new Hashtable();

    // This Hashtable stores class level configure bindings.
    public static Hashtable configureTable = new Hashtable();

    // This Hashtable stores class level key bindings.
    public static Hashtable keyTable = new Hashtable();

    // This Hashtable stores class level mouse bindings.
    public static Hashtable mouseTable = new Hashtable();
    public static Hashtable stateChangeTable = new Hashtable();
    public static Hashtable selectionChangeTable = new Hashtable();

    // This Hashtable stores class level mousemotion bindings.
    public static Hashtable mouseMotionTable = new Hashtable();

    /** Method called to process the bind command.
     * @param interp The interpreter in which this command is active.
     * @param argv Array of TclObjects containing arguments to the bind command.
     * @throws TclException .
     */
    public void cmdProc(Interp interp, TclObject[] argv)
        throws TclException {
        if (argv.length < 4) {
            throw new TclNumArgsException(interp, 1, argv,
                "option ?arg arg ...?");
        }

        SwkBinding binding = SwkBind.getBinding(interp, argv, 2);

        if (binding == null) {
            return;
        }

        String tag = argv[1].toString();

        if (binding.virtual) {
            Vector sequenceVec = EventCmd.getVirtualEvents(argv[2].toString());

            if (sequenceVec != null) {
                for (int i = 0; i < sequenceVec.size(); i++) {
                    SwkBinding subBinding = (SwkBinding) sequenceVec.elementAt(i);

                    if (subBinding != null) {
                        SwkBind.updateBindingCommand(interp, subBinding, argv, 2);
                        addBinding(interp, subBinding, tag);
                    }
                }
            }
        } else {
            SwkBind.updateBindingCommand(interp, binding, argv, 2);
            addBinding(interp, binding, tag);
        }
    }

    void addBinding(Interp interp, SwkBinding binding, String tag)
        throws TclException {
        SwkWidget swkWidget = null;
        Vector bindingVector = null;
        Hashtable currentTable = null;

        if (tag.charAt(0) != '.') {
            if (binding.type == SwkBinding.FOCUS) {
                currentTable = focusTable;
            } else if (binding.type == SwkBinding.COMPONENT) {
                currentTable = configureTable;
            } else if (binding.type == SwkBinding.KEY) {
                currentTable = keyTable;
            } else if (binding.type == SwkBinding.MOUSE) {
                currentTable = mouseTable;
            } else if (binding.type == SwkBinding.MOUSEMOTION) {
                currentTable = mouseMotionTable;
            } else if (binding.type == SwkBinding.STATECHANGED) {
                currentTable = stateChangeTable;
            } else if (binding.type == SwkBinding.SELECTIONCHANGED) {
                currentTable = selectionChangeTable;
            } else {
                throw new TclException(interp,
                    "invalid binding type \"" + binding.type +
                    "\" in bind cmd");
            }

            bindingVector = (Vector) currentTable.get(tag);

            if (bindingVector == null) {
                bindingVector = new Vector();
                currentTable.put(tag, bindingVector);
            }
        } else {
            TclObject tObj = (TclObject) Widgets.getWidget(interp,tag);

            if (tObj == null) {
                throw new TclException(interp,
                    "bad window path name \"" + tag + "\"");
            }

            swkWidget = (SwkWidget) ReflectObject.get(interp, tObj);

            if (swkWidget == null) {
                throw new TclException(interp,
                    "Can't find widget " + tObj.toString());
            }
        }

        setupBinding(interp, binding, swkWidget, bindingVector);
    }

    public static Vector getMouseBindings(String tag) {
        Vector bindingVector = (Vector) mouseTable.get(tag);

        return bindingVector;
    }

    public static Vector getMouseMotionBindings(String tag) {
        Vector bindingVector = (Vector) mouseMotionTable.get(tag);

        return bindingVector;
    }

    public static Vector getKeyBindings(String tag) {
        Vector bindingVector = (Vector) keyTable.get(tag);

        return bindingVector;
    }

    public static Vector getFocusBindings(String tag) {
        Vector bindingVector = (Vector) focusTable.get(tag);

        return bindingVector;
    }

    public static Vector getVirtualBindings(String tag) {
        Vector bindingVector = (Vector) virtualTable.get(tag);

        return bindingVector;
    }

    public static void addDefaultListeners(Interp interp, SwkWidget swkWidget) {
        if (swkWidget.getFocusListener() == null) {
            SwkFocusListener focusListener = new SwkFocusListener(interp,
                    (Component) swkWidget);
            ((Component) swkWidget).addFocusListener(focusListener);
            swkWidget.setFocusListener(focusListener);
        }

        if (swkWidget.getComponentListener() == null) {
            SwkComponentListener componentListener = new SwkComponentListener(interp,
                    (Component) swkWidget);
            ((Component) swkWidget).addComponentListener(componentListener);
            swkWidget.setComponentListener(componentListener);
        }

        if (swkWidget.getMouseListener() == null) {
            SwkMouseListener mouseListener = new SwkMouseListener(interp,
                    (Component) swkWidget);

            if (swkWidget instanceof SwkJSlider) {
                SwkJSlider swkslider = (SwkJSlider) swkWidget;
                ((Component) swkslider.getSlider()).addMouseListener(mouseListener);
            } else {
                ((Component) swkWidget).addMouseListener(mouseListener);
            }

            swkWidget.setMouseListener(mouseListener);
        }

        if (swkWidget.getMouseMotionListener() == null) {
            SwkMouseMotionListener mouseMotionListener = new SwkMouseMotionListener(interp,
                    (Component) swkWidget);
            ((Component) swkWidget).addMouseMotionListener(mouseMotionListener);
            swkWidget.setMouseListener(mouseMotionListener);
        }

        if (swkWidget instanceof JFrame) {
            //SwkKeyCommandListener keyCommandListener = new SwkKeyCommandListener(interp, binding, (Component) swkWidget);
            //((SwkJFrame) swkWidget).keyCommandListener = keyCommandListener;
            if (swkWidget.getKeyListener() == null) {
                try {
                    SwkKeyListener keyListener = new SwkKeyListener(interp,
                            (Component) swkWidget);
                    ((JFrame) swkWidget).addKeyListener(keyListener);
                    swkWidget.setKeyListener(keyListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (swkWidget instanceof SwkJTextPane) {
            if (swkWidget.getKeyListener() == null) {
                SwkKeyListener keyListener = new SwkKeyListener(interp,
                        (Component) swkWidget);
                ((Component) swkWidget).addKeyListener(keyListener);
                swkWidget.setKeyListener(keyListener);
            }
        } else if (swkWidget instanceof JComponent) {
            if (swkWidget.getKeyListener() == null) {
                SwkKeyListener keyListener = new SwkKeyListener(interp,
                        (Component) swkWidget);
                ((JComponent) swkWidget).addKeyListener(keyListener);
                swkWidget.setKeyListener(keyListener);
            }
        }
    }

    /** Stores bindings and sets up listeners appropriate for binding type.
     *Bindings are stored in the Hashtable of this class if they are at a Class level
     *and stored in the appropriate listener object if they are at the window level.
     * @param interp The interpreter (Interp) in which the bind command is called.
     * @param binding The SwkBinding object which stores the action and command for this binding.
     * @param swkWidget The SwkWidget this binding is applied to.
     *If null then the binding is applied to subsequent widgets in
     *this class.
     * @param bindingVector This Vector stores all the bindings of this type.
     */
    public static void setupBinding(Interp interp, SwkBinding binding,
        SwkWidget swkWidget, Vector bindingVector) {
        if (binding.type == SwkBinding.FOCUS) {
            if (swkWidget == null) {
                setClassBinding(bindingVector,binding);
            } else {
                if (swkWidget.getFocusListener() == null) {
                    SwkFocusListener focusListener = new SwkFocusListener(interp,
                            (Component) swkWidget);
                    ((Component) swkWidget).addFocusListener(focusListener);
                    swkWidget.setFocusListener(focusListener);
                }

                swkWidget.getFocusListener().setBinding(binding);
            }
        } else if (binding.type == SwkBinding.COMPONENT) {
            if (swkWidget == null) {
                setClassBinding(bindingVector,binding);
            } else {
                if (swkWidget.getComponentListener() == null) {
                    SwkComponentListener componentListener = new SwkComponentListener(interp,
                            (Component) swkWidget);
                    ((Component) swkWidget).addComponentListener(componentListener);
                    swkWidget.setComponentListener(componentListener);
                }

                swkWidget.getComponentListener().setBinding(binding);
            }
        } else if (binding.type == SwkBinding.STATECHANGED) {
            if (swkWidget == null) {
                setClassBinding(bindingVector,binding);
            } else {
                if (swkWidget instanceof JTabbedPane) {
                    if (swkWidget.getChangeListener() == null) {
                        SwkChangeListener changeListener = new SwkChangeListener(interp,
                                (Component) swkWidget);
                        ((JTabbedPane) swkWidget).addChangeListener(changeListener);
                        swkWidget.setChangeListener(changeListener);
                    }

                    swkWidget.getChangeListener().setBinding(binding);
                }
            }
        } else if (binding.type == SwkBinding.SELECTIONCHANGED) {
            if (swkWidget == null) {
                setClassBinding(bindingVector,binding);
            } else {
                if (swkWidget instanceof SwkJTable) {
                    SwkJTable swkjtable = (SwkJTable) swkWidget;
                    ListSelectionModel listSelectionModel = swkjtable.getSelectionModel();

                    if (swkjtable.getListSelectionListener() == null) {
                        SwkListSelectionListener selectionListener = new SwkListSelectionListener(interp,
                                (Component) swkWidget);
                        swkjtable.getSelectionModel().addListSelectionListener(selectionListener);
                        swkjtable.setListSelectionListener(selectionListener);
                    }

                    swkjtable.getListSelectionListener().setBinding(binding);
                }
            }
        } else if (binding.type == SwkBinding.MOUSE) {
            if (swkWidget == null) {
                setClassBinding(bindingVector,binding);
            } else {
                if (swkWidget.getMouseListener() == null) {
                    SwkMouseListener mouseListener = new SwkMouseListener(interp,
                            (Component) swkWidget);

                    if (swkWidget instanceof SwkJSlider) {
                        SwkJSlider swkslider = (SwkJSlider) swkWidget;
                        ((Component) swkslider.getSlider()).addMouseListener(mouseListener);
                    } else {
                        ((Component) swkWidget).addMouseListener(mouseListener);
                    }

                    swkWidget.setMouseListener(mouseListener);
                }

                swkWidget.getMouseListener().setBinding(binding);
            }
        } else if (binding.type == SwkBinding.MOUSEMOTION) {
            if (swkWidget == null) {
                setClassBinding(bindingVector,binding);
            } else {
                if (swkWidget.getMouseMotionListener() == null) {
                    SwkMouseMotionListener mouseMotionListener = new SwkMouseMotionListener(interp,
                            (Component) swkWidget);
                    ((Component) swkWidget).addMouseMotionListener(mouseMotionListener);
                    swkWidget.setMouseListener(mouseMotionListener);
                }

                swkWidget.getMouseMotionListener().setBinding(binding);
            }
        } else if (binding.type == SwkBinding.KEY) {
            if (swkWidget == null) {
                setClassBinding(bindingVector,binding);
            } else {
                if (swkWidget instanceof JFrame) {
                    //SwkKeyCommandListener keyCommandListener = new SwkKeyCommandListener(interp, binding, (Component) swkWidget);
                    //((SwkJFrame) swkWidget).keyCommandListener = keyCommandListener;
                    if (swkWidget.getKeyListener() == null) {
                        SwkKeyListener keyListener = new SwkKeyListener(interp,
                                (Component) swkWidget);
                        ((Component) swkWidget).addKeyListener(keyListener);
                        swkWidget.setKeyListener(keyListener);
                    }

                    swkWidget.getKeyListener().setBinding(binding);
                } else if (swkWidget instanceof SwkJTextPane) {
                    if (swkWidget.getKeyListener() == null) {
                        SwkKeyListener keyListener = new SwkKeyListener(interp,
                                (Component) swkWidget);
                        ((Component) swkWidget).addKeyListener(keyListener);
                        swkWidget.setKeyListener(keyListener);
                    }

                    swkWidget.getKeyListener().setBinding(binding);
                } else if (swkWidget instanceof JComponent) {
                    if (swkWidget.getKeyListener() == null) {
                        SwkKeyListener keyListener = new SwkKeyListener(interp,
                                (Component) swkWidget);
                        ((JComponent) swkWidget).addKeyListener(keyListener);
                        swkWidget.setKeyListener(keyListener);
                    }

                    swkWidget.getKeyListener().setBinding(binding);

                    /*
                                    SwkKeyCommandListener keyCommandListener = new SwkKeyCommandListener (interp, binding,(Component) swkWidget);
                                   ((JComponent) swkWidget).registerKeyboardAction(keyCommandListener,binding.keyStroke,JComponent.WHEN_FOCUSED);
                    */
                }
            }
        }
    }

    /** Called when a new widget is created to apply bindings specific to the widgets class.
     * @param interp The interpreter in which the widget was created.
     * @param tObj TclObject containing the new widget.
     * @param className The name of the class of the widget.  Used to lookup bindings for this widgets class.
     * @throws TclException If widget corresponding to tObj can't be found.
     */
    public static void applyBindings(Interp interp, SwkWidget swkWidget,
        String className) throws TclException {
        int i;

        if (swkWidget == null) {
            throw new TclException(interp, "bad window path name ");
        }

        Vector bindingVector = (Vector) BindCmd.mouseTable.get(className);

        if (bindingVector != null) {
            for (i = 0; i < bindingVector.size(); i++) {
                setupBinding(interp, (SwkBinding) bindingVector.elementAt(i),
                    swkWidget, bindingVector);
            }
        }

        bindingVector = (Vector) BindCmd.mouseMotionTable.get(className);

        if (bindingVector != null) {
            for (i = 0; i < bindingVector.size(); i++) {
                setupBinding(interp, (SwkBinding) bindingVector.elementAt(i),
                    swkWidget, bindingVector);
            }
        }

        bindingVector = (Vector) BindCmd.keyTable.get(className);

        if (bindingVector != null) {
            for (i = 0; i < bindingVector.size(); i++) {
                setupBinding(interp, (SwkBinding) bindingVector.elementAt(i),
                    swkWidget, bindingVector);
            }
        }

        bindingVector = (Vector) BindCmd.focusTable.get(className);

        if (bindingVector != null) {
            for (i = 0; i < bindingVector.size(); i++) {
                setupBinding(interp, (SwkBinding) bindingVector.elementAt(i),
                    swkWidget, bindingVector);
            }
        }
    }

    /** Substitutes values into event fields and then executes command.
     * @param interp The interpreter in which command is executing.
     * @param binding The SwkBinding object that describes the binding event and command.
     * @param component The Component in which the event occurred.
     * @param e The Action Event that occured to trigger this binding response.
     * @throws TclException if exception is thrown when evaluating the command.
     */
    public static void doCmd(Interp interp, SwkBinding binding,
        Component component, ComponentEvent e) throws TclException {
        doCmd(interp, binding, component, (ActionEvent) null);
    }

    public static void doCmd(Interp interp, SwkBinding binding,
        Component component, ActionEvent e) throws TclException {
        int i;
        char type;
        final StringBuffer sbuf = new StringBuffer();

        for (i = 0; i < binding.command.length(); i++) {
            if (binding.command.charAt(i) != '%') {
                sbuf.append(binding.command.charAt(i));

                continue;
            } else {
                i++;
                type = binding.command.charAt(i);

                switch (type) {
                case 'x':
                    sbuf.append("0");

                    break;

                case 'y':
                    sbuf.append("0");

                    break;

                case 'W':
                    sbuf.append(component.getName());

                    break;

                case 'K':
                    sbuf.append(binding.name);

                    break;

                case 'w':
                    sbuf.append(component.getWidth());

                    break;

                case 'h':
                    sbuf.append(component.getHeight());

                    break;

                case 's':

                    if (component instanceof JTabbedPane) {
                        sbuf.append(((JTabbedPane) component).getSelectedIndex());
                    }

                    break;
                }
            }
        }

        //BindEvent bEvent = new BindEvent(interp,sbuf.toString());
        //interp.getNotifier().queueEvent(bEvent,TCL.QUEUE_TAIL);
        //interp.eval(sbuf.toString());
        SwkExceptionCmd.doExceptionCmd(interp, sbuf.toString());
    }

    /** Substitutes values into event fields and then executes command.
     * @param interp The interpreter in which command is executing.
     * @param command String containing the command to evaluate.
     * @param e The Input Event that occured to trigger this binding response.
     * @throws TclException if exception is thrown when evaluating the command.
     */
    public static void doCmd(Interp interp, String command, InputEvent e)
        throws TclException {
        int i;
        char type;
        StringBuffer sbuf = new StringBuffer();

        for (i = 0; i < command.length(); i++) {
            int x = 0;
            int y = 0;
            Component comp = null;

            if (command.charAt(i) != '%') {
                sbuf.append(command.charAt(i));

                continue;
            } else {
                i++;
                type = command.charAt(i);

                switch (type) {
                case 'x':
                case 'X':

                    if (e instanceof MouseEvent) {
                        x = ((MouseEvent) e).getX();
                    } else {
                        comp = e.getComponent();

                        if (comp instanceof SwkWidget) {
                            x = ((SwkWidget) comp).getMouseX();
                        }
                    }

                    if (type == 'X') {
                        x += e.getComponent().getLocationOnScreen().getX();
                    }

                    sbuf.append(x);

                    break;

                case 'y':
                case 'Y':

                    if (e instanceof MouseEvent) {
                        y = ((MouseEvent) e).getY();
                    } else {
                        comp = e.getComponent();

                        if (comp instanceof SwkWidget) {
                            y = ((SwkWidget) comp).getMouseY();
                        }
                    }

                    if (type == 'Y') {
                        y += e.getComponent().getLocationOnScreen().getY();
                    }

                    sbuf.append(y);

                    break;

                case 'W':
                    sbuf.append(e.getComponent().getName());

                    break;

                case 'k':
                    sbuf.append(((KeyEvent) e).getKeyCode());

                    break;

                case 'K':

                    if (e instanceof KeyEvent) {
                        char ch = ((KeyEvent) e).getKeyChar();

                        if ((ch != KeyEvent.CHAR_UNDEFINED) &&
                                !Character.isISOControl(ch) &&
                                !Character.isWhitespace(ch)) {
                            sbuf.append(ch);
                        } else {
                            int keyCode = ((KeyEvent) e).getKeyCode();
                            sbuf.append(((KeyEvent) e).getKeyText(keyCode));
                        }
                    }

                    break;

                case 'A':

                    if (e instanceof KeyEvent) {
                        char keyChar = ((KeyEvent) e).getKeyChar();

                        if ((keyChar == '{') || (keyChar == '}')) {
                            sbuf.append("\\" + keyChar);
                        } else if ((keyChar == '\\')) {
                            sbuf.append("\\\\");
                        } else {
                            sbuf.append("{" + keyChar + "}");
                        }
                    }

                    break;
                }
            }
        }

        //BindEvent bEvent = new BindEvent(interp,sbuf.toString());
        //interp.getNotifier().queueEvent(bEvent,TCL.QUEUE_TAIL);
        //interp.eval(sbuf.toString());
        SwkExceptionCmd.doExceptionCmd(interp, sbuf.toString());
    }

    public static void setClassBinding(Vector bindings, SwkBinding newBinding) {
        SwkBinding binding = null;

        if (!newBinding.add) {
            for (int i = 0; i < bindings.size(); i++) {
                binding = (SwkBinding) bindings.elementAt(i);

                if (binding.equals(newBinding)) {
                    if (newBinding.remove) {
                        bindings.removeElementAt(i);
                    } else {
                        bindings.setElementAt(newBinding, i);
                    }
                    return;
                }
            }
        }

        bindings.addElement(newBinding);
    }

    public static void setVirtualBinding(SwkWidget swkWidget,
        SwkBinding newBinding) {
        SwkBinding binding = null;
        Vector bindings = swkWidget.getVirtualBindings();

        if (bindings == null) {
            bindings = new Vector();
            swkWidget.setVirtualBindings(bindings);
        }

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
}
