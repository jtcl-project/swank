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

import java.lang.*;

import java.util.*;

import javax.swing.*;

public class WinfoCmd implements Command {

    static final private String[] validCmds = {
        "children", "class", "exists", "geometry", "height", "toplevel", "width",
        "x", "y", "parent", "depth", "fpixels", "pixels", "viewable",
        "screenwidth", "screenheight", "reqwidth", "reqheight", "rootx", "rooty",
        "vrootx", "vrooty", "visual", "ismapped", "name"
    };
    static final private int OPT_CHILDREN = 0;
    static final private int OPT_CLASS = 1;
    static final private int OPT_EXISTS = 2;
    static final private int OPT_GEOMETRY = 3;
    static final private int OPT_HEIGHT = 4;
    static final private int OPT_TOPLEVEL = 5;
    static final private int OPT_WIDTH = 6;
    static final private int OPT_X = 7;
    static final private int OPT_Y = 8;
    static final private int OPT_PARENT = 9;
    static final private int OPT_DEPTH = 10;
    static final private int OPT_FPIXELS = 11;
    static final private int OPT_PIXELS = 12;
    static final private int OPT_VIEWABLE = 13;
    static final private int OPT_SCREENWIDTH = 14;
    static final private int OPT_SCREENHEIGHT = 15;
    static final private int OPT_REQWIDTH = 16;
    static final private int OPT_REQHEIGHT = 17;
    static final private int OPT_ROOTX = 18;
    static final private int OPT_ROOTY = 19;
    static final private int OPT_VROOTX = 20;
    static final private int OPT_VROOTY = 21;
    static final private int OPT_VISUAL = 22;
    static final private int OPT_ISMAPPED = 23;
    static final private int OPT_NAME = 24;
    Interp interp;

    public void cmdProc(Interp interp, TclObject[] argv)
            throws TclException {
        int i;
        int index = 0;
        this.interp = interp;

        String checkTL = null;
        SwkWidget widget = null;

        if (argv.length < 3) {
            throw new TclNumArgsException(interp, 1, argv,
                    "option window ?arg arg ...?");
        }

        int opt = TclIndex.get(interp, argv[1], validCmds, "option", 0);
        TclObject tObj = (TclObject) Widgets.getWidget(interp, argv[2].toString());

        if (opt == OPT_EXISTS) {
            if (tObj == null) {
                interp.setResult("0");
            } else {
                interp.setResult("1");
            }

            return;
        } else {
            if (tObj == null) {
                throw new TclException(interp,
                        "bad window path name \"" + argv[2].toString() + "\"");
            }
        }

        final Object object = (Object) ReflectObject.get(interp, tObj);
        Rectangle rectangle = null;

        switch (opt) {
            case OPT_CHILDREN:
                String widgetName = argv[2].toString();
                getChildrenByName(interp, widgetName);

                break;

            case OPT_PARENT:
                widgetName = argv[2].toString();

                if (!Widgets.exists(interp, widgetName)) {
                    throw new TclException(interp,
                            "bad window path name \"" + widgetName + "\"");
                }

                interp.setResult(Widgets.pathParent(interp, widgetName));

                break;

            case OPT_CLASS:
                widget = (SwkWidget) ReflectObject.get(interp, tObj);
                interp.setResult(widget.getClassName());

                break;

            case OPT_GEOMETRY:
                widgetName = argv[2].toString();

                if (!Widgets.exists(interp, widgetName)) {
                    throw new TclException(interp,
                            "bad window path name \"" + widgetName + "\"");
                }

                rectangle = getGeometry(interp, object, argv);
                interp.setResult(rectangle.width + "x" + rectangle.height + "+"
                        + rectangle.x + "+" + rectangle.y);

                break;

            case OPT_HEIGHT:
                rectangle = getGeometry(interp, object, argv);
                interp.setResult(rectangle.height);

                break;

            case OPT_DEPTH:

                int pixelSize = (new Depth()).exec(object);
                interp.setResult(pixelSize);

                break;

            case OPT_TOPLEVEL: {
                widgetName = argv[2].toString().intern();
                index = widgetName.indexOf(".", 1);

                if (index < 0) {
                    checkTL = widgetName;
                } else {
                    checkTL = widgetName.substring(0, index);
                }

                TclObject tObj2 = (TclObject) Widgets.getWidget(interp, checkTL);

                if (tObj2 == null) {
                    throw new TclException(interp,
                            "bad window path name \"" + checkTL + "\"");
                }

                Object object2 = ReflectObject.get(interp, tObj2);

                if (object2 == null) {
                    throw new TclException(interp,
                            "bad window path name \"" + checkTL + "\"");
                }

                if (object2 instanceof Window) {
                    interp.setResult(checkTL);
                } else {
                    interp.setResult(".");
                }

                break;
            }

            case OPT_WIDTH:
                rectangle = getGeometry(interp, object, argv);
                interp.setResult(rectangle.width);

                break;

            case OPT_X:
                rectangle = getGeometry(interp, object, argv);
                interp.setResult(rectangle.x);

                break;

            case OPT_Y:
                rectangle = getGeometry(interp, object, argv);
                interp.setResult(rectangle.y);

                break;

            case OPT_FPIXELS:

                if (argv.length != 4) {
                    throw new TclNumArgsException(interp, 2, argv,
                            "option ?arg arg ...?");
                }

                interp.setResult(SwankUtil.getTkSizeD(interp, (Component) object,
                        argv[3]));

                break;

            case OPT_PIXELS:

                if (argv.length != 4) {
                    throw new TclNumArgsException(interp, 2, argv,
                            "option ?arg arg ...?");
                }

                interp.setResult(SwankUtil.getTkSize(interp, (Component) object,
                        argv[3]));

                break;

            case OPT_VIEWABLE:

                Winfo winfo = new Winfo();
                winfo.exec(object);
                interp.setResult(winfo.viewable);

                break;

            case OPT_SCREENWIDTH:
                interp.setResult(((Component) object).getToolkit().getScreenSize().width);

                break;

            case OPT_SCREENHEIGHT:
                interp.setResult(((Component) object).getToolkit().getScreenSize().height);

                break;

            case OPT_REQWIDTH:
                winfo = new Winfo();
                winfo.exec(object);
                interp.setResult(winfo.preferredSize.width);

                break;

            case OPT_REQHEIGHT:
                winfo = new Winfo();
                winfo.exec(object);
                interp.setResult(winfo.preferredSize.height);

                break;

            case OPT_ROOTX:
                winfo = new Winfo();
                winfo.exec(object);

                if (!winfo.showing) {
                    throw new TclException(interp, "component is not showing");
                }

                interp.setResult(winfo.locationOnScreen.x);

                break;

            case OPT_ROOTY:
                winfo = new Winfo();
                winfo.exec(object);

                if (!winfo.showing) {
                    throw new TclException(interp, "component is not showing");
                }

                interp.setResult(winfo.locationOnScreen.y);

                break;

            case OPT_VROOTX:
                winfo = new Winfo();
                winfo.exec(object);
                /*
                if (!winfo.showing) {
                throw new TclException(interp, "component is not showing");
                }

                interp.setResult(winfo.locationOnScreen.x);
                 */
                interp.setResult(0);
                break;

            case OPT_VROOTY:
                winfo = new Winfo();
                winfo.exec(object);
                /*
                if (!winfo.showing) {
                throw new TclException(interp, "component is not showing");
                }

                interp.setResult(winfo.locationOnScreen.y);
                 */
                interp.setResult(0);

                break;

            case OPT_VISUAL:

                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv,
                            "option ?arg arg ...?");
                }

