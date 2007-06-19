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


class SwkJOptionPaneWidgetCmd implements Command {
    static final private String[] validCmds = {
        "cget", "configure", "object", "jadd", "dialog"
    };
    static final private int OPT_CGET = 0;
    static final private int OPT_CONFIGURE = 1;
    static final private int OPT_OBJECT = 2;
    static final private int OPT_JADD = 3;
    static final private int OPT_DIALOG = 4;
    static boolean gotDefaults = false;
    Interp interp = null;
    SwkJOptionPane swkjoptionpane = null;

    public static String[] getValidCmds() {
        return validCmds;
    }

    public void cmdProc(final Interp interp, final TclObject[] argv)
        throws TclException {
        int i;
        this.interp = interp;

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

        swkjoptionpane = (SwkJOptionPane) ReflectObject.get(interp, tObj);

        switch (opt) {
        case OPT_CGET:

            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            interp.setResult(swkjoptionpane.jget(interp, argv[2]));

            break;

        case OPT_CONFIGURE:

            if (!gotDefaults) {
                swkjoptionpane.setResourceDefaults();
                gotDefaults = true;
            }

            if (argv.length == 2) {
                swkjoptionpane.jgetAll(interp);
            } else if (argv.length == 3) {
                String result = swkjoptionpane.jget(interp, argv[2]);
                ResourceObject ro = (ResourceObject) SwkJOptionPane.resourceDB.get(argv[2].toString());
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
                swkjoptionpane.configure(interp, argv, 2);
            }

            break;

        case OPT_OBJECT:
            interp.setResult(tObj);

            break;

        case OPT_JADD:

            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            swkjoptionpane.jadd(interp, argv[2]);

            break;

        case OPT_DIALOG:
            dialog(interp, swkjoptionpane, argv);

            break;

        default:
            throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    void dialog(final Interp interp, final SwkJOptionPane swkjoptionpane,
        final TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "title");
        }

        (new Dialog()).exec(argv[2].toString());
    }

    class Dialog extends GetValueOnEventThread {
        int index = -1;
        String title = "";
        Object result = null;

        void exec(String title) {
            this.title = title;
            execOnThread();
            interp.setResult(result.toString());
        }

        public void run() {
            JDialog dialog = swkjoptionpane.createDialog(null, title);
            dialog.show();
            dialog.pack();
            result = swkjoptionpane.getValue();
        }
    }
}
