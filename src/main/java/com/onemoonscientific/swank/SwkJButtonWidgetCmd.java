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

import java.net.*;

import java.util.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.tree.*;


class SwkJButtonWidgetCmd implements Command {
    static final private String[] validCmds = {
        "cget", "configure", "flash", "invoke"
    };
    static final private int OPT_CGET = 0;
    static final private int OPT_CONFIGURE = 1;
    static final private int OPT_FLASH = 2;
    static final private int OPT_INVOKE = 3;
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
        final TclObject tObj = (TclObject) Widgets.getWidget(interp,argv[0].toString());

        if (tObj == null) {
            throw new TclException(interp,
                "bad window path name \"" + argv[0].toString() + "\"");
        }

        final SwkJButton swkjbutton = (SwkJButton) ReflectObject.get(interp,
                tObj);

        switch (opt) {
        case OPT_CGET:

            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            interp.setResult(swkjbutton.jget(interp, argv[2]));

            break;

        case OPT_CONFIGURE:

            if (!gotDefaults) {
                swkjbutton.setResourceDefaults();
                gotDefaults = true;
            }

            if (argv.length == 2) {
                swkjbutton.jgetAll(interp);
            } else if (argv.length == 3) {
                String result = swkjbutton.jget(interp, argv[2]);
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
                swkjbutton.configure(interp, argv, 2);
            }

            break;

        case OPT_FLASH:

            if (argv.length != 2) {
                throw new TclNumArgsException(interp, 2, argv, "");
            }

            break;

        case OPT_INVOKE:

            if (argv.length != 2) {
                throw new TclNumArgsException(interp, 2, argv, "");
            }

            if (!swkjbutton.isEnabled()) {
                return;
            }

            if ((swkjbutton.commandListener.command != null) &&
                    (swkjbutton.commandListener.command.length() != 0)) {
                try {
                    interp.eval(swkjbutton.commandListener.command);
                } catch (TclException tclE) {
                    System.out.println(interp.getResult());
                }
            }

            break;

        default:
            throw new TclRuntimeError("TclIndex.get() error");
        }
    }
}
