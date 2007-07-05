/*
 * Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file "LICENSE" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 *
 */
package com.onemoonscientific.swank;

import tcl.lang.*;

import java.awt.*;

import java.io.*;

import java.lang.*;

import java.net.*;

import java.util.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.tree.*;


class SwkJRadioButtonMenuItemWidgetCmd implements Command {
    static final private String[] validCmds = {
        "cget", "configure", "deselect", "flash", "invoke",
        "select"
    };
    static final private int OPT_CGET = 0;
    static final private int OPT_CONFIGURE = 1;
    static final private int OPT_DESELECT = 2;
    static final private int OPT_FLASH = 3;
    static final private int OPT_INVOKE = 4;
    static final private int OPT_SELECT = 5;
    static boolean gotDefaults = false;
    int index;

    public static String[] getValidCmds() {
        return validCmds;
    }

    public void cmdProc(final Interp interp, final TclObject[] argv)
        throws TclException {
        int i;

        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv,
                "option ?arg arg ...?");
        }

        int opt = TclIndex.get(interp, argv[1], validCmds, "option", 0);
        TclObject tObj = (TclObject) Widgets.theWidgets.get(argv[0].toString());

        if (tObj == null) {
            throw new TclException(interp,
                "bad window path name \"" + argv[0].toString() + "\"");
        }

        final SwkJRadioButtonMenuItem swkjradiobuttonmenuitem = (SwkJRadioButtonMenuItem) ReflectObject.get(interp,
                tObj);

        switch (opt) {
        case OPT_CGET:

            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            interp.setResult(swkjradiobuttonmenuitem.jget(interp, argv[2]));

            break;

        case OPT_CONFIGURE:

            if (!gotDefaults) {
                swkjradiobuttonmenuitem.setResourceDefaults();
                gotDefaults = true;
            }

            if (argv.length == 2) {
                swkjradiobuttonmenuitem.jgetAll(interp);
            } else if (argv.length == 3) {
                String result = swkjradiobuttonmenuitem.jget(interp, argv[2]);
                ResourceObject ro = (ResourceObject) SwkJRadioButtonMenuItem.resourceDB.get(argv[2].toString());

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
                swkjradiobuttonmenuitem.configure(interp, argv, 2);
            }

            break;

        case OPT_DESELECT:
            deselect(interp, swkjradiobuttonmenuitem, argv);

            break;

        case OPT_FLASH:
            break;

        case OPT_INVOKE:

            if (argv.length != 2) {
                throw new TclNumArgsException(interp, 2, argv, "");
            }

            if (!swkjradiobuttonmenuitem.isEnabled()) {
                return;
            }

            if ((swkjradiobuttonmenuitem.commandListener.command != null) &&
                    (swkjradiobuttonmenuitem.commandListener.command.length() != 0)) {
                try {
                    interp.eval(swkjradiobuttonmenuitem.commandListener.command);
                } catch (TclException tclE) {
                    System.out.println(interp.getResult());
                }
            }

            break;

        case OPT_SELECT:
            select(interp, swkjradiobuttonmenuitem, argv);

            break;

        default:
            throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    void deselect(final Interp interp,
        final SwkJRadioButtonMenuItem swkjradiobuttonmenuitem,
        final TclObject[] argv) throws TclException {
        if (argv.length != 2) {
            throw new TclNumArgsException(interp, 2, argv, "");
        }

        (new Select()).exec(swkjradiobuttonmenuitem, true);
    }

    void select(final Interp interp,
        final SwkJRadioButtonMenuItem swkjradiobuttonmenuitem,
        final TclObject[] argv) throws TclException {
        if (argv.length != 2) {
            throw new TclNumArgsException(interp, 2, argv, "");
        }

        (new Select()).exec(swkjradiobuttonmenuitem, false);
    }

    class Select extends UpdateOnEventThread {
        boolean mode = false;
        SwkJRadioButtonMenuItem swkjradiobuttonmenuitem;

        void exec(SwkJRadioButtonMenuItem swkjradiobuttonmenuitem,
            final boolean mode) {
            this.mode = mode;
            this.swkjradiobuttonmenuitem = swkjradiobuttonmenuitem;
            execOnThread();
        }

        public void run() {
            if (!swkjradiobuttonmenuitem.isEnabled()) {
                return;
            }

            swkjradiobuttonmenuitem.setSelected(mode);
            swkjradiobuttonmenuitem.doClick();
        }
    }
}
