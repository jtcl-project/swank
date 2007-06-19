/*
 * TkMessageBox.java
 *
 * Created on December 30, 2005, 9:03 PM
 */
package com.onemoonscientific.swank;

import tcl.lang.*;

import javax.swing.*;


/**
 *
 * @author brucejohnson
 */
public class TkMessageBox implements Command {
    static String[] optionsARI = { "abort", "retry", "ignore" };
    static String[] optionsOK = { "ok" };
    static String[] optionsOC = { "ok", "cancel" };
    static String[] optionsRC = { "retry", "cancel" };
    static String[] optionsYN = { "yes", "no" };
    static String[] optionsYNC = { "yes", "no", "cancel" };
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
        String parent = null;
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
                parent = argv[i + 1].toString();
            } else if (option.equals("-choices")) {
                choices = TclList.getElements(interp, argv[i + 1]);
            } else if (option.equals("-title")) {
                title = argv[i + 1].toString();
            } else if (option.equals("-type")) {
                type = argv[i + 1].toString();
            } else {
                throw new TclException(interp,
                    "bad option \"" + option +
                    "\": must be -choices, -default, -icon, -message, -parent, -title, -type");
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

                if ((defaultValue == "") && (options.length > 0)) {
                    defaultValue = options[0];
                }
            } else {
                options = null;
            }
        } else {
            throw new TclException(interp, "invalid value for type");
        }

        (new Option()).exec(title, message, messageType, options, optionMode,
            defaultValue);
    }

    class Option extends GetValueOnEventThread {
        int index = -1;
        String title = "";
        String message = "";
        int messageType = 0;
        String[] options = null;
        String defaultOption = "";
        String strResult = "";
        int result = -1;
        boolean optionMode = false;

        void exec(String title, String message, int messageType,
            String[] options, boolean optionMode, String defaultOption) {
            this.title = title;
            this.message = message;
            this.messageType = messageType;
            this.options = options;
            this.defaultOption = defaultOption;
            this.optionMode = optionMode;
            execOnThread();

            if (optionMode) {
                interp.setResult(options[result]);
            } else {
                interp.setResult(strResult);
            }
        }

        public void run() {
            if (optionMode) {
                result = JOptionPane.showOptionDialog(null, message, title, 0,
                        messageType, null, options, defaultOption);
            } else {
                strResult = (String) JOptionPane.showInputDialog(null, message,
                        title, messageType, null, (Object[]) options,
                        (Object) defaultOption);
            }
        }
    }
}
