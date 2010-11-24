/*
 * ColorPicker.java
 *
 * Created on December 30, 2005, 9:03 PM
 */
package com.onemoonscientific.swank;

import tcl.lang.*;
import tcl.pkg.java.ReflectObject;


import java.awt.Component;
import java.awt.Color;
import java.awt.Container;
import java.awt.Window;
import java.lang.reflect.Method;

/**
 *
 * @author brucejohnson
 */
public class SwkColorPicker implements Command {

    Interp interp = null;

    public void cmdProc(final Interp interp, final TclObject[] argv)
        throws TclException {
        int i;
        this.interp = interp;

        option(interp, argv);

        return;
    }

    void option(final Interp interp, final TclObject[] argv)
        throws TclException {
        String[] options = null;

        if (((argv.length - 1) % 2) != 0) {
            throw new TclException(interp,
                "tk_messageBox:  number of args must be a multiple of 2");
        }

        String title = "Choose Color";
        String icon = "info";
        String type = "ok";
        String defaultValue = "";
        String dialogParent = "";
        TclObject[] choices = null;
        Color initialColor = Color.BLACK;
        boolean showOpacity = true;

        for (int i = 1; i < argv.length; i += 2) {
            String option = argv[i].toString();

            if (option.equals("-initialcolor")) {
                initialColor = SwankUtil.getColor(interp, argv[i + 1]);
            } else if (option.equals("-parent")) {
                dialogParent = argv[i + 1].toString();
            } else if (option.equals("-title")) {
                title = argv[i + 1].toString();
            } else if (option.equals("-showopacity")) {
                showOpacity = TclBoolean.get(interp, argv[i + 1]);
            } else {
                throw new TclException(interp,
                    "bad option \"" + option
                    + "\": must be -initialcolor, -parent, -showopacity, -title");
            }
        }

        Component dParent = null;
        if ((dialogParent != null) && (dialogParent.length() != 0)) {
            TclObject tObj = (TclObject) Widgets.getWidget(interp, dialogParent);
            if (tObj == null) {
                throw new TclException(interp, "bad window path name \"" + dialogParent + "\"");
            }
            dParent = (Component) ReflectObject.get(interp, tObj);
        } else {
            String focusWindow = FocusCmd.getFocusWindow();
            if ((focusWindow != null) && (focusWindow.length() != 0)) {
                TclObject tObj = (TclObject) Widgets.getWidget(interp, focusWindow);
                if (tObj != null) {
                    dParent = (Component) ReflectObject.get(interp, tObj);
                }
            }
            if (dParent == null) {
                TclObject tObj = (TclObject) Widgets.getWidget(interp, ".");
                if (tObj != null) {
                    dParent = (Component) ReflectObject.get(interp, tObj);
                }
            }
        }
        Container container = Widgets.getContainer(dParent);
        Component frameOrWindow = Widgets.getFrameOrWindow(container);
        (new Option()).exec(title, initialColor, showOpacity, frameOrWindow);
    }

    class Option extends GetValueOnEventThread {

        int index = -1;
        String title = "";
        String[] options = null;
        String defaultOption = "";
        String strResult = "";
        int result = -1;
        boolean showOpacity = false;
        Color color;
        Component dParent = null;
        Color initialColor = Color.BLACK;

        void exec(String title, final Color initialColor, boolean showOpacity, Component dParent) {
            this.title = title;
            this.initialColor = initialColor;
            this.showOpacity = showOpacity;
            this.dParent = dParent;
            execOnThread();
            if (color != null) {
                interp.setResult(SwankUtil.parseColor(color));
            }
        }

        public void run() {

            Class cl = null;
            try {
                cl = Class.forName("com.bric.swing.ColorPicker");
            } catch (ClassNotFoundException cnfE) {
                return;
            }

            if (cl != null) {
                Method method = null;
                Class[] classArgs = new Class[4];
                classArgs[0] = Window.class;
                classArgs[1] = String.class;
                classArgs[2] = Color.class;
                classArgs[3] = boolean.class;

                try {
                    method = cl.getMethod("showDialog", classArgs);
                } catch (NoSuchMethodException nsmE) {
                }
                try {
                    if (method != null) {
                        color = (Color) method.invoke(cl.newInstance(),(Window) dParent, title, initialColor, showOpacity);
                    }


                    // color = ColorPicker.showDialog((Window) dParent, title, initialColor, showOpacity);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

        }
    }
}
