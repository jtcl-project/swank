/*
 *
 *
 * Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file \"LICENSE\" for information on usage and redistribution
 * of this file.
 * IN NO EVENT SHALL THE AUTHORS OR DISTRIBUTORS BE LIABLE TO
 * ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR
 * CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OF THIS
 * SOFTWARE, ITS DOCUMENTATION, OR ANY DERIVATIVES THEREOF,
 * EVEN IF THE AUTHORS HAVE BEEN ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * THE AUTHORS AND DISTRIBUTORS SPECIFICALLY DISCLAIM ANY
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, AND NON-INFRINGEMENT.  THIS SOFTWARE
 * IS PROVIDED ON AN "AS IS" BASIS, AND THE AUTHORS AND
 * DISTRIBUTORS HAVE NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 *
 */
package com.onemoonscientific.swank;

import tcl.lang.*;
import tcl.pkg.java.ReflectObject;

import java.awt.*;

import java.io.*;

import java.util.*;

import javax.swing.*;

public class LowerCmd implements Command {

    public void cmdProc(Interp interp, TclObject[] argv)
            throws TclException {
        int i;

        if ((argv.length < 2) || (argv.length > 3)) {
            throw new TclNumArgsException(interp, 1, argv, "?belowThis?");
        }

        TclObject tObj = (TclObject) Widgets.getWidget(interp, argv[1].toString());

        if (tObj == null) {
            throw new TclException(interp,
                    "bad window path name \"" + argv[2].toString() + "\"");
        }

        SwkWidget swkwidget = (SwkWidget) ReflectObject.get(interp, tObj);
        Component component = (Component) swkwidget;
        lower(component);
    }

    public static void lower(final Component component) {
        (new UpdateOnEventThread() {

            public void run() {
                if (component instanceof JFrame) {
                    ((JFrame) component).toBack();
                } else if (component instanceof JWindow) {
                    ((JWindow) component).toBack();
                } else if (component instanceof JInternalFrame) {
                    ((JInternalFrame) component).toBack();
                }
            }
        }).execOnThread();
    }
}
