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
import javax.swing.*;

class SwkJDialogWidgetCmd implements Command {

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

        int opt = TclIndex.get(interp, argv[1], validCmds, "option", 0);
        TclObject tObj = (TclObject) Widgets.getWidget(interp, argv[0].toString());

        if (tObj == null) {
            throw new TclException(interp,
                    "bad window path name \"" + argv[0].toString() + "\"");
        }

        SwkJDialog swkjdialog = (SwkJDialog) ReflectObject.get(interp, tObj);

        switch (opt) {
            case OPT_CGET:

                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "option");
                }

                interp.setResult(swkjdialog.jget(interp, argv[2]));

                break;

            case OPT_CONFIGURE:

                if (!gotDefaults) {
                    swkjdialog.setResourceDefaults();
                    gotDefaults = true;
                }

                if (argv.length == 2) {
                    swkjdialog.jgetAll(interp);
                } else if (argv.length == 3) {
                    String result = swkjdialog.jget(interp, argv[2]);
                    ResourceObject ro = (ResourceObject) SwkJDialog.resourceDB.get(argv[2].toString());

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
                    swkjdialog.configure(interp, argv, 2);
                }

                break;
            case OPT_ADD:
                add(interp, swkjdialog, argv);

                break;

            default:
                throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    void add(final Interp interp, final SwkJDialog swkjdialog,
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
        (new Add()).exec(swkjdialog, jcomp);
    }

    private static class Add extends UpdateOnEventThread {

        SwkJDialog swkjdialog = null;
        JComponent jcomp = null;

        void exec(final SwkJDialog swkjdialog, JComponent jcomp) {
            this.jcomp = jcomp;
            this.swkjdialog = swkjdialog;
            execOnThread();
        }

        @Override
        public void run() {
            swkjdialog.add(jcomp);
        }
    }
}
