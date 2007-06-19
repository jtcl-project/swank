/*
 * Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file "LICENSE" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 *
 */
package com.onemoonscientific.swank;

import tcl.lang.*;

import java.awt.Rectangle;

import javax.swing.plaf.ScrollBarUI;
import javax.swing.plaf.basic.BasicScrollBarUI;


class SwkJScrollBarWidgetCmd implements Command {
    static final private String[] validCmds = {
        "cget", "configure", "object", "jadd", "activate", "delta", "fraction",
        "get", "identify", "set"
    };
    static final private int OPT_CGET = 0;
    static final private int OPT_CONFIGURE = 1;
    static final private int OPT_OBJECT = 2;
    static final private int OPT_JADD = 3;
    static final private int OPT_ACTIVATE = 4;
    static final private int OPT_DELTA = 5;
    static final private int OPT_FRACTION = 6;
    static final private int OPT_GET = 7;
    static final private int OPT_IDENTIFY = 8;
    static final private int OPT_SET = 9;
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
        final TclObject tObj = (TclObject) Widgets.theWidgets.get(argv[0].toString());

        if (tObj == null) {
            throw new TclException(interp,
                "bad window path name \"" + argv[0].toString() + "\"");
        }

        final SwkJScrollBar swkjscrollbar = (SwkJScrollBar) ReflectObject.get(interp,
                tObj);

