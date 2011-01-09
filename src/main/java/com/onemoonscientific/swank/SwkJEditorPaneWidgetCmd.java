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

class SwkJEditorPaneWidgetCmd implements Command {

    static final private String[] validCmds = {
        "cget", "configure", "scrolltoreference"
    };
    static final private int OPT_CGET = 0;
    static final private int OPT_CONFIGURE = 1;
    static final private int OPT_SCROLLTOREFERENCE = 2;
    static boolean gotDefaults = false;
    SwkJEditorPane swkjeditorpane = null;

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

        swkjeditorpane = (SwkJEditorPane) ReflectObject.get(interp, tObj);

        switch (opt) {
            case OPT_CGET:

                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "option");
                }

                interp.setResult(swkjeditorpane.jget(interp, argv[2]));

                break;

            case OPT_CONFIGURE:

                if (!gotDefaults) {
                    swkjeditorpane.setResourceDefaults();
                    gotDefaults = true;
                }

                if (argv.length == 2) {
                    swkjeditorpane.jgetAll(interp);
                } else if (argv.length == 3) {
                    String result = swkjeditorpane.jget(interp, argv[2]);
                    ResourceObject ro = (ResourceObject) SwkJButton.resourceDB.get(argv[2].toString());

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
                    swkjeditorpane.configure(interp, argv, 2);
                }

                break;

            case OPT_SCROLLTOREFERENCE:

                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "option");
                }

                final String reference = argv[2].toString();
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        swkjeditorpane.scrollToReference(reference);
                    }
                });


                break;

            default:
                throw new TclRuntimeError("TclIndex.get() error");
        }
    }
}
