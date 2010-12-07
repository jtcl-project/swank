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

class SwkJTextFieldWidgetCmd implements Command {

    static final private String[] validCmds = {
        "cget", "configure", "delete", "get", "index",
        "insert", "scan", "selection", "xview"
    };
    static final private int OPT_CGET = 0;
    static final private int OPT_CONFIGURE = 1;
    static final private int OPT_DELETE = 2;
    static final private int OPT_GET = 3;
    static final private int OPT_INDEX = 4;
    static final private int OPT_INSERT = 5;
    static final private int OPT_SCAN = 6;
    static final private int OPT_SELECTION = 7;
    static final private int OPT_XVIEW = 8;
    static boolean gotDefaults = false;
    int index;
    Interp interp = null;

    public static String[] getValidCmds() {
        return validCmds;
    }

    public void cmdProc(Interp interp, TclObject[] argv)
            throws TclException {
        int i;

        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv,
                    "option ?arg arg ...?");
        }

        this.interp = interp;

        int opt = TclIndex.get(interp, argv[1], validCmds, "option", 0);
        TclObject tObj = (TclObject) Widgets.getWidget(interp, argv[0].toString());

        if (tObj == null) {
            throw new TclException(interp,
                    "bad window path name \"" + argv[0].toString() + "\"");
        }

        final SwkJTextField swkjtextfield = (SwkJTextField) ReflectObject.get(interp,
                tObj);

        switch (opt) {
            case OPT_CGET:

                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "option");
                }

                interp.setResult(swkjtextfield.jget(interp, argv[2]));

                break;

            case OPT_CONFIGURE:

                if (!gotDefaults) {
                    swkjtextfield.setResourceDefaults();
                    gotDefaults = true;
                }

                if (argv.length == 2) {
                    swkjtextfield.jgetAll(interp);
                } else if (argv.length == 3) {
                    String result = swkjtextfield.jget(interp, argv[2]);
                    ResourceObject ro = (ResourceObject) SwkJTextField.resourceDB.get(argv[2].toString());

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
                    swkjtextfield.configure(interp, argv, 2);
                }

                break;

            case OPT_DELETE:
                break;

            case OPT_GET:
                interp.setResult(swkjtextfield.getText());

                break;

            case OPT_INDEX:
                getIndex(interp, swkjtextfield, argv, 0);

                break;

            case OPT_INSERT:
                insert(interp, swkjtextfield, argv);

                break;

            case OPT_SCAN:
                break;

            case OPT_SELECTION:
                break;

            case OPT_XVIEW:

                if (argv.length == 2) {
                    (new ViewValues()).exec(swkjtextfield);
                } else if (argv.length == 3) {
                    index = getIndex2(interp, swkjtextfield, argv, -1);

                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            BoundedRangeModel brm = swkjtextfield.getHorizontalVisibility();
                            int maxSize = swkjtextfield.getText().length();
                            double fx1 = (1.0 * index) / maxSize;

                            if (fx1 < 0.0) {
                                fx1 = 0.0;
                            }

                            final int x = (int) ((fx1 * (brm.getMaximum()
                                    - brm.getMinimum())) + brm.getMinimum());
                            swkjtextfield.setScrollOffset(x);
                        }
                    });
                } else if (argv[2].toString().equals("moveto")) {
                    if (argv.length != 4) {
                        throw new TclNumArgsException(interp, 2, argv,
                                "option ?arg arg ...?");
                    }

                    final double fx1 = TclDouble.get(interp, argv[3]);

                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            BoundedRangeModel brm = swkjtextfield.getHorizontalVisibility();
                            double fx = fx1;

                            if (fx1 < 0.0) {
                                fx = 0.0;
                            }

                            final int x = (int) ((fx * (brm.getMaximum()
                                    - brm.getMinimum())) + brm.getMinimum());

                            //            System.out.println("Moveto is called for TextField " + x);
                            swkjtextfield.setScrollOffset(x);
                        }
                    });
                } else if (argv[2].toString().equals("scroll")) {
                    if (argv.length != 5) {
                        throw new TclNumArgsException(interp, 2, argv,
                                "option ?arg arg ...?");
                    }

                    if (argv[4].toString().equals("units")) {
                        final int units = TclInteger.get(interp, argv[3]);

                        SwingUtilities.invokeLater(new Runnable() {

                            public void run() {
                                int x = swkjtextfield.getScrollOffset();
                                swkjtextfield.setScrollOffset(x + units);
                            }
                        });
                    } else if (argv[4].toString().equals("pages")) {
                        final int units = TclInteger.get(interp, argv[3]);

                        SwingUtilities.invokeLater(new Runnable() {

                            public void run() {
                                int x = swkjtextfield.getScrollOffset();
                                swkjtextfield.setScrollOffset(x + units);
                            }
                        });
                    }
                } else {
                    throw new TclException(interp,
                            "unknown option \"" + argv[2].toString()
                            + "\": must be moveto or scroll");
                }

                break;

            default:
                throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    int getIndex2(final Interp interp, final SwkJTextField swkjtextfield,
            final TclObject[] argv, int offset) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "index");
        }

        int index = (new Index()).exec(swkjtextfield, argv[2].toString(), offset);

        return index;
    }

    void getIndex(final Interp interp, final SwkJTextField swkjtextfield,
            final TclObject[] argv, int offset) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "index");
        }

        int index = (new Index()).exec(swkjtextfield, argv[2].toString(), offset);
        interp.setResult(index);
    }

    void insert(final Interp interp, final SwkJTextField swkjtextfield,
            final TclObject[] argv) throws TclException {
        if (argv.length != 4) {
            throw new TclNumArgsException(interp, 2, argv, "option");
        }

        (new Insert()).exec(swkjtextfield, argv[2].toString(),
                argv[3].toString());
    }

    class Index extends GetValueOnEventThread {

        SwkJTextField swkjtextfield = null;
        String item = null;
        int index = 0;
        int endVal = 0;
        String errMessage = null;

        int exec(final SwkJTextField swkjtextfield, final String item,
                int endVal) throws TclException {
            this.item = item;
            this.endVal = endVal;
            this.swkjtextfield = swkjtextfield;
            execOnThread();

            if (errMessage != null) {
                throw new TclException(interp, errMessage);
            }

            return index;
        }

        public void run() {
            Result result = new Result();
            swkjtextfield.getIndex(item, endVal, result);

            if (result.hasError()) {
                errMessage = result.getErrorMsg();

                return;
            }

            index = result.i;
        }
    }

    class Insert extends UpdateOnEventThread {

        SwkJTextField swkjtextfield = null;
        String strIndex = null;
        String text = null;
        String errMessage = null;

        void exec(final SwkJTextField swkjtextfield, final String strIndex,
                final String text) throws TclException {
            this.strIndex = strIndex;
            this.text = text;
            this.swkjtextfield = swkjtextfield;
            execOnThread();
        }

        public void run() {
            Result result = new Result();
            swkjtextfield.getIndex(strIndex, 0, result);

            if (!result.hasError()) {
                try {
                    swkjtextfield.getDocument().insertString(result.i, text,
                            null);
                } catch (BadLocationException bLE) {
                    // throw new TclException(interp, bLE.toString());
                    // FIXME need to do something like add background error
                }
            }
        }
    }

    class ViewValues extends GetValueOnEventThread {

        SwkJTextField swkjtextfield = null;
        double fx1 = 0.0;
        double fx2 = 0.0;

        void exec(final SwkJTextField swkjtextfield) throws TclException {
            this.swkjtextfield = swkjtextfield;
            execOnThread();

            TclObject list = TclList.newInstance();
            TclList.append(interp, list, TclDouble.newInstance(fx1));
            TclList.append(interp, list, TclDouble.newInstance(fx2));
            interp.setResult(list);
        }

        public void run() {
            BoundedRangeModel brm = swkjtextfield.getHorizontalVisibility();
            fx1 = (1.0 * brm.getValue()) / (brm.getMaximum()
                    - brm.getMinimum());
            fx2 = (1.0 * (brm.getValue() + brm.getExtent())) / (brm.getMaximum()
                    - brm.getMinimum());
        }
    }
}
