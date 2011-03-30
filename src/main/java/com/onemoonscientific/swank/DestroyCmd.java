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
import java.util.*;
import javax.swing.*;

/**
 *
 * @author brucejohnson
 */
public class DestroyCmd implements Command {

    /**
     *
     * @param interp
     * @param argv
     * @throws TclException
     */
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
        destroyWidgets(interp,widgetNames);

    }

    private static void destroyWidgets(final Interp interp, final String[] widgetNames) throws TclException {
        final HashSet<String> childrenSet = new HashSet<String>();
        for (int j = 0; j < widgetNames.length; j++) {
                getAllChildren(interp, widgetNames[j], childrenSet);
        }
        final HashSet objectSet = new HashSet();
        for (String childName: childrenSet) {
            TclObject tObj = (TclObject) Widgets.getWidget(interp, childName);
            if (tObj == null) {
                return;
            }
            Object object = ReflectObject.get(interp, tObj);
            if (object != null) {
                Widgets.removeChild(interp, childName);
                Widgets.removeWidget(interp, childName);
                interp.deleteCommand(childName);
                objectSet.add(object);
                if (object instanceof SwkWidget) {
                    ((SwkWidget) object).close();
                }
            }
            tObj.release();
            tObj = null;
        }
        final HashSet<Container> topLevels = new HashSet<Container>();
        (new GetValueOnEventThread() {
            @Override
            public void run() {
                for (Object object: objectSet) {
                    destroyObject(object,topLevels);
                }
            }
        }).execOnThread();


        for (Container topLevel : topLevels) {
            Widgets.relayoutContainer(topLevel);
        }
    }

    private static void getAllChildren(final Interp interp, final String name, final HashSet childrenSet) {
        TclObject tObj = (TclObject) Widgets.getWidget(interp, name);
        if (tObj == null) {
            return;
        }
        childrenSet.add(name);
        Vector childrenNames = new Vector();
        try {
            childrenNames = Widgets.children(interp, name);
        } catch (TclException tclE) {
        }
        for (int k = 0; k < childrenNames.size(); k++) {
            String childName =  (String) childrenNames.elementAt(k);
            childrenSet.add(childName);
            getAllChildren(interp, childName,childrenSet);
        }
    }


    private static void destroyObject(Object object, HashSet topLevels) {


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
                        if (topLevels != null) {
                            topLevels.add(container);
                        }
                        break;
                    }

                    if (container instanceof JFrame) {
                        if (topLevels != null) {
                            topLevels.add(container);
                        }
                        break;
                    }

                    container = ((Container) container).getParent();
                }
            }
        }

        if (object instanceof Window) {
            ((Window) object).dispose();
            if (topLevels != null) {
                topLevels.remove((Container) object);
            }
        }
    }
}
