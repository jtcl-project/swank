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

import java.lang.*;

import java.net.*;

import java.util.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.tree.*;


class SwkJPopupMenuWidgetCmd implements Command {
    static final private String[] validCmds = {
        "cget", "configure", "delete"
    };
    static final private int OPT_CGET = 0;
    static final private int OPT_CONFIGURE = 1;
    static final private int OPT_DELETE = 4;
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

        this.interp = interp;

        int opt = TclIndex.get(interp, argv[1], validCmds, "option", 0);
        TclObject tObj = (TclObject) Widgets.getWidget(interp,argv[0].toString());

        if (tObj == null) {
            throw new TclException(interp,
                "bad window path name \"" + argv[0].toString() + "\"");
        }

        SwkJPopupMenu swkjpopupmenu = (SwkJPopupMenu) ReflectObject.get(interp,
                tObj);

        switch (opt) {
        case OPT_CGET:

            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            interp.setResult(swkjpopupmenu.jget(interp, argv[2]));

            break;

        case OPT_CONFIGURE:

            if (!gotDefaults) {
                swkjpopupmenu.setResourceDefaults();
                gotDefaults = true;
            }

            if (argv.length == 2) {
                swkjpopupmenu.jgetAll(interp);
            } else if (argv.length == 3) {
                String result = swkjpopupmenu.jget(interp, argv[2]);
                ResourceObject ro = (ResourceObject) SwkJPopupMenu.resourceDB.get(argv[2].toString());

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
                swkjpopupmenu.configure(interp, argv, 2);
            }

            break;

        case OPT_DELETE:
            delete(interp, swkjpopupmenu, argv);

            break;

        default:
            throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    void delete(final Interp interp, final SwkJPopupMenu swkjpopupmenu,
        final TclObject[] argv) throws TclException {
        if ((argv.length < 3) || (argv.length > 4)) {
            throw new TclNumArgsException(interp, 2, argv, "first ?last");
        }

        TclObject firstArg = argv[2].duplicate();
        TclObject lastArg = null;

        if (argv.length == 4) {
            lastArg = argv[3].duplicate();
        }

        (new Delete()).exec(swkjpopupmenu, firstArg, lastArg);
    }

    class Delete extends UpdateOnEventThread {
        SwkJPopupMenu swkjpopupmenu = null;
        TclObject firstArg = null;
        TclObject lastArg = null;
        String sIndex = null;
        String sIndexLast = null;
        int first = 0;
        int last = 0;

        void exec(final SwkJPopupMenu swkjpopupmenu, final TclObject firstArg,
            final TclObject lastArg) {
            this.firstArg = firstArg;
            this.lastArg = lastArg;
            this.swkjpopupmenu = swkjpopupmenu;

            try {
                first = TclInteger.get(interp, firstArg);
            } catch (TclException tclE) {
                sIndex = firstArg.toString();
            }

            if (lastArg != null) {
                try {
                    last = TclInteger.get(interp, lastArg);
                } catch (TclException tclE) {
                    sIndexLast = lastArg.toString();
                }
            }

            execOnThread();
        }

        public void run() {
            if (sIndex != null) {
                try {
                    first = swkjpopupmenu.getIndex(sIndex, -1);
                } catch (TclException Tce) {
                }
            }

            if (lastArg == null) {
                last = first;
            } else {
                if (sIndexLast != null) {
                    try {
                        last = swkjpopupmenu.getIndex(sIndexLast, -1);
                    } catch (TclException Tce) {
                    }
                }
            }

            if (last < first) {
                int hold;
                hold = last;
                last = first;
                first = hold;
            }

            for (int i = last; i >= first; i--) {
                if ((i >= 0) && (i < swkjpopupmenu.getComponentCount())) {
                    swkjpopupmenu.remove(i);
                }
            }
        }
    }
}
