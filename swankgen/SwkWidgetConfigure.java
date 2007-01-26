set body {/*
 * Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file "LICENSE" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 *
*/
package com.onemoonscientific.swank$subdir;

import tcl.lang.*;


import java.io.*;
import java.util.*;
import java.lang.*;
import java.net.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.tree.*;
import javax.swing.event.*;

$specialImports

/** Contains methods for configuring the ${widgetType} widgets. */
class ${widgetType}Configure {
    /** Method configures the widget objects (implements the widgets "configure" subcommand.
     * @param interp The interpreter containing the command.
     * @param ${widgetVar} The object to be configured.
     * @param argv Argument array contains configuration options.
     * @param start Start processing configuration options at this element.
     * @throws TclException Exception thrown if an error occurs while processing options
     */
    $configOptions
    $configOPTs
    public static void configureET(Interp interp, ${widgetType} ${widgetVar},
        TclObject[] argv, int start) throws TclException {
        int i;

        if (argv.length <= start) {
            return;
        }

        ResourceObject ro = null;

        for (i = start; i < argv.length; i += 2) {
            if ((i + 1) >= argv.length) {
                throw new TclException(interp,
                    "value for \"" + argv[i].toString() + "\" missing");
            }

            ro = (ResourceObject) ${widgetType}.resourceDB.get(argv[i].toString());

            if (ro == null) {
                throw new TclException(interp,
                    "unknown option \"" + argv[i].toString() + "\"");
            }

            if (ro.defaultVal == null) {
                ro.defaultVal = ${widgetType}Configure.jget(interp, ${widgetVar},
                        argv[i]);
            }
                 $configCASEs

        }
    }
    /** Returns current value for configurable options for the  ${widgetType} widget.
     * @param interp The interpreter containing the widgets command.
     * @param ${widgetVar} The widget object for which the request is made.
     * @param tclObject The object representing the option for which a value is requested.
     * @throws TclException Exception thrown if an error occurs getting the value.
     */
    public static String jget(Interp interp, ${widgetType} ${widgetVar}, TclObject arg) throws TclException {
            $getCASEs
        throw new TclException(interp, "unknown option \"" + arg.toString() + "\"");

    }
    /** Adds objects to the ${widgetType} widget.
     * @param interp The interpreter containing the widgets command.
     * @param ${widgetVar} The widget object to to which the object will be added.
     * @param tclObject The object to add to widget.
     * @throws TclException Exception thrown if an error occurs while adding object
     */
    public static void jadd(Interp interp, ${widgetType} ${widgetVar},
        TclObject tclObject) throws TclException {
        int i;
        TclObject tObj = (TclObject) Widgets.theWidgets.get(tclObject.toString());

        if (tObj != null) {
            Object object = ReflectObject.get(interp, tObj);
            $addBody

        }
    }
}
}
