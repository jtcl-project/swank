/*
 * TkMessageBox.java
 *
 * Created on December 30, 2005, 9:03 PM
 */
package com.onemoonscientific.swank;

import tcl.lang.*;
import tcl.pkg.java.ReflectObject;


import java.awt.Component;
import javax.swing.*;

/**
 *
 * @author brucejohnson
 */
public class TkMessageBox implements Command {

    static String[] optionsARI = {"abort", "retry", "ignore"};
    static String[] optionsOK = {"ok"};
    static String[] optionsOC = {"ok", "cancel"};
    static String[] optionsRC = {"retry", "cancel"};
    static String[] optionsYN = {"yes", "no"};
    static String[] optionsYNC = {"yes", "no", "cancel"};
    Interp interp = null;

    /**
     *
     * @param interp
     * @param argv
     * @throws TclException
     */
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

        if (argv.length == 1) {
            throw new TclNumArgsException(interp, 1, argv, "arg ?arg ...?");
        }

        if (((argv.length - 1) % 2) != 0) {
            throw new TclException(interp,
                    "tk_messageBox:  number of args must be a multiple of 2");
        }

        String title = "Message Box";
        String icon = "info";
        String message = "";
        String type = "ok";
        String defaultValue = "";
        String dialogParent = "";
        TclObject[] choices = null;

        for (int i = 1; i < argv.length; i += 2) {
            String option = argv[i].toString();

            if (option.equals("-default")) {
                defaultValue = argv[i + 1].toString();
            } else if (option.equals("-icon")) {
                icon = argv[i + 1].toString();
            } else if (option.equals("-message")) {
                message = argv[i + 1].toString();
            } else if (option.equals("-parent")) {
                dialogParent = argv[i + 1].toString();
            } else if (option.equals("-choices")) {
                choices = TclList.getElements(interp, argv[i + 1]);
            } else if (option.equals("-title")) {
                title = argv[i + 1].toString();
            } else if (option.equals("-type")) {
                type = argv[i + 1].toString();
            } else {
                throw new TclException(interp,
                        "bad option \"" + option
                        + "\": must be -choices, -default, -icon, -message, -parent, -title, -type");
            }
        }

        int messageType = 0;
        boolean optionMode = true;

        if ("error".equals(icon)) {
            messageType = javax.swing.JOptionPane.ERROR_MESSAGE;
        } else if ("info".equals(icon)) {
            messageType = javax.swing.JOptionPane.INFORMATION_MESSAGE;
        } else if ("warning".equals(icon)) {
            messageType = javax.swing.JOptionPane.WARNING_MESSAGE;
        } else if ("question".equals(icon)) {
            messageType = javax.swing.JOptionPane.QUESTION_MESSAGE;
        } else {
            throw new TclException(interp, "invalid type for icon");
        }

        if ("abortretryignore".equals(type)) {
            options = optionsARI;
        } else if ("ok".equals(type)) {
            options = optionsOK;
        } else if ("okcancel".equals(type)) {
            options = optionsOC;
        } else if ("retrycancel".equals(type)) {
            options = optionsRC;
        } else if ("yesno".equals(type)) {
            options = optionsYN;
        } else if ("yesnocancel".equals(type)) {
            options = optionsYNC;
        } else if ("input".equals(type)) {
            optionMode = false;

            if (choices != null) {
                options = new String[choices.length];

                for (int j = 0; j < choices.length; j++) {
                    options[j] = choices[j].toString();
                }

                if ((defaultValue.equals("")) && (options.length > 0)) {
                    defaultValue = options[0];
                }
            } else {
                options = null;
            }
        } else {
            throw new TclException(interp, "invalid value for type");
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

        (new Option()).exec(title, message, messageType, options, optionMode,
                defaultValue, dParent);
    }

    private class Option extends GetValueOnEventThread {

        String title = "";
        String message = "";
        int messageType = 0;
        String[] options = null;
        String defaultOption = "";
        String strResult = "";
        int result = -1;
        boolean optionMode = false;
        Component dParent = null;

        void exec(String title, String message, int messageType,
                String[] options, boolean optionMode, String defaultOption, Component dParent) {
            this.title = title;
            this.message = message;
            this.messageType = messageType;
            this.options = options;
            this.defaultOption = defaultOption;
            this.optionMode = optionMode;
            this.dParent = dParent;
            execOnThread();

            if (optionMode) {
                if ((result < 0) || (result >= options.length)) {
                    interp.resetResult();
                } else {
                    interp.setResult(options[result]);
                }
            } else {
                interp.setResult(strResult);
            }
        }

        @Override
        public void run() {
            if (optionMode) {
                result = JOptionPane.showOptionDialog(dParent, message, title, 0,
                        messageType, null, options, defaultOption);
            } else {
                strResult = (String) JOptionPane.showInputDialog(dParent, message,
                        title, messageType, null, (Object[]) options,
                        (Object) defaultOption);
            }
        }
    }
}
