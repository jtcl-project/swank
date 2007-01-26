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


class SwkJCheckBoxMenuItemWidgetCmd implements Command {
    static final private String[] validCmds = {
        "cget", "configure", "object", "jadd", "deselect", "flash", "invoke",
        "select"
    };
    static final private int OPT_CGET = 0;
    static final private int OPT_CONFIGURE = 1;
    static final private int OPT_OBJECT = 2;
    static final private int OPT_JADD = 3;
    static final private int OPT_DESELECT = 4;
    static final private int OPT_FLASH = 5;
    static final private int OPT_INVOKE = 6;
    static final private int OPT_SELECT = 7;
    static boolean gotDefaults = false;
    int index;
    public static String[] getValidCmds() {
         return validCmds;
    }
    public void cmdProc(Interp interp, TclObject[] argv)
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

        final SwkJCheckBoxMenuItem swkjcheckboxmenuitem = (SwkJCheckBoxMenuItem) ReflectObject.get(interp,
                tObj);

        switch (opt) {
        case OPT_CGET:

            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            interp.setResult(swkjcheckboxmenuitem.jget(interp, argv[2]));

            break;

        case OPT_CONFIGURE:

            if (!gotDefaults) {
                swkjcheckboxmenuitem.setResourceDefaults();
                gotDefaults = true;
            }

            if (argv.length == 2) {
                swkjcheckboxmenuitem.jgetAll(interp);
            } else if (argv.length == 3) {
                String result = swkjcheckboxmenuitem.jget(interp, argv[2]);
                ResourceObject ro = (ResourceObject) SwkJCheckBoxMenuItem.resourceDB.get(argv[2].toString());
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
                swkjcheckboxmenuitem.configure(interp, argv, 2);
            }

            break;

        case OPT_OBJECT:
            interp.setResult(tObj);

            break;

        case OPT_JADD:

            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            swkjcheckboxmenuitem.jadd(interp, argv[2]);

            break;

        case OPT_DESELECT:
            deselect(interp, swkjcheckboxmenuitem, argv);

            break;

        case OPT_FLASH:
            break;

        case OPT_INVOKE:

            if (argv.length != 2) {
                throw new TclNumArgsException(interp, 2, argv, "");
            }

            if (!swkjcheckboxmenuitem.isEnabled()) {
                return;
            }

            if ((swkjcheckboxmenuitem.commandListener.command != null) &&
                    (swkjcheckboxmenuitem.commandListener.command.length() != 0)) {
                try {
                    interp.eval(swkjcheckboxmenuitem.commandListener.command);
                } catch (TclException tclE) {
                    System.out.println(interp.getResult());
                }
            }

        case OPT_SELECT:
            select(interp, swkjcheckboxmenuitem, argv);

            break;

        default:
            throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    void deselect(final Interp interp,
        final SwkJCheckBoxMenuItem swkjcheckboxmenuitem, final TclObject[] argv)
        throws TclException {
        if (argv.length != 2) {
            throw new TclNumArgsException(interp, 2, argv, "");
        }

        (new Select()).exec(swkjcheckboxmenuitem, true);
    }

    void select(final Interp interp,
        final SwkJCheckBoxMenuItem swkjcheckboxmenuitem, final TclObject[] argv)
        throws TclException {
        if (argv.length != 2) {
            throw new TclNumArgsException(interp, 2, argv, "");
        }

        (new Select()).exec(swkjcheckboxmenuitem, false);
    }

    class Select extends UpdateOnEventThread {
        boolean mode = false;
        SwkJCheckBoxMenuItem swkjcheckboxmenuitem;

        void exec(SwkJCheckBoxMenuItem swkjcheckboxmenuitem, final boolean mode) {
            this.mode = mode;
            this.swkjcheckboxmenuitem = swkjcheckboxmenuitem;
            execOnThread();
        }

        public void run() {
            if (!swkjcheckboxmenuitem.isEnabled()) {
                return;
            }

            swkjcheckboxmenuitem.setSelected(mode);
            swkjcheckboxmenuitem.doClick();
        }
    }
}
