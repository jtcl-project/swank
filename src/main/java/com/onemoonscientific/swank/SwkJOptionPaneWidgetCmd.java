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

class SwkJOptionPaneWidgetCmd implements Command {

    static final private String[] validCmds = {
        "cget", "configure", "dialog"
    };
    static final private int OPT_CGET = 0;
    static final private int OPT_CONFIGURE = 1;
    static final private int OPT_DIALOG = 2;
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
        TclObject tObj = (TclObject) Widgets.getWidget(interp, argv[0].toString());

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
                    swkjoptionpane.configure(interp, argv, 2);
                }

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
        if ((argv.length != 3) && (argv.length != 4)) {
            throw new TclNumArgsException(interp, 2, argv, "title ?alwaysOnTop?");
        }
        boolean alwaysOnTop = true;
        if (argv.length == 4) {
            alwaysOnTop = TclBoolean.get(interp, argv[3]);
        }

        (new Dialog()).exec(argv[2].toString(), alwaysOnTop);
    }

    class Dialog extends GetValueOnEventThread {

        String title = "";
        Object result = null;
        boolean alwaysOnTop = true;

        void exec(String title, boolean alwaysOnTop) {
            this.title = title;
            this.alwaysOnTop = alwaysOnTop;
            execOnThread();
            if (result == null) {
                interp.resetResult();
            } else {
                interp.setResult(result.toString());
            }
        }

        @Override
        public void run() {
            JDialog dialog = swkjoptionpane.createDialog(null, title);
            dialog.setAlwaysOnTop(alwaysOnTop);
            dialog.show();
            dialog.pack();
            result = swkjoptionpane.getValue();
        }
    }
}
