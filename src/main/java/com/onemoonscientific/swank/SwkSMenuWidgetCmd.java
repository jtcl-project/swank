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

class SwkSMenuWidgetCmd implements Command {

    static final private String[] validCmds = {
        "cget", "configure", "add", "delete"
    };
    static final private int OPT_CGET = 0;
    static final private int OPT_CONFIGURE = 1;
    static final private int OPT_ADD = 2;
    static final private int OPT_DELETE = 3;
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
        final TclObject tObj = (TclObject) Widgets.getWidget(interp, argv[0].toString());
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
                    swksmenu.configure(interp, argv, 2);
                }

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
        String sIndex = null;
        String sIndexLast = null;
        int first = 0;
        int last = 0;

        void exec(final SwkSMenu swksmenu, final TclObject firstArg,
                final TclObject lastArg) {
            this.firstArg = firstArg;
            this.lastArg = lastArg;
            this.swksmenu = swksmenu;

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
                    first = swksmenu.getIndex(sIndex, -1);
                } catch (TclException Tce) {
                }
            }

            if (lastArg == null) {
                last = first;
            } else {
                if (sIndexLast != null) {
                    try {
                        last = swksmenu.getIndex(sIndexLast, -1);
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
                if ((i >= 0) && (i < swksmenu.getComponentCount())) {
                    swksmenu.remove(i);
                }
            }
        }
    }
}
