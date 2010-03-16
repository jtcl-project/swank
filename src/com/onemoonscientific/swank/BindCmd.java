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

import com.onemoonscientific.swank.canvas.SwkShape;
import com.onemoonscientific.swank.canvas.HitShape;
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

    // This Hashtable stores class level activation bindings.
    public static Hashtable activationTable = new Hashtable();

    // This Hashtable stores class level key bindings.
    public static Hashtable keyTable = new Hashtable();

    // This Hashtable stores class level mouse bindings.
    public static Hashtable mouseTable = new Hashtable();
    public static Hashtable stateChangeTable = new Hashtable();
    public static Hashtable selectionChangeTable = new Hashtable();
    public static Hashtable appChangeTable = new Hashtable();

    // This Hashtable stores class level mousemotion bindings.
    public static Hashtable mouseMotionTable = new Hashtable();
    public static  SwkAppListener swkAppListener = null;

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
        ArrayList<SwkBinding> bindingVector = null;
        Hashtable currentTable = null;

        if (tag.charAt(0) != '.') {
            if (binding.type == SwkBinding.FOCUS) {
                currentTable = focusTable;
            } else if (binding.type == SwkBinding.COMPONENT) {
                currentTable = configureTable;
            } else if (binding.type == SwkBinding.ACTIVATION) {
                currentTable = activationTable;
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
            } else if (binding.type == SwkBinding.APP) {
                currentTable = appChangeTable;
            } else {
                throw new TclException(interp,
                    "invalid binding type \"" + binding.type +
                    "\" in bind cmd");
            }

            bindingVector = (ArrayList<SwkBinding>) currentTable.get(tag);

            if (bindingVector == null) {
                bindingVector = new ArrayList<SwkBinding>();
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

    public static ArrayList<SwkBinding> getMouseBindings(String tag) {
        ArrayList<SwkBinding> bindingVector = (ArrayList<SwkBinding>) mouseTable.get(tag);

        return bindingVector;
    }

    public static ArrayList<SwkBinding> getMouseMotionBindings(String tag) {
        ArrayList<SwkBinding> bindingVector = (ArrayList<SwkBinding>) mouseMotionTable.get(tag);

        return bindingVector;
    }

    public static ArrayList<SwkBinding> getKeyBindings(String tag) {
        ArrayList<SwkBinding> bindingVector = (ArrayList<SwkBinding>) keyTable.get(tag);

        return bindingVector;
    }

    public static ArrayList<SwkBinding> getFocusBindings(String tag) {
        ArrayList<SwkBinding> bindingVector = (ArrayList<SwkBinding>) focusTable.get(tag);

        return bindingVector;
    }
    public static ArrayList<SwkBinding> getActivationBindings(String tag) {
        ArrayList<SwkBinding> bindingVector = (ArrayList<SwkBinding>) activationTable.get(tag);

        return bindingVector;
    }

    public static ArrayList<SwkBinding> getVirtualBindings(String tag) {
        ArrayList<SwkBinding> bindingVector = (ArrayList<SwkBinding>) virtualTable.get(tag);

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
// fix me, Java only uses activation on toplevel windows, but Tk/Swank should support propagating activation event to 
// all contained widgets
        if (swkWidget instanceof SwkJFrame) {
            if (((SwkJFrame) swkWidget).getWindowListener() == null) {
                SwkWindowListener windowListener = new SwkWindowListener(interp,
                    (SwkJFrame) swkWidget);
                ((SwkJFrame) swkWidget).addWindowStateListener(windowListener);
                ((SwkJFrame) swkWidget).addWindowListener(windowListener);
                ((SwkJFrame) swkWidget).setWindowListener(windowListener);
            }
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
        SwkWidget swkWidget, ArrayList<SwkBinding> bindingVector) {
        if (binding.type == SwkBinding.APP) {
            if (swkAppListener == null) {
                KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
                swkAppListener = new SwkAppListener(interp);
                focusManager.addPropertyChangeListener(swkAppListener);
            }
            swkAppListener.setBinding(binding);
        } else if (binding.type == SwkBinding.FOCUS) {
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
       } else if (binding.type == SwkBinding.ACTIVATION) {
            if (swkWidget == null) {
                setClassBinding(bindingVector,binding);
            } else {
                if (swkWidget instanceof SwkJFrame) {
                     if (((SwkJFrame) swkWidget).getWindowListener() == null) {
                         SwkWindowListener windowListener = new SwkWindowListener(interp,
                             (SwkJFrame) swkWidget);
                         ((SwkJFrame) swkWidget).addWindowStateListener(windowListener);
                         ((SwkJFrame) swkWidget).addWindowListener(windowListener);
                         ((SwkJFrame) swkWidget).setWindowListener(windowListener);
                     }
                     ((SwkJFrame) swkWidget).getWindowListener().setBinding(binding);
                 }
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
                if (swkWidget instanceof SwkListListener) {
                    SwkListListener swkListListener = (SwkListListener) swkWidget;
                    if (swkListListener.getListSelectionListener() == null) {
                        SwkListSelectionListener selectionListener = new SwkListSelectionListener(interp,
                                (Component) swkWidget);
                        swkListListener.getSelectionModel().addListSelectionListener(selectionListener);
                        swkListListener.setListSelectionListener(selectionListener);
                    }

                    swkListListener.getListSelectionListener().setBinding(binding);
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

        ArrayList<SwkBinding> bindingVector = (ArrayList<SwkBinding>) BindCmd.mouseTable.get(className);

        if (bindingVector != null) {
            for (i = 0; i < bindingVector.size(); i++) {
                setupBinding(interp, (SwkBinding) bindingVector.get(i),
                    swkWidget, bindingVector);
            }
        }

        bindingVector = (ArrayList<SwkBinding>) BindCmd.mouseMotionTable.get(className);

        if (bindingVector != null) {
            for (i = 0; i < bindingVector.size(); i++) {
                setupBinding(interp, (SwkBinding) bindingVector.get(i),
                    swkWidget, bindingVector);
            }
        }

        bindingVector = (ArrayList<SwkBinding>) BindCmd.keyTable.get(className);

        if (bindingVector != null) {
            for (i = 0; i < bindingVector.size(); i++) {
                setupBinding(interp, (SwkBinding) bindingVector.get(i),
                    swkWidget, bindingVector);
            }
        }

        bindingVector = (ArrayList<SwkBinding>) BindCmd.focusTable.get(className);

        if (bindingVector != null) {
            for (i = 0; i < bindingVector.size(); i++) {
                setupBinding(interp, (SwkBinding) bindingVector.get(i),
                    swkWidget, bindingVector);
            }
        }
       bindingVector = (ArrayList<SwkBinding>) BindCmd.activationTable.get(className);

        if (bindingVector != null) {
            for (i = 0; i < bindingVector.size(); i++) {

                setupBinding(interp, (SwkBinding) bindingVector.get(i),
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
        doCmd(interp, command, e,null);

    }

    /** Substitutes values into event fields and then executes command.
     * @param interp The interpreter in which command is executing.
     * @param command String containing the command to evaluate.
     * @param e The Input Event that occured to trigger this binding response.
     * @throws TclException if exception is thrown when evaluating the command.
     */
    public static void doCmd(Interp interp, String command, InputEvent e, HitShape hitShape)
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
                  case 'b':
                    if (e instanceof MouseEvent) {
                        MouseEvent mE = (MouseEvent) e;
                        switch (mE.getButton()) {
                            case MouseEvent.BUTTON1:
                            sbuf.append('1');
                            break;
                            case MouseEvent.BUTTON2:
                            sbuf.append('2');
                            break;
                            case MouseEvent.BUTTON3:
                            sbuf.append('3');
                            break;
                            default:
                                sbuf.append('0');
                        }
                    } else {
                        sbuf.append("??");
                    }
                    break;
                case 'd':
                    if ((hitShape != null) && (hitShape.getShape() != null)) {
                            sbuf.append(hitShape.getShape().getId());
                            if (hitShape.getHandle() >= 0) {
                                sbuf.append('.');
                                sbuf.append(hitShape.getHandle());
                            }
                    } else {
                        sbuf.append("??");
                    }
                    break;
                case 't':
                    sbuf.append(System.currentTimeMillis());
                    break;
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
                    if (e instanceof KeyEvent) {
                        sbuf.append(((KeyEvent) e).getKeyCode());
                    } else {
                        sbuf.append("??");
                    }

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
                    default:sbuf.append("??");
                }
            }
        }

        //BindEvent bEvent = new BindEvent(interp,sbuf.toString());
        //interp.getNotifier().queueEvent(bEvent,TCL.QUEUE_TAIL);
        //interp.eval(sbuf.toString());
        SwkExceptionCmd.doExceptionCmd(interp, sbuf.toString());
    }

    public static void setClassBinding(ArrayList<SwkBinding> bindings, SwkBinding newBinding) {
        SwkBinding binding = null;

        if (!newBinding.add) {
            for (int i = 0; i < bindings.size(); i++) {
                binding = (SwkBinding) bindings.get(i);

                if (binding.equals(newBinding)) {
                    if (newBinding.remove) {
                        bindings.remove(i);
                    } else {
                        bindings.add(i,newBinding);
                    }
                    return;
                }
            }
        }

        bindings.add(newBinding);
    }

    public static void setVirtualBinding(SwkWidget swkWidget,
        SwkBinding newBinding) {
        SwkBinding binding = null;
        ArrayList<SwkBinding> bindings = swkWidget.getVirtualBindings();

        if (bindings == null) {
            bindings = new ArrayList<SwkBinding>();
            swkWidget.setVirtualBindings(bindings);
        }

        if (!newBinding.add) {
            for (int i = 0; i < bindings.size(); i++) {
                binding = (SwkBinding) bindings.get(i);

                if (binding.equals(newBinding)) {
                    bindings.add(i,newBinding);

                    return;
                }
            }
        }

        bindings.add(newBinding);
    }
}
