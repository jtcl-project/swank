/*
 * Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file "LICENSE" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 *
 */
package com.onemoonscientific.swank;

import tcl.lang.*;

import java.awt.*;

import javax.swing.*;


class SwkJSliderWidgetCmd implements Command {
    static final private String[] validCmds = {
        "cget", "configure", "coords", "get", "identify",
        "set"
    };
    static final private int OPT_CGET = 0;
    static final private int OPT_CONFIGURE = 1;
    static final private int OPT_COORDS = 2;
    static final private int OPT_GET = 3;
    static final private int OPT_IDENTIFY = 4;
    static final private int OPT_SET = 5;
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

        final SwkJSlider swkjslider = (SwkJSlider) ReflectObject.get(interp,
                tObj);

        switch (opt) {
        case OPT_CGET:

            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            interp.setResult(swkjslider.jget(interp, argv[2]));

            break;

        case OPT_CONFIGURE:

            if (!gotDefaults) {
                swkjslider.setResourceDefaults();
                gotDefaults = true;
            }

            if (argv.length == 2) {
                swkjslider.jgetAll(interp);
            } else if (argv.length == 3) {
                String result = swkjslider.jget(interp, argv[2]);
                ResourceObject ro = (ResourceObject) SwkJSlider.resourceDB.get(argv[2].toString());

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
                swkjslider.configure(interp, argv, 2);
            }

            break;

        case OPT_COORDS:
            coords(interp, swkjslider, argv);

            break;

        case OPT_GET:
            get(interp, swkjslider, argv);

            break;

        case OPT_IDENTIFY:
            identify(interp, swkjslider, argv);

            break;

        case OPT_SET:
            set(interp, swkjslider, argv);

            break;

        default:
            throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    void coords(final Interp interp, final SwkJSlider swkjslider,
        final TclObject[] argv) throws TclException {
        if (argv.length != 2) {
            throw new TclNumArgsException(interp, 2, argv, "");
        }

        double value = 0.0;
        Point pt = null;

        if (argv.length == 3) {
            value = TclDouble.get(interp, argv[2]);
            pt = (new Coords()).exec(swkjslider, true, value);
        } else {
            pt = (new Coords()).exec(swkjslider, false, 0.0);
        }

        TclObject list = TclList.newInstance();
        TclList.append(interp, list, TclInteger.newInstance(pt.x));
        TclList.append(interp, list, TclInteger.newInstance(pt.y));
        interp.setResult(list);
    }

    void get(final Interp interp, final SwkJSlider swkjslider,
        final TclObject[] argv) throws TclException {
        if ((argv.length != 2) && (argv.length != 4)) {
            throw new TclNumArgsException(interp, 2, argv, "?x y?");
        }

        int x = 0;
        int y = 0;

        Point pt = null;
        TclObject result = null;

        if (argv.length == 4) {
            x = TclInteger.get(interp, argv[2]);
            y = TclInteger.get(interp, argv[3]);
            result = (new Get()).exec(swkjslider, true, x, y);
        } else {
            result = (new Get()).exec(swkjslider, false, 0, 0);
        }

        interp.setResult(result);
    }

    void identify(final Interp interp, final SwkJSlider swkjslider,
        final TclObject[] argv) throws TclException {
        if (argv.length != 4) {
            throw new TclNumArgsException(interp, 2, argv, "x y");
        }

        int x;
        int y;
        x = TclInteger.get(interp, argv[2]);
        y = TclInteger.get(interp, argv[3]);
    }

    void set(final Interp interp, final SwkJSlider swkjslider,
        final TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "value");
        }

        double value = TclDouble.get(interp, argv[2]);
        (new Set()).exec(swkjslider, value);
    }

    class Coords extends GetValueOnEventThread {
        SwkJSlider swkjslider = null;
        int size = 0;
        double value = 0.0;
        boolean hasValue = false;
        Point pt = new Point();

        Point exec(final SwkJSlider swkjslider, final boolean hasValue,
            final double value) {
            this.swkjslider = swkjslider;
            this.value = value;
            this.hasValue = hasValue;
            execOnThread();

            return pt;
        }

        public void run() {
            Dimension size = swkjslider.getSize();
            double to = swkjslider.getTo();
            double from = swkjslider.getFrom();
            double value = 0.0;

            if (!hasValue) {
                value = swkjslider.getDValue();
            }

            int x;
            int y;
            int halfWid = 7;

            if (swkjslider.jslider.getOrientation() == JSlider.VERTICAL) {
                x = size.width / 2;
                y = (int) (((value - from) / (to - from) * (size.height -
                    halfWid)) + halfWid);
            } else {
                x = (int) (((value - from) / (to - from) * (size.width -
                    halfWid)) + halfWid);
                y = size.height / 2;
            }

            pt.x = x;
            pt.y = y;
        }
    }

    class Get extends GetValueOnEventThread {
        SwkJSlider swkjslider = null;
        int size = 0;
        int x = 0;
        int y = 0;
        boolean hasValue = false;
        Point pt = new Point();
        TclObject result = null;

        TclObject exec(final SwkJSlider swkjslider, final boolean hasValue,
            final int x, final int y) {
            this.swkjslider = swkjslider;
            this.x = x;
            this.y = y;
            this.hasValue = hasValue;
            execOnThread();

            return result;
        }

        public void run() {
            if (!hasValue) {
                if (swkjslider.resolution >= 1.0) {
                    result = TclInteger.newInstance((int) swkjslider.getDValue());
                } else {
                    result = TclDouble.newInstance(swkjslider.getDValue());
                }
            } else {
                Dimension size = swkjslider.getSize();
                double to = swkjslider.getTo();
                double from = swkjslider.getFrom();
                int halfWid = 7;
                double value;

                if (swkjslider.jslider.getOrientation() == JSlider.VERTICAL) {
                    value = ((((double) (y - halfWid)) / (size.height -
                        halfWid)) * (to - from)) + from;
                } else {
                    value = ((((double) (x - halfWid)) / (size.width - halfWid)) * (to -
                        from)) + from;
                }

                if ((swkjslider.resolution == 0) ||
                        (swkjslider.resolution >= 1.0)) {
                    int iValue = (int) Math.round(value);
                    result = TclInteger.newInstance(iValue);
                } else {
                    value = Math.round(value / swkjslider.resolution) * swkjslider.resolution;
                    result = TclDouble.newInstance(value);
                }
            }
        }
    }

    class Set extends UpdateOnEventThread {
        SwkJSlider swkjslider = null;
        double value = 0.0;

        void exec(final SwkJSlider swkjslider, final double value) {
            this.value = value;
            this.swkjslider = swkjslider;
            execOnThread();
        }

        public void run() {
            if (swkjslider.isEnabled()) {
                swkjslider.setDValue(value);
            }
        }
    }
}
