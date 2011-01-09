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

class SwkJSpinnerWidgetCmd implements Command {

    static final private String[] validCmds = {
        "cget", "configure", "get", "set"
    };
    static final private int OPT_CGET = 0;
    static final private int OPT_CONFIGURE = 1;
    static final private int OPT_GET = 2;
    static final private int OPT_SET = 3;
    static boolean gotDefaults = false;
    int index;

    public static String[] getValidCmds() {
        return validCmds;
    }

    public void cmdProc(final Interp interp, final TclObject[] argv)
            throws TclException {
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

        final SwkJSpinner swkjspinner = (SwkJSpinner) ReflectObject.get(interp,
                tObj);

        switch (opt) {
            case OPT_CGET:

                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "option");
                }

                interp.setResult(swkjspinner.jget(interp, argv[2]));

                break;

            case OPT_CONFIGURE:

                if (!gotDefaults) {
                    swkjspinner.setResourceDefaults();
                    gotDefaults = true;
                }

                if (argv.length == 2) {
                    swkjspinner.jgetAll(interp);
                } else if (argv.length == 3) {
                    String result = swkjspinner.jget(interp, argv[2]);
                    ResourceObject ro = (ResourceObject) SwkJSpinner.resourceDB.get(argv[2].toString());

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
                    swkjspinner.configure(interp, argv, 2);
                }

                break;

            case OPT_GET: {
                if (argv.length != 2) {
                    throw new TclNumArgsException(interp, 2, argv, "");
                }
                String value = (new Get()).exec(swkjspinner);
                interp.setResult(value);
                break;
            }
            case OPT_SET: {

                if ((argv.length != 2) && (argv.length != 3)) {
                    throw new TclNumArgsException(interp, 2, argv, "?string?");
                }
                if (argv.length == 2) {
                    String value = (new Get()).exec(swkjspinner);
                    interp.setResult(value);
                } else {
                    String value = (new Set()).exec(interp, swkjspinner, argv[2]);
                    interp.setResult(value);
                }
                break;
            }

            default:
                throw new TclRuntimeError("TclIndex.get() error");
        }
    }
}

class Get extends GetValueOnEventThread {

    SwkJSpinner swkjspinner = null;
    String value = null;

    String exec(final SwkJSpinner swkjspinner) throws TclException {
        this.swkjspinner = swkjspinner;
        execOnThread();
        return value;
    }

    @Override
    public void run() {
        Object object = swkjspinner.getValue();
        if (object == null) {
            value = "";
        } else {
            value = object.toString();
        }
    }
}

class Set extends GetValueOnEventThread {

    Interp interp;
    SwkJSpinner swkjspinner = null;
    String sValue;
    double dValue;
    String value = "";

    String exec(final Interp interp, final SwkJSpinner swkjspinner, TclObject arg) throws TclException {
        this.interp = interp;
        this.swkjspinner = swkjspinner;
        SpinnerModel model = swkjspinner.getModel();
        if (arg != null) {
            if ((model == null) || !(model instanceof SpinnerNumberModel)) {
                sValue = arg.toString().trim();
                dValue = 0.0;
            } else {
                dValue = TclDouble.get(interp, arg);
                sValue = null;
            }
        }
        execOnThread();
        return value;
    }

    @Override
    public void run() {
        if (sValue == null) {
            swkjspinner.setValue(new Double(dValue));
        } else {
            swkjspinner.setValue(sValue);
        }

        Object object = swkjspinner.getValue();
        if (object == null) {
            value = "";
        } else {
            value = object.toString();
        }
    }
}