                if (!Widgets.exists(interp, argv[2].toString())) {
                    throw new TclException(interp,
                            "bad window path name \"" + argv[2].toString() + "\"");
                }

                interp.setResult("truecolor");

                break;

            case OPT_ISMAPPED:
                winfo = new Winfo();
                winfo.exec(object);
                interp.setResult(winfo.isMapped);

                break;

            case OPT_NAME:

                int lastDot = argv[2].toString().lastIndexOf('.');

                if (lastDot == -1) {
                    throw new TclException(interp,
                            "bad window path name \"" + argv[2].toString() + "\"");
                }

                interp.setResult(argv[2].toString().substring(lastDot + 1));

                break;

            default:
        }
    }

    static void getChildrenByName(Interp interp, String parentName)
            throws TclException {
        Vector children = Widgets.children(interp, parentName);
        TclObject list = TclList.newInstance();
        for (int i = 0; i < children.size(); i++) {
            String childName = (String) children.elementAt(i);
            TclList.append(interp, list, TclString.newInstance(childName));
        }
        interp.setResult(list);
    }

    final void getChildren(final Interp interp, final Component component, final TclObject list)
            throws TclException {
        final ArrayList<String> childList = (new GetChildren()).exec(component);
        for (String compName : childList) {
            TclList.append(interp, list, TclString.newInstance(compName));
        }
    }

    class GetChildren extends GetValueOnEventThread {

        Component comp = null;
        ArrayList<String> childList = new ArrayList<String>();

        ArrayList<String> exec(final Component comp)
                throws TclException {
            this.comp = comp;
            execOnThread();
            return childList;
        }

        public void run() {
            getChildren(comp);
        }

        void getChildren(Component component) {
            Container master = null;
            if (component instanceof JFrame) {
                master = ((JFrame) component).getContentPane();
            } else if (component instanceof JWindow) {
                master = ((JWindow) component).getContentPane();
            } else if (component instanceof JInternalFrame) {
                master = ((JInternalFrame) component).getContentPane();
            } else if (component instanceof Container) {
                master = (Container) component;
            } else {
                return;
            }

            String compName = null;

            Component[] comps = master.getComponents();

            for (int i = 0; i < comps.length; i++) {
                compName = comps[i].getName();

                if ((compName != null) && (compName.length() != 0)) {
                    childList.add(compName);
                }
            }

            for (int i = 0; i < comps.length; i++) {
                getChildren(comps[i]);
            }
        }
    }

    public Rectangle getGeometry(Interp interp, Object object, String widgetName) {
        Point wLocation = null;
        Point pLocation = null;
        Point location;
        Dimension size;
        Rectangle rectangle = null;

        try {
            if (object instanceof JFrame) {
                Window window = (Window) object;
                size = ((JFrame) window).getRootPane().getSize();
                wLocation = window.getLocationOnScreen();
                location = window.getLocation();
            } else if (object instanceof Window) {
                Window window = (Window) object;
                size = window.getSize();
                wLocation = window.getLocationOnScreen();
                location = window.getLocation();
            } else if (object instanceof Component) {
                Component comp = (Component) object;
                String parent = Widgets.pathParent(interp, widgetName);
                TclObject pObj = (TclObject) Widgets.getWidget(interp, parent);
                Object pObject = (Object) ReflectObject.get(interp, pObj);

                if (pObject instanceof Window) {
                    Window pWindow = (Window) pObject;
                    pLocation = Widgets.getContainer(pWindow).getLocationOnScreen();
                } else {
                    Component pComp = (Component) pObject;
                    pLocation = Widgets.getContainer(pComp).getLocationOnScreen();
                }

                size = comp.getSize();
                wLocation = comp.getLocationOnScreen();
                location = comp.getLocation();
            } else {
                throw new TclException(interp,
                        "invalid object type \"" + widgetName + "\"");
            }

            if ((pLocation != null) && (wLocation != null)) {
                location = new Point(wLocation.x - pLocation.x,
                        wLocation.y - pLocation.y);
            }

            rectangle = new Rectangle();
            rectangle.x = location.x;
            rectangle.y = location.y;
            rectangle.width = size.width;
            rectangle.height = size.height;
        } catch (TclException tclE) {
            interp.backgroundError();
        }

        return rectangle;
    }

    Rectangle getGeometry(final Interp interp, final Object object,
            final TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "window");
        }

        String widgetName = argv[2].toString().intern();

        if (!(object instanceof JFrame) && !(object instanceof JWindow)
                && !(object instanceof Component)) {
            throw new TclException(interp,
                    "invalid object type \"" + widgetName + "\"");
        }

        Rectangle rectangle = (new GeometryGet()).exec(object, widgetName);

        if (rectangle == null) {
            throw new TclException(interp,
                    "Can't get geometry for object \"" + widgetName + "\"");
        }

        return rectangle;
    }

    class GeometryGet extends GetValueOnEventThread {

        Object object = null;
        Rectangle rectangle = null;
        String widgetName = null;

        Rectangle exec(final Object object, final String widgetName)
                throws TclException {
            this.object = object;
            this.widgetName = widgetName;
            execOnThread();

            return rectangle;
        }

        public void run() {
            Dimension size = null;
            Point location = null;
            JRootPane jRoot;

            if (object instanceof JWindow) {
                JWindow window = (JWindow) object;
                jRoot = window.getRootPane();
                size = jRoot.getSize();
                location = window.getLocation();
            }

            rectangle = getGeometry(interp, object, widgetName);
            rectangle.x = location.x;
            rectangle.y = location.y;
            rectangle.width = size.width;
            rectangle.height = size.height;

            if (rectangle.x < 0) {
                rectangle.x = 0;
            }

            if (rectangle.y < 0) {
                rectangle.y = 0;
            }

            if (rectangle.width < 0) {
                rectangle.width = 0;
            }

            if (rectangle.height < 0) {
                rectangle.height = 0;
            }
        }
    }

    class Depth extends GetValueOnEventThread {

        Object object = null;
        int pixelSize;

        int exec(final Object object) throws TclException {
            this.object = object;
            execOnThread();

            return pixelSize;
        }

        public void run() {
            pixelSize = ((Component) object).getColorModel().getPixelSize();
        }
    }

    class Winfo extends GetValueOnEventThread {

        Object object = null;
        boolean viewable = false;
        boolean showing = false;
        Dimension preferredSize = null;
        Point locationOnScreen = null;
        boolean isMapped = false;

        void exec(final Object object) throws TclException {
            this.object = object;
            execOnThread();
        }

        public void run() {
            viewable = ((Component) object).isVisible();
            preferredSize = ((Component) object).getPreferredSize();
            showing = ((Component) object).isShowing();
            locationOnScreen = ((Component) object).getLocationOnScreen();

            if ((object instanceof Frame)
                    && ((((Frame) object).getExtendedState() & Frame.ICONIFIED) == Frame.ICONIFIED)) {
                isMapped = false;
            } else {
                isMapped = ((Component) object).isDisplayable();
            }
        }
    }
}
