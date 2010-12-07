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

import java.awt.EventQueue;

import javax.swing.*;

class SwkJScrollPaneWidgetCmd implements Command {

    static final private String[] validCmds = {
        "cget", "configure", "add"
    };
    static final private int OPT_CGET = 0;
    static final private int OPT_CONFIGURE = 1;
    static final private int OPT_ADD = 2;
    static boolean gotDefaults = false;

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

        if (tObj == null) {
            throw new TclException(interp,
                    "bad window path name \"" + argv[0].toString() + "\"");
        }

        final SwkJScrollPane swkjscrollpane = (SwkJScrollPane) ReflectObject.get(interp,
                tObj);

        switch (opt) {
            case OPT_CGET:

                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "option");
                }

                interp.setResult(swkjscrollpane.jget(interp, argv[2]));

                break;

            case OPT_CONFIGURE:

                if (!gotDefaults) {
                    swkjscrollpane.setResourceDefaults();
                    gotDefaults = true;
                }

                if (argv.length == 2) {
                    swkjscrollpane.jgetAll(interp);
                } else if (argv.length == 3) {
                    String result = swkjscrollpane.jget(interp, argv[2]);
                    ResourceObject ro = (ResourceObject) SwkJScrollPane.resourceDB.get(argv[2].toString());

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
                    swkjscrollpane.configure(interp, argv, 2);
                }

                break;

            case OPT_ADD:
                add(interp, swkjscrollpane, argv);

                break;

            default:
                throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    void add(final Interp interp, final SwkJScrollPane swkjscrollpane,
            final TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "option");
        }

        final TclObject tObj2 = (TclObject) Widgets.getWidget(interp, argv[2].toString());

        if (tObj2 == null) {
            throw new TclException(interp,
                    "bad window path name \"" + argv[2].toString() + "\"");
        }

        final JComponent jcomp = (JComponent) ReflectObject.get(interp, tObj2);
        (new Add()).exec(swkjscrollpane, jcomp);
    }

    class Add extends UpdateOnEventThread {

        static final int LEFT = 0;
        static final int TOP = 1;
        static final int BOTTOM = 2;
        static final int RIGHT = 3;
        SwkJScrollPane swkjscrollpane = null;
        JComponent jcomp = null;

        void exec(final SwkJScrollPane swkjscrollpane, JComponent jcomp) {
            this.jcomp = jcomp;
            this.swkjscrollpane = swkjscrollpane;
            execOnThread();
        }

        public void run() {
            swkjscrollpane.setViewportView(jcomp);
        }
    }
}
