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

class SwkJInternalFrameWidgetCmd implements Command {

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

        SwkJInternalFrame swkjinternalframe = (SwkJInternalFrame) ReflectObject.get(interp,
                tObj);

        switch (opt) {
            case OPT_CGET:

                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "option");
                }

                interp.setResult(swkjinternalframe.jget(interp, argv[2]));

                break;

            case OPT_CONFIGURE:

                if (!gotDefaults) {
                    swkjinternalframe.setResourceDefaults();
                    gotDefaults = true;
                }

                if (argv.length == 2) {
                    swkjinternalframe.jgetAll(interp);
                } else if (argv.length == 3) {
                    String result = swkjinternalframe.jget(interp, argv[2]);
                    ResourceObject ro = (ResourceObject) SwkJInternalFrame.resourceDB.get(argv[2].toString());

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
                    swkjinternalframe.configure(interp, argv, 2);
                }

                break;

            case OPT_ADD:
                add(interp, swkjinternalframe, argv);

                break;

            default:
                throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    void add(final Interp interp, final SwkJInternalFrame swkjinternalframe,
            final TclObject[] argv) throws TclException {
        if (argv.length != 4) {
            throw new TclNumArgsException(interp, 2, argv, "option");
        }

        TclObject tObj2 = (TclObject) Widgets.getWidget(interp, argv[2].toString());

        if (tObj2 == null) {
            throw new TclException(interp,
                    "bad window path name \"" + argv[2].toString() + "\"");
        }

        JComponent jcomp = (JComponent) ReflectObject.get(interp, tObj2);
        (new Add()).exec(swkjinternalframe, jcomp, argv[3].toString().intern());
    }

    static class Add extends UpdateOnEventThread {

        SwkJInternalFrame swkjinternalframe = null;
        JComponent jcomp = null;
        String label = null;

        void exec(SwkJInternalFrame swkjinternalframe, final JComponent jcomp,
                final String label) {
            this.jcomp = jcomp;
            this.swkjinternalframe = swkjinternalframe;
            this.label = label;
            execOnThread();
        }

        @Override
        public void run() {
            swkjinternalframe.add(jcomp, label);
        }
    }
}
