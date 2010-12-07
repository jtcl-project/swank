/*
 * Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file "LICENSE" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 *
 */
package com.onemoonscientific.swank;

import tcl.lang.*;
import tcl.pkg.java.ReflectObject;

import java.awt.*;

import java.io.*;

import java.net.*;

import java.util.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.tree.*;

class SwkJCheckBoxWidgetCmd implements Command {

    static final private String[] validCmds = {
        "cget", "configure", "deselect", "flash", "invoke",
        "select", "toggle"
    };
    static final private int OPT_CGET = 0;
    static final private int OPT_CONFIGURE = 1;
    static final private int OPT_DESELECT = 2;
    static final private int OPT_FLASH = 3;
    static final private int OPT_INVOKE = 4;
    static final private int OPT_SELECT = 5;
    static final private int OPT_TOGGLE = 6;
    static boolean gotDefaults = false;
    int index;

    public static String[] getValidCmds() {
        return validCmds;
    }

    public void cmdProc(Interp interp, TclObject[] argv)
            throws TclException {
        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv,
                    "option ?arg arg ...?");
        }

        int opt = TclIndex.get(interp, argv[1], validCmds, "option", 0);
        TclObject tObj = (TclObject) Widgets.getWidget(interp, argv[0].toString());

        if (tObj == null) {
            throw new TclException(interp,
                    "bad window path name \"" + argv[0].toString() + "\"");
        }

        final SwkJCheckBox swkjcheckbox = (SwkJCheckBox) ReflectObject.get(interp,
                tObj);

        switch (opt) {
            case OPT_CGET:

                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "option");
                }

                interp.setResult(swkjcheckbox.jget(interp, argv[2]));

                break;

            case OPT_CONFIGURE:

                if (!gotDefaults) {
                    swkjcheckbox.setResourceDefaults();
                    gotDefaults = true;
                }

                if (argv.length == 2) {
                    swkjcheckbox.jgetAll(interp);
                } else if (argv.length == 3) {
                    String result = swkjcheckbox.jget(interp, argv[2]);
                    ResourceObject ro = (ResourceObject) SwkJCheckBox.resourceDB.get(argv[2].toString());

                    if (ro == null) {
                        throw new TclException(interp,
                                "unknown option \"" + argv[2].toString() + "\"");
                    }

                    TclObject list = TclList.newInstance();
                    TclList.append(interp, list,
                            TclString.newInstance(argv[2].toString()));
                    TclList.append(interp, list, TclString.newInstance(ro.resource));
                    TclList.append(interp, list, TclString.newInstance(ro.className));
                    TclList.append(interp, list,
                            TclString.newInstance(ro.defaultVal));
                    TclList.append(interp, list, TclString.newInstance(result));
                    interp.setResult(list);
                } else {
                    swkjcheckbox.configure(interp, argv, 2);
                }

                break;

            case OPT_DESELECT:
                deselect(interp, swkjcheckbox, argv);
                swkjcheckbox.commandListener.tclAction();

                break;

            case OPT_FLASH:

                if (argv.length != 2) {
                    throw new TclNumArgsException(interp, 2, argv, "");
                }

                break;

            case OPT_INVOKE:

                if (argv.length != 2) {
                    throw new TclNumArgsException(interp, 2, argv, "");
                }

                if (!swkjcheckbox.isEnabled()) {
                    return;
                }

                if ((swkjcheckbox.commandListener.command != null)
                        && (swkjcheckbox.commandListener.command.length() != 0)) {
                    try {
                        interp.eval(swkjcheckbox.commandListener.command);
                    } catch (TclException tclE) {
                        System.out.println(interp.getResult());
                    }
                }

                swkjcheckbox.setSelected(true);
                swkjcheckbox.commandListener.tclAction();

                break;

            case OPT_SELECT:
                select(interp, swkjcheckbox, argv);
                swkjcheckbox.commandListener.tclAction();

                break;

            case OPT_TOGGLE:

                if (argv.length != 2) {
                    throw new TclNumArgsException(interp, 2, argv, "");
                }

                if (!swkjcheckbox.isEnabled()) {
                    return;
                }

                if (swkjcheckbox.isSelected()) {
                    swkjcheckbox.setSelected(false);
                } else {
                    swkjcheckbox.setSelected(true);
                }

                swkjcheckbox.commandListener.tclAction();

                break;

            default:
                throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    void deselect(final Interp interp, final SwkJCheckBox swkjcheckbox,
            final TclObject[] argv) throws TclException {
        if (argv.length != 2) {
            throw new TclNumArgsException(interp, 2, argv, "");
        }

        (new Select()).exec(swkjcheckbox, true);
    }

    void select(final Interp interp, final SwkJCheckBox swkjcheckbox,
            final TclObject[] argv) throws TclException {
        if (argv.length != 2) {
            throw new TclNumArgsException(interp, 2, argv, "");
        }

        (new Select()).exec(swkjcheckbox, false);
    }

    class Select extends UpdateOnEventThread {

        boolean mode = false;
        SwkJCheckBox swkjcheckbox;

        void exec(SwkJCheckBox swkjcheckbox, final boolean mode) {
            this.mode = mode;
            this.swkjcheckbox = swkjcheckbox;
            execOnThread();
        }

        public void run() {
            if (!swkjcheckbox.isEnabled()) {
                return;
            }

            swkjcheckbox.setSelected(mode);
            swkjcheckbox.doClick();
        }
    }
}
