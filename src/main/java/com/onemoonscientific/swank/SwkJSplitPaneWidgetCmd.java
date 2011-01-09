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

import java.awt.Component;

import javax.swing.*;

class SwkJSplitPaneWidgetCmd implements Command {

    static final private String[] validCmds = {
        "cget", "configure", "add", "forget", "panes"
    };
    static final private int OPT_CGET = 0;
    static final private int OPT_CONFIGURE = 1;
    static final private int OPT_ADD = 2;
    static final private int OPT_FORGET = 3;
    static final private int OPT_PANES = 4;
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

        final SwkJSplitPane swkjsplitpane = (SwkJSplitPane) ReflectObject.get(interp,
                tObj);

        switch (opt) {
            case OPT_CGET:

                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "option");
                }

                interp.setResult(swkjsplitpane.jget(interp, argv[2]));

                break;

            case OPT_CONFIGURE:

                if (!gotDefaults) {
                    swkjsplitpane.setResourceDefaults();
                    gotDefaults = true;
                }

                if (argv.length == 2) {
                    swkjsplitpane.jgetAll(interp);
                } else if (argv.length == 3) {
                    String result = swkjsplitpane.jget(interp, argv[2]);
                    ResourceObject ro = (ResourceObject) SwkJSplitPane.resourceDB.get(argv[2].toString());

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
                    swkjsplitpane.configure(interp, argv, 2);
                }

                break;

            case OPT_ADD:
                add(interp, swkjsplitpane, argv);

                break;

            case OPT_FORGET:
                forget(interp, swkjsplitpane, argv);

                break;

            case OPT_PANES:
                panes(interp, swkjsplitpane, argv);

                break;

            default:
                throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    void add(final Interp interp, final SwkJSplitPane swkjsplitpane,
            final TclObject[] argv) throws TclException {
        if ((argv.length < 3) || (argv.length > 5)) {
            throw new TclNumArgsException(interp, 2, argv, "option");
        }

        int iComp = 0;
        int mode = Add.NONE;
        int relativeMode = Add.NONE;
        final JComponent[] jcomps = {null, null};
        JComponent relativeComp = null;

        for (int i = 2; i < argv.length; i++) {
            if (i == 2) { // first argument could be a position specifier

                if (argv[i].toString().equals("left")) {
                    mode = Add.LEFT;
                } else if (argv[i].toString().equals("top")) {
                    mode = Add.TOP;
                } else if (argv[i].toString().equals("bottom")) {
                    mode = Add.BOTTOM;
                } else if (argv[i].toString().equals("right")) {
                    mode = Add.RIGHT;
                }
            }

            if ((i > 2) || (mode == Add.NONE)) { // then it should be a window specifier or widget option

                if (argv[i].toString().startsWith("-")) {
                    if (argv[i].toString().equals("-after")) {
                        relativeMode = Add.AFTER;
                    } else if (argv[i].toString().equals("-before")) {
                        relativeMode = Add.BEFORE;
                    } else {
                        throw new TclException(interp,
                                "unknown option \"" + argv[i].toString() + "\"");
                    }

                    if (i >= argv.length) {
                        throw new TclException(interp,
                                "missing value for \"" + argv[i].toString() + "\"");
                    }

                    final TclObject winObj = (TclObject) Widgets.getWidget(interp, argv[i
                            + 1].toString());

                    if (winObj == null) {
                        throw new TclException(interp,
                                "bad window path name \"" + argv[i + 1].toString()
                                + "\"");
                    }

                    relativeComp = (JComponent) ReflectObject.get(interp, winObj);
                    i++;
                } else {
                    if (relativeMode != Add.NONE) {
                        throw new TclException(interp,
                                "unknown option \"" + argv[i].toString() + "\"");
                    }

                    final TclObject winObj = (TclObject) Widgets.getWidget(interp, argv[i].toString());

                    if (winObj == null) {
                        throw new TclException(interp,
                                "bad window path name \"" + argv[i + 2].toString()
                                + "\"");
                    }

                    jcomps[iComp] = (JComponent) ReflectObject.get(interp,
                            winObj);
                    iComp++;
                }
            }
        }

        (new Add()).exec(swkjsplitpane, jcomps, mode, relativeComp, relativeMode);
    }

    void forget(final Interp interp, final SwkJSplitPane swkjsplitpane,
            final TclObject[] argv) throws TclException {
        if ((argv.length < 3) || (argv.length > 4)) {
            throw new TclNumArgsException(interp, 2, argv, "win ?win?");
        }

        final JComponent[] jcomps = {null, null};

        for (int i = 0; i < 2; i++) {
            if (i >= (argv.length - 2)) {
                break;
            }

            final TclObject winObj = (TclObject) Widgets.getWidget(interp, argv[i
                    + 2].toString());

            if (winObj == null) {
                throw new TclException(interp,
                        "bad window path name \"" + argv[i + 2].toString() + "\"");
            }

            jcomps[i] = (JComponent) ReflectObject.get(interp, winObj);
        }

        (new Forget()).exec(swkjsplitpane, jcomps);
    }

    void panes(final Interp interp, final SwkJSplitPane swkjsplitpane,
            final TclObject[] argv) throws TclException {
        if (argv.length != 2) {
            throw new TclNumArgsException(interp, 2, argv, "");
        }

        TclObject list = TclList.newInstance();
        String[] names = (new Panes()).exec(swkjsplitpane);

        for (int i = 0; i < names.length; i++) {
            if (names[i] != null) {
                TclList.append(interp, list, TclString.newInstance(names[i]));
            }
        }

        interp.setResult(list);
    }

    static class Add extends UpdateOnEventThread {

        static final int LEFT = 0;
        static final int TOP = 1;
        static final int BOTTOM = 2;
        static final int RIGHT = 3;
        static final int NONE = 4;
        static final int AFTER = 5;
        static final int BEFORE = 6;
        SwkJSplitPane swkjsplitpane = null;
        JComponent[] jcomps = null;
        JComponent relComp = null;
        int mode = NONE;
        int relMode = NONE;

        void exec(final SwkJSplitPane swkjsplitpane, JComponent[] jcomps,
                final int mode, JComponent relComp, final int relMode) {
            this.swkjsplitpane = swkjsplitpane;
            this.mode = mode;
            this.jcomps = jcomps;
            this.relMode = relMode;
            this.relComp = relComp;
            execOnThread();
        }

        @Override
        public void run() {
            int nComps = 1;

            if (jcomps[0] == null) {
                return;
            }

            if (jcomps[1] != null) {
                nComps = 2;
            }

            int relCompPosition = -1;
            System.out.println("ncomps " + nComps);

            if ((relMode != NONE) && (relComp != null)) {
                if (relComp == swkjsplitpane.getLeftComponent()) {
                    relCompPosition = 0;
                } else if (relComp == swkjsplitpane.getRightComponent()) {
                    relCompPosition = 1;
                } else {
                    // FIXME throw error
                    return;
                }
            } else if (nComps == 1) {
                relComp = (JComponent) swkjsplitpane.getLeftComponent();

                if (relComp != null) {
                    relCompPosition = 1;
                    relMode = AFTER;
                }
            }

            if ((nComps == 2) && (relCompPosition != -1)) {
                return;
            }

            int newPosition = 0;

            if (relCompPosition != -1) {
                if (relMode == AFTER) {
                    jcomps[1] = jcomps[0];
                    jcomps[0] = relComp;
                } else {
                    jcomps[1] = relComp;
                }
            }

            switch (mode) {
                case RIGHT:
                    jcomps[1] = jcomps[0];
                    jcomps[0] = null;

                case LEFT:
                    swkjsplitpane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);

                    break;

                case BOTTOM:
                    jcomps[1] = jcomps[0];
                    jcomps[0] = null;

                case TOP:
                    swkjsplitpane.setOrientation(JSplitPane.VERTICAL_SPLIT);

                    break;
            }

            int iComp = 0;

            for (int i = 0; i < jcomps.length; i++) {
                if (jcomps[i] != null) {
                    if (iComp == 0) {
                        swkjsplitpane.setLeftComponent(jcomps[i]);
                    } else {
                        swkjsplitpane.setRightComponent(jcomps[i]);
                    }

                    iComp++;
                }
            }
        }
    }

    static class Forget extends UpdateOnEventThread {

        SwkJSplitPane swkjsplitpane = null;
        JComponent[] jcomps = null;

        void exec(final SwkJSplitPane swkjsplitpane, JComponent[] jcomps) {
            this.jcomps = jcomps;
            this.swkjsplitpane = swkjsplitpane;
            execOnThread();
        }

        @Override
        public void run() {
            for (int i = 0; i < jcomps.length; i++) {
                if (jcomps[i] != null) {
                    swkjsplitpane.remove(jcomps[i]);
                }
            }
        }
    }

    static class Panes extends GetValueOnEventThread {

        SwkJSplitPane swkjsplitpane = null;
        String[] names = {null, null};

        String[] exec(final SwkJSplitPane swkjsplitpane) {
            this.swkjsplitpane = swkjsplitpane;
            execOnThread();

            return names;
        }

        @Override
        public void run() {
            Component comp = swkjsplitpane.getTopComponent();
            int iComp = 0;

            if (comp != null) {
                names[0] = comp.getName();
                iComp++;
            }

            comp = swkjsplitpane.getBottomComponent();

            if (comp != null) {
                names[iComp] = comp.getName();
            }
        }
    }
}
