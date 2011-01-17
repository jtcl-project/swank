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

import javax.swing.*;
import javax.swing.plaf.SliderUI;
import javax.swing.plaf.basic.BasicSliderUI;

class SwkJSliderWidgetCmd implements Command {

    private static final String[] validCmds = {
        "cget", "configure", "coords", "get", "identify",
        "set"
    };
    private static final int OPT_CGET = 0;
    private static final int OPT_CONFIGURE = 1;
    private static final int OPT_COORDS = 2;
    private static final int OPT_GET = 3;
    private static final int OPT_IDENTIFY = 4;
    private static final int OPT_SET = 5;
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
                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            swkjslider.updateRange();
                        }
                    });

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
        if (argv.length > 3) {
            throw new TclNumArgsException(interp, 2, argv, "?value?");
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

    }

    void set(final Interp interp, final SwkJSlider swkjslider,
            final TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "value");
        }

        double value = TclDouble.get(interp, argv[2]);
        CommandVarListenerSettings buttonSettings = (new Set()).exec(swkjslider, value);
        swkjslider.sliderChangeListener.tclActionVar(buttonSettings);
    }

    private static class Coords extends GetValueOnEventThread {

        SwkJSlider swkjslider = null;
        boolean hasValue = false;
        Point pt = new Point();

        Point exec(final SwkJSlider swkjslider, final boolean hasValue,
                final double value) {
            this.swkjslider = swkjslider;
            this.hasValue = hasValue;
            execOnThread();

            return pt;
        }

        @Override
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
                y = (int) (((value - from) / (to - from) * (size.height
                        - halfWid)) + halfWid);
            } else {
                x = (int) (((value - from) / (to - from) * (size.width
                        - halfWid)) + halfWid);
                y = size.height / 2;
            }

            pt.x = x;
            pt.y = y;
        }
    }

    private static class Get extends GetValueOnEventThread {

        SwkJSlider swkjslider = null;
        int x = 0;
        int y = 0;
        boolean hasValue = false;
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

        @Override
        @SuppressWarnings("empty-statement")
        public void run() {
            if (!hasValue) {
                if (swkjslider.resolution >= 1.0) {
                    result = TclInteger.newInstance((int) swkjslider.getDValue());
                } else {
                    result = TclDouble.newInstance(swkjslider.getDValue());
                }
            } else {
                SliderUI sliderUI = swkjslider.jslider.getUI();
                int xValue = 0;
                int yValue = 0;
                if (sliderUI instanceof BasicSliderUI) {
                    BasicSliderUI bSliderUI = (BasicSliderUI) sliderUI;
                    xValue = bSliderUI.valueForXPosition(x);
                    yValue = bSliderUI.valueForYPosition(y);
               }
               int value;
                if (swkjslider.jslider.getOrientation() == JSlider.VERTICAL) {
                    value = yValue;
                   
                } else {
                    value = xValue;
                }
                if ((swkjslider.resolution == 0)
                        || (swkjslider.resolution >= 1.0)) {
                    ;
                    int iValue = (int) Math.round(swkjslider.convertValue(value));
                    result = TclInteger.newInstance(iValue);
                } else {
                        result = TclDouble.newInstance(swkjslider.convertValue(value));
                }
            }
        }
    }

    private static class Set extends GetValueOnEventThread {

        SwkJSlider swkjslider = null;
        double value = 0.0;
        CommandVarListenerSettings sliderSettings = null;

        CommandVarListenerSettings exec(final SwkJSlider swkjslider, final double value) {
            this.value = value;
            this.swkjslider = swkjslider;
            execOnThread();
            return sliderSettings;
        }

        @Override
        public void run() {
            if (swkjslider.isEnabled()) {
                swkjslider.setDValue(value);
                swkjslider.sliderChangeListener.updateSettingsValue();

            }
            sliderSettings = swkjslider.sliderChangeListener.getButtonSettings();
        }
    }
}
