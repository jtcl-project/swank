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

import java.awt.*;

import java.io.*;

import java.lang.*;

import java.util.*;

import javax.swing.*;


public class DestroyCmd implements Command {
    public void cmdProc(final Interp interp, final TclObject[] argv)
        throws TclException {
        String name;
        TclObject tObj2;
        Object object2;
        int index;
        String checkTL;

        if (argv.length < 2) {
            return;
        }

        final String[] widgetNames = new String[argv.length - 1];

        for (int j = 1; j < argv.length; j++) {
            widgetNames[j - 1] = argv[j].toString().intern();
        }

        (new GetValueOnEventThread() {
                public void run() {
                    destroyWidgets(interp, widgetNames);
                }
            }).execOnThread();
    }

    public static void destroyWidgets(final Interp interp,
        final String[] widgetNames) {
        for (int j = 0; j < widgetNames.length; j++) {
            try {
                destroyWidget(interp, widgetNames[j]);
            } catch (TclException tclE) {
                interp.backgroundError();
            }
        }
    }

    public static void destroyWidget(final Interp interp, final String name)
        throws TclException {
        TclObject tObj = (TclObject) Widgets.theWidgets.get(name);

        if (tObj == null) {
            return;
        }

        Vector childrenNames = Widgets.children(interp, name);

        for (int k = 0; k < childrenNames.size(); k++) {
            destroyWidget(interp, (String) childrenNames.elementAt(k));
        }

        Widgets.removeChild(interp, name);
        Widgets.theWidgets.remove(name);
        interp.deleteCommand(name);

        Object object = ReflectObject.get(interp, tObj);

        if (object == null) {
            return;
        }

        if (object instanceof JComponent) {
            Container container = ((JComponent) object).getParent();

            if (container != null) {
                container.remove((JComponent) object);

                while (true) {
                    if (container == null) {
                        break;
                    }

                    if (container instanceof Window) {
                        // ((Window) container).pack();
                        //((Window) container).repaint();
                        break;
                    }

                    if (container instanceof JFrame) {
                        //((JFrame) container).pack();
                        //((JFrame) container).repaint();
                        break;
                    }

                    container = ((Container) container).getParent();
                }
            }
        }

        if (object instanceof Window) {
            ((Window) object).dispose();
        }

        if (object instanceof SwkWidget) {
            ((SwkWidget) object).close();
        }

        tObj.release();
        tObj = null;
    }
}
