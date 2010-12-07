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

import java.awt.EventQueue;
import java.awt.Color;

import javax.swing.*;

class SwkJTabbedPaneWidgetCmd implements Command {

    static final private String[] validCmds = {
        "cget", "configure", "add", "select", "tabconfigure",
        "tabcget", "tabcount", "index"
    };
    static final private int OPT_CGET = 0;
    static final private int OPT_CONFIGURE = 1;
    static final private int OPT_ADD = 2;
    static final private int OPT_SELECT = 3;
    static final private int OPT_TABCONFIGURE = 4;
    static final private int OPT_TABCGET = 5;
    static final private int OPT_TABCOUNT = 6;
    static final private int OPT_INDEX = 7;
    static boolean gotDefaults = false;
    Interp interp = null;

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

        this.interp = interp;

        final int opt = TclIndex.get(interp, argv[1], validCmds, "option", 0);
        final TclObject tObj = (TclObject) Widgets.getWidget(interp, argv[0].toString());

        if (tObj == null) {
            throw new TclException(interp,
                    "bad window path name \"" + argv[0].toString() + "\"");
        }

        final SwkJTabbedPane swkjtabbedpane = (SwkJTabbedPane) ReflectObject.get(interp,
                tObj);

        switch (opt) {
            case OPT_CGET:

                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "option");
                }

                interp.setResult(swkjtabbedpane.jget(interp, argv[2]));

                break;

            case OPT_CONFIGURE:

                if (!gotDefaults) {
                    swkjtabbedpane.setResourceDefaults();
                    gotDefaults = true;
                }

                if (argv.length == 2) {
                    swkjtabbedpane.jgetAll(interp);
                } else if (argv.length == 3) {
                    String result = swkjtabbedpane.jget(interp, argv[2]);
                    ResourceObject ro = (ResourceObject) SwkJTabbedPane.resourceDB.get(argv[2].toString());

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
                    swkjtabbedpane.configure(interp, argv, 2);
                }

                break;

            case OPT_ADD:
                add(interp, swkjtabbedpane, argv);

                break;

            case OPT_SELECT:
                select(interp, swkjtabbedpane, argv);

                break;

            case OPT_TABCONFIGURE:
                tabConfigure(interp, swkjtabbedpane, argv);

                break;

            case OPT_TABCGET:
                tabCGet(interp, swkjtabbedpane, argv);

                break;

            case OPT_TABCOUNT:
                getTabCount(interp, swkjtabbedpane, argv);

                break;

            case OPT_INDEX:
                getIndex(interp, swkjtabbedpane, argv);

                break;

            default:
                throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    void add(final Interp interp, final SwkJTabbedPane swkjtabbedpane,
            final TclObject[] argv) throws TclException {
        if (argv.length != 4) {
            throw new TclNumArgsException(interp, 2, argv, "window title");
        }

        final TclObject tObj2 = (TclObject) Widgets.getWidget(interp, argv[2].toString());

        if (tObj2 == null) {
            throw new TclException(interp,
                    "bad window path name \"" + argv[2].toString() + "\"");
        }

        final JComponent jcomp = (JComponent) ReflectObject.get(interp, tObj2);
        (new Add()).exec(swkjtabbedpane, jcomp, argv[3].toString().intern());
    }

    void select(final Interp interp, final SwkJTabbedPane swkjtabbedpane,
            final TclObject[] argv) throws TclException {
        if ((argv.length != 2) && (argv.length != 3)) {
            throw new TclNumArgsException(interp, 2, argv, "index");
        }

        if (argv.length == 3) {
            int index = TclInteger.get(interp, argv[2]);
            (new SetSelected()).exec(swkjtabbedpane, index);
        } else {
            int index = (new GetSelected()).exec(swkjtabbedpane);
            interp.setResult(index);
        }
    }

    void tabConfigure(final Interp interp, final SwkJTabbedPane swkjtabbedpane,
            final TclObject[] argv) throws TclException {
        if (argv.length < 5) {
            throw new TclNumArgsException(interp, 2, argv,
                    "index item value ? item value ...?");
        }

        int index = TclInteger.get(interp, argv[2]);
        (new TabConfigure()).exec(swkjtabbedpane, index, 3, argv);
    }

    void tabCGet(final Interp interp, final SwkJTabbedPane swkjtabbedpane,
            final TclObject[] argv) throws TclException {
        if ((argv.length != 4)) {
            throw new TclNumArgsException(interp, 2, argv, "index");
        }

        int index = TclInteger.get(interp, argv[2]);
        (new TabCGet()).exec(swkjtabbedpane, argv[3], index);
    }

    void getTabCount(final Interp interp, final SwkJTabbedPane swkjtabbedpane,
            final TclObject[] argv) throws TclException {
        if ((argv.length != 2)) {
            throw new TclNumArgsException(interp, 2, argv, "");
        }

        int count = (new GetTabCount()).exec(swkjtabbedpane);
        interp.setResult(count);
    }

    void getIndex(final Interp interp, final SwkJTabbedPane swkjtabbedpane,
            final TclObject[] argv) throws TclException {
        if ((argv.length != 3)) {
            throw new TclNumArgsException(interp, 2, argv, "selected");
        }

        int index = (new GetSelected()).exec(swkjtabbedpane);
        interp.setResult(index);
    }

    class Add extends UpdateOnEventThread {

        SwkJTabbedPane swkjtabbedpane = null;
        JComponent jcomp = null;
        String label = null;

        void exec(final SwkJTabbedPane swkjtabbedpane, final JComponent jcomp,
                final String label) {
            this.jcomp = jcomp;
            this.label = label;
            this.swkjtabbedpane = swkjtabbedpane;

            execOnThread();
        }

        public void run() {
            swkjtabbedpane.add(jcomp, label);
        }
    }

    class GetSelected extends GetValueOnEventThread {

        SwkJTabbedPane swkjtabbedpane = null;
        int index = -1;

        int exec(final SwkJTabbedPane swkjtabbedpane) {
            this.swkjtabbedpane = swkjtabbedpane;
            execOnThread();

            return index;
        }

        public void run() {
            index = swkjtabbedpane.getSelectedIndex();
        }
    }

    class SetSelected extends UpdateOnEventThread {

        SwkJTabbedPane swkjtabbedpane = null;
        int index = -1;

        void exec(final SwkJTabbedPane swkjtabbedpane, final int index) {
            this.swkjtabbedpane = swkjtabbedpane;
            this.index = index;
            execOnThread();
        }

        public void run() {
            swkjtabbedpane.setSelectedIndex(index);
        }
    }

    class TabConfigure extends UpdateOnEventThread {

        SwkJTabbedPane swkjtabbedpane = null;
        int index = -1;
        TclObject[] argv = null;
        Color foreground = null;
        Color background = null;
        String title = null;
        String toolTipText = null;
        ImageIcon icon = null;
        String iconName = null;
        Integer underline = null;
        String state = null;

        void exec(final SwkJTabbedPane swkjtabbedpane, final int index, final int start,
                final TclObject[] argv) throws TclException {
            this.swkjtabbedpane = swkjtabbedpane;
            this.index = index;
            this.argv = new TclObject[argv.length];
            if (((argv.length - start) % 2) != 0) {
                throw new TclNumArgsException(interp, 0, argv,
                        "-option value ? -option value? ...");
            }

            for (int i = start; i < argv.length; i += 2) {
                if (argv[i].toString().equals("-foreground")) {
                    foreground = SwankUtil.getColor(interp, argv[i + 1]);
                } else if (argv[i].toString().equals("-background")) {
                    background = SwankUtil.getColor(interp, argv[i + 1]);
                } else if (argv[i].toString().startsWith("-text")) {
                    title = argv[i + 1].toString();
                } else if (argv[i].toString().startsWith("-tooltiptext")) {
                    toolTipText = argv[i + 1].toString();
                } else if (argv[i].toString().startsWith("-state")) {
                    state = argv[i + 1].toString();
                    if (!state.equals("normal") && !state.equals("disabled")) {
                        throw new TclException(interp, "state must be \"normal\" or \"disabled\"");
                    }
                } else if (argv[i].toString().startsWith("-underline")) {
                    underline = TclInteger.get(interp, argv[i + 1]);
                } else if (argv[i].toString().startsWith("-image")) {
                    iconName = argv[i + 1].toString();
                    icon = SwankUtil.getImageIcon(interp, argv[i + 1]);
                }
            }

            execOnThread();
        }

        public void run() {
            if (background != null) {
                swkjtabbedpane.setBackgroundAt(index, background);
            }
            if (foreground != null) {
                swkjtabbedpane.setForegroundAt(index, foreground);
            }
            if (title != null) {
                if (title.trim().equals("")) {
                    swkjtabbedpane.setTitleAt(index, null);
                } else {
                    swkjtabbedpane.setTitleAt(index, title);
                }
            }
            if (toolTipText != null) {
                if (toolTipText.trim().equals("")) {
                    swkjtabbedpane.setToolTipTextAt(index, null);
                } else {
                    swkjtabbedpane.setToolTipTextAt(index, toolTipText);
                }
            }
            if (iconName != null) {
                if (iconName.trim().equals("")) {
                    swkjtabbedpane.setIconAt(index, null);
                } else {
                    swkjtabbedpane.setIconAt(index, icon);
                }
            }
            if (underline != null) {
                swkjtabbedpane.setDisplayedMnemonicIndexAt(index, underline.intValue());
            }
            if (state != null) {
                if (state.equals("normal")) {
                    swkjtabbedpane.setEnabledAt(index, true);
                } else {
                    swkjtabbedpane.setEnabledAt(index, false);
                }

            }
        }
    }

    class TabCGet extends GetValueOnEventThread {

        SwkJTabbedPane swkjtabbedpane = null;
        TclObject item = null;
        int index = -1;
        String errMessage = null;
        String sItem = null;
        String result = null;

        void exec(final SwkJTabbedPane swkjtabbedpane, final TclObject item,
                final int index) throws TclException {
            this.swkjtabbedpane = swkjtabbedpane;
            this.item = item;
            this.index = index;
            this.sItem = item.toString();

            execOnThread();

            if (errMessage != null) {
                throw new TclException(interp, errMessage);
            }

            interp.setResult(result);
        }

        public void run() {
            //swkjtabbedpane.tabCGet(interp, item, index);
            result = swkjtabbedpane.tabCGet(sItem, index);
        }
    }

    class GetTabCount extends GetValueOnEventThread {

        SwkJTabbedPane swkjtabbedpane = null;
        int count = -1;

        int exec(final SwkJTabbedPane swkjtabbedpane) {
            this.swkjtabbedpane = swkjtabbedpane;
            execOnThread();

            return count;
        }

        public void run() {
            count = swkjtabbedpane.getTabCount();
        }
    }

    /*
     */
}