        switch (opt) {
        case OPT_CGET:

            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            interp.setResult(swkjscrollbar.jget(interp, argv[2]));

            break;

        case OPT_CONFIGURE:

            if (!gotDefaults) {
                swkjscrollbar.setResourceDefaults();
                gotDefaults = true;
            }

            if (argv.length == 2) {
                swkjscrollbar.jgetAll(interp);
            } else if (argv.length == 3) {
                String result = swkjscrollbar.jget(interp, argv[2]);
                ResourceObject ro = (ResourceObject) SwkJScrollBar.resourceDB.get(argv[2].toString());

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
                swkjscrollbar.configure(interp, argv, 2);
            }

            break;

        case OPT_OBJECT:
            interp.setResult(tObj);

            break;

        case OPT_JADD:

            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            swkjscrollbar.jadd(interp, argv[2]);

            break;

        case OPT_ACTIVATE:
            activate(interp, swkjscrollbar, argv);

            break;

        case OPT_DELTA:
            delta(interp, swkjscrollbar, argv);

            break;

        case OPT_FRACTION:
            fraction(interp, swkjscrollbar, argv);

            break;

        case OPT_GET:
            get(interp, swkjscrollbar, argv);

            break;

        case OPT_IDENTIFY:
            identify(interp, swkjscrollbar, argv);

            break;

        case OPT_SET:
            set(interp, swkjscrollbar, argv);

            break;

        default:
            throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    void activate(Interp interp, SwkJScrollBar swkjscrollbar, TclObject[] argv)
        throws TclException {
        if (argv.length > 3) {
            throw new TclNumArgsException(interp, 2, argv, "?element?");
        }
    }

    void delta(Interp interp, SwkJScrollBar swkjscrollbar, TclObject[] argv)
        throws TclException {
        if (argv.length != 4) {
            throw new TclNumArgsException(interp, 2, argv, "deltaX deltaY");
        }

        double deltaX = TclInteger.get(interp, argv[2]);
        double deltaY = TclInteger.get(interp, argv[3]);
        double result = (new Delta()).exec(swkjscrollbar, deltaX, deltaY);
        interp.setResult(result);
    }

    void fraction(Interp interp, SwkJScrollBar swkjscrollbar, TclObject[] argv)
        throws TclException {
        if (argv.length != 4) {
            throw new TclNumArgsException(interp, 2, argv, "deltaX deltaY");
        }

        double x = TclInteger.get(interp, argv[2]);
        double y = TclInteger.get(interp, argv[3]);
        double result = (new Delta()).exec(swkjscrollbar, x, y);
        interp.setResult(result);
    }

    void get(Interp interp, SwkJScrollBar swkjscrollbar, TclObject[] argv)
        throws TclException {
        if (argv.length != 2) {
            throw new TclNumArgsException(interp, 2, argv, "");
        }

        double[] result = (new Get()).exec(swkjscrollbar);
        TclObject list = TclList.newInstance();
        TclList.append(interp, list, TclDouble.newInstance(result[0]));
        TclList.append(interp, list, TclDouble.newInstance(result[1]));
        interp.setResult(list);
    }

    void identify(Interp interp, SwkJScrollBar swkjscrollbar, TclObject[] argv)
        throws TclException {
        if (argv.length != 4) {
            throw new TclNumArgsException(interp, 2, argv, "x delytaY");
        }
    }

    void set(Interp interp, SwkJScrollBar swkjscrollbar, TclObject[] argv)
        throws TclException {
        if (argv.length != 4) {
            throw new TclNumArgsException(interp, 2, argv,
                "firstFraction lastFraction");
        }

        double firstFraction = TclDouble.get(interp, argv[2]);
        double lastFraction = TclDouble.get(interp, argv[3]);

        if (firstFraction < 0.0) {
            firstFraction = 0.0;
        }

        if (firstFraction > 1.0) {
            firstFraction = 1.0;
        }

        if (lastFraction < firstFraction) {
            lastFraction = firstFraction;
        }

        if (lastFraction > 1.0) {
            lastFraction = 1.0;
        }

        (new Set()).exec(swkjscrollbar, firstFraction, lastFraction);
    }

    class Delta extends GetValueOnEventThread {
        SwkJScrollBar swkjscrollbar;
        double result = 0;
        double deltaX = 0.0;
        double deltaY = 0.0;

        double exec(final SwkJScrollBar swkjscrollbar, final double deltaX,
            final double deltaY) {
            this.swkjscrollbar = swkjscrollbar;
            this.deltaX = deltaX;
            this.deltaY = deltaY;
            execOnThread();

            return result;
        }

        public void run() {
            int min = swkjscrollbar.getMinimum();
            int max = swkjscrollbar.getMaximum();

            if (swkjscrollbar.getOrientation() == SwkJScrollBar.HORIZONTAL) {
                result = deltaX / (max - min);
            } else {
                result = deltaY / (max - min);
            }
        }
    }

    class Fraction extends GetValueOnEventThread {
        SwkJScrollBar swkjscrollbar;
        double result = 0;
        double x = 0.0;
        double y = 0.0;

        double exec(final SwkJScrollBar swkjscrollbar, final double x,
            final double y) {
            this.swkjscrollbar = swkjscrollbar;
            this.x = x;
            this.y = y;
            execOnThread();

            return result;
        }

        public void run() {
            int min = swkjscrollbar.getMinimum();
            int max = swkjscrollbar.getMaximum();

            if (swkjscrollbar.getOrientation() == SwkJScrollBar.HORIZONTAL) {
                if (x > max) {
                    x = max;
                }

                if (x < min) {
                    x = min;
                }

                result = (x - min) / (max - min);
            } else {
                if (y > max) {
                    y = max;
                }

                if (y < min) {
                    y = min;
                }

                result = (y - min) / (max - min);
            }
        }
    }

    class Get extends GetValueOnEventThread {
        SwkJScrollBar swkjscrollbar;
        double[] result = new double[2];

        double[] exec(final SwkJScrollBar swkjscrollbar) {
            this.swkjscrollbar = swkjscrollbar;
            execOnThread();

            return result;
        }

        public void run() {
            int min = swkjscrollbar.getMinimum();
            int max = swkjscrollbar.getMaximum();
            int value = swkjscrollbar.getValue();
            int extent = swkjscrollbar.getVisibleAmount();

            result[0] = ((double) value - min) / (max - min);
            result[1] = (((double) value + extent) - min) / (max - min);
        }
    }

    class Set extends UpdateOnEventThread {
        SwkJScrollBar swkjscrollbar;
        double firstFraction = 0.0;
        double lastFraction = 1.0;

        void exec(final SwkJScrollBar swkjscrollbar,
            final double firstFraction, final double lastFraction) {
            this.swkjscrollbar = swkjscrollbar;
            this.firstFraction = firstFraction;
            this.lastFraction = lastFraction;
            execOnThread();
        }

        public void run() {
            int min = swkjscrollbar.getMinimum();
            int max = swkjscrollbar.getMaximum();
            int value = (int) Math.round((firstFraction * (max - min)) + min);
            int extent = (int) Math.round((lastFraction - firstFraction) * (max -
                    min));
            swkjscrollbar.setFromSet();
            swkjscrollbar.setValues(value, extent, min, max);
        }
    }
}
