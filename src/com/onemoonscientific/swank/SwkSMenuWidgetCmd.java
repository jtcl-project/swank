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


class SwkSMenuWidgetCmd implements Command {
    static final private String[] validCmds = {
        "cget", "configure", "object", "jadd", "add", "delete"
    };
    static final private int OPT_CGET = 0;
    static final private int OPT_CONFIGURE = 1;
    static final private int OPT_OBJECT = 2;
    static final private int OPT_JADD = 3;
    static final private int OPT_ADD = 4;
    static final private int OPT_DELETE = 5;
    static boolean gotDefaults = false;
    Interp interp = null;

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

        final int opt = TclIndex.get(interp, argv[1], validCmds, "option", 0);
        final TclObject tObj = (TclObject) Widgets.theWidgets.get(argv[0].toString());
        this.interp = interp;

        if (tObj == null) {
            throw new TclException(interp,
                "bad window path name \"" + argv[0].toString() + "\"");
        }

        final SwkSMenu swksmenu = (SwkSMenu) ReflectObject.get(interp, tObj);

        switch (opt) {
        case OPT_CGET:

            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            interp.setResult(swksmenu.jget(interp, argv[2]));

            break;

        case OPT_CONFIGURE:

            if (!gotDefaults) {
                swksmenu.setResourceDefaults();
                gotDefaults = true;
            }

            if (argv.length == 2) {
                swksmenu.jgetAll(interp);
            } else if (argv.length == 3) {
                String result = swksmenu.jget(interp, argv[2]);
                ResourceObject ro = (ResourceObject) SwkSMenu.resourceDB.get(argv[2].toString());
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
                swksmenu.configure(interp, argv, 2);
            }

            break;

        case OPT_OBJECT:
            interp.setResult(tObj);

            break;

        case OPT_JADD:

            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            swksmenu.jadd(interp, argv[2]);

            break;

        case OPT_ADD:

            // FIXME addmenu should be on ET
            SwankUtil.addmenu(interp, swksmenu, argv);

            break;

        case OPT_DELETE:
            delete(interp, swksmenu, argv);

            break;

        default:
            throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    void delete(final Interp interp, final SwkSMenu swksmenu,
        final TclObject[] argv) throws TclException {
        if ((argv.length < 3) || (argv.length > 4)) {
            throw new TclNumArgsException(interp, 2, argv, "first ?last");
        }

        TclObject firstArg = argv[2].duplicate();
        TclObject lastArg = null;

        if (argv.length == 4) {
            lastArg = argv[3].duplicate();
        }

        (new Delete()).exec(swksmenu, firstArg, lastArg);
    }

    class Delete extends UpdateOnEventThread {
        SwkSMenu swksmenu = null;
        TclObject firstArg = null;
        TclObject lastArg = null;

        void exec(final SwkSMenu swksmenu, final TclObject firstArg,
            final TclObject lastArg) {
            this.firstArg = firstArg;
            this.lastArg = lastArg;
            this.swksmenu = swksmenu;
            execOnThread();
        }

        public void run() {
            int first = 0;

            try {
                first = swksmenu.getIndex(interp, firstArg, -1);
            } catch (TclException tclE) {
                //FIXME
                return;
            }

            int last = first;

            if (lastArg != null) {
                try {
                    last = swksmenu.getIndex(interp, lastArg, -1);
                } catch (TclException tclE) {
                    //FIXME
                    return;
                }
            }

            if (last < first) {
                int hold;
                hold = last;
                last = first;
                first = hold;
            }

            for (int i = last; i >= first; i--) {
                if ((i >= 0) && (i < swksmenu.getComponentCount())) {
                    swksmenu.remove(i);
                }
            }
        }
    }
}
