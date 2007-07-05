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

import java.io.*;

import java.lang.*;

import java.net.*;

import java.util.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.tree.*;


class SwkJMenuBarWidgetCmd implements Command {
    static final private String[] validCmds = {
        "cget", "configure","add"
    };
    static final private int OPT_CGET = 0;
    static final private int OPT_CONFIGURE = 1;
    static final private int OPT_ADD = 2;
    static boolean gotDefaults = false;
    Interp interp = null;
    public static String[] getValidCmds() {
        return validCmds;
    }

    public void cmdProc(final Interp interp, final TclObject[] argv)
        throws TclException {
        this.interp = interp;
        int i;

        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv,
                "option ?arg arg ...?");
        }

        int opt = TclIndex.get(interp, argv[1], validCmds, "option", 0);
        TclObject tObj = (TclObject) Widgets.theWidgets.get(argv[0].toString());

        if (tObj == null) {
            throw new TclException(interp,
                "bad window path name \"" + argv[0].toString() + "\"");
        }

        SwkJMenuBar swkjmenubar = (SwkJMenuBar) ReflectObject.get(interp, tObj);

        switch (opt) {
        case OPT_CGET:

            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            interp.setResult(swkjmenubar.jget(interp, argv[2]));

            break;

        case OPT_CONFIGURE:

            if (!gotDefaults) {
                swkjmenubar.setResourceDefaults();
                gotDefaults = true;
            }

            if (argv.length == 2) {
                swkjmenubar.jgetAll(interp);
            } else if (argv.length == 3) {
                String result = swkjmenubar.jget(interp, argv[2]);
                ResourceObject ro = (ResourceObject) SwkJMenuBar.resourceDB.get(argv[2].toString());

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
                swkjmenubar.configure(interp, argv, 2);
            }

            break;

        case OPT_ADD:
            if (argv.length < 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }
            addmenu(interp,swkjmenubar,argv);
            break;

        default:
            throw new TclRuntimeError("TclIndex.get() error");
        }
    }


    public void addmenu(Interp interp, SwkJMenuBar swkjmenubar, TclObject[] argv)
        throws TclException {
        int i;

        if (argv.length < 3) {
            throw new TclNumArgsException(interp, 1, argv,
                "option ?arg arg ...?");
        }

        String itemType = argv[2].toString();

        if (itemType.equals("cascade")) {
            String menuName = null;
            int j = 0;

            if (((argv.length - 3) % 2) != 0) {
                throw new TclNumArgsException(interp, 1, argv,
                    "arguments not multiple of 2");
            }

            TclObject[] argNew = new TclObject[argv.length - 5];

            for (i = 3; i < argv.length; i += 2) {
                if (argv[i].toString().equals("-menu")) {
                    menuName = argv[i + 1].toString();
                } else {
                    argNew[j] = TclString.newInstance(argv[i].toString());
                    argNew[j + 1] = TclString.newInstance(argv[i + 1].toString());
                    j += 2;
                }
            }

            TclObject tObj = (TclObject) Widgets.theWidgets.get(menuName);

            if (tObj == null) {
                SwkJMenu cascade = (new Add()).exec(swkjmenubar, menuName);
                interp.createCommand(menuName, new SwkJMenuWidgetCmd());
                tObj = ReflectObject.newInstance(interp, SwkJMenu.class, cascade);
                tObj.preserve();
                cascade.children = null;
                cascade = (SwkJMenu) ReflectObject.get(interp, tObj);
                cascade.configure(interp, argNew, 0);
                Widgets.addNewWidget(interp, menuName, tObj);
                cascade.setCreated(false);
            } else {
                SwkJMenu cascade = (SwkJMenu) ReflectObject.get(interp, tObj);
                (new Add()).exec(swkjmenubar, menuName, cascade);
                cascade.configure(interp, argNew, 0);
            }
        } else {
            throw new TclException(interp,"invalid menu option \""+itemType+"\"");
        }

        swkjmenubar.revalidate();

        return;
    }


    class Add extends GetValueOnEventThread {
        SwkJMenuBar swkjmenubar = null;
        JComponent jcomp = null;
        String menuName = "";
        SwkWidget swkWidget = null;
        SwkJMenu cascade = null;

        SwkJMenu exec(final SwkJMenuBar swkjmenubar, final String menuName) {
            this.swkjmenubar = swkjmenubar;
            this.menuName = menuName;
            execOnThread();

            return cascade;
        }

        SwkJMenu exec(final SwkJMenuBar swkjmenubar, final String menuName, SwkJMenu cascade) {
            this.swkjmenubar = swkjmenubar;
            this.cascade = cascade;
            this.menuName = menuName;
            execOnThread();

            return cascade;
        }

        public void run() {
            if (cascade == null) {
                cascade = new SwkJMenu(interp, menuName, "Menu");
            }

            swkjmenubar.add(cascade);
        }
    }
}
