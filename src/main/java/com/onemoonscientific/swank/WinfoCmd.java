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

public class WinfoCmd implements Command {

    static final private String[] validCmds = {
        "children", "class", "containing", "depth", "exists", "fpixels", "geometry", "height", "ismapped",
        "manager", "mousebuttons", "name", "parent", "pixels", "pointerx", "pointerxy", "pointery", "reqheight", "reqwidth",
        "rootx", "rooty", "screen", "screenheight", "screenwidth", "toplevel", "viewable", "visual", "vrootx", "vrooty", "width",
        "x", "y",};
    static final private int OPT_CHILDREN = 0;
    static final private int OPT_CLASS = 1;
    static final private int OPT_CONTAINING = 2;
    static final private int OPT_DEPTH = 3;
    static final private int OPT_EXISTS = 4;
    static final private int OPT_FPIXELS = 5;
    static final private int OPT_GEOMETRY = 6;
    static final private int OPT_HEIGHT = 7;
    static final private int OPT_ISMAPPED = 8;
    static final private int OPT_MANAGER = 9;
    static final private int OPT_MOUSEBUTTONS = 10;
    static final private int OPT_NAME = 11;
    static final private int OPT_PARENT = 12;
    static final private int OPT_PIXELS = 13;
    static final private int OPT_POINTERX = 14;
    static final private int OPT_POINTERXY = 15;
    static final private int OPT_POINTERY = 16;
    static final private int OPT_REQHEIGHT = 17;
    static final private int OPT_REQWIDTH = 18;
    static final private int OPT_ROOTX = 19;
    static final private int OPT_ROOTY = 20;
    static final private int OPT_SCREEN = 21;
    static final private int OPT_SCREENHEIGHT = 22;
    static final private int OPT_SCREENWIDTH = 23;
    static final private int OPT_TOPLEVEL = 24;
    static final private int OPT_VIEWABLE = 25;
    static final private int OPT_VISUAL = 26;
    static final private int OPT_VROOTX = 27;
    static final private int OPT_VROOTY = 28;
    static final private int OPT_WIDTH = 29;
    static final private int OPT_X = 30;
    static final private int OPT_Y = 31;
    Interp interp;

    public void cmdProc(Interp interp, TclObject[] argv)
            throws TclException {
        int i;
        int index = 0;
        this.interp = interp;

        String checkTL = null;
        SwkWidget widget = null;

        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv,
                    "option ?arg?");
        }

        int opt = TclIndex.get(interp, argv[1], validCmds, "option", 0);

        Rectangle rectangle = null;

        switch (opt) {
            case OPT_CHILDREN: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }
                String widgetName = argv[2].toString();
                getChildrenByName(interp, widgetName);
                break;
            }

            case OPT_EXISTS: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }
                String widgetName = argv[2].toString();
                if (windowExists(interp, widgetName)) {
                    interp.setResult(TclBoolean.newInstance(true));
                } else {
                    interp.setResult(TclBoolean.newInstance(false));
                }
                break;
            }
            case OPT_PARENT: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }
                String widgetName = argv[2].toString();

                if (!Widgets.exists(interp, widgetName)) {
                    throw new TclException(interp,
                            "bad window path name \"" + widgetName + "\"");
                }

                interp.setResult(Widgets.pathParent(interp, widgetName));

                break;

            }
            case OPT_CLASS: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }
                Component component = getWindow(interp, argv[2].toString());
                interp.setResult(((SwkWidget) component).getClassName());

                break;

            }
            case OPT_GEOMETRY: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }
                Component component = getWindow(interp, argv[2].toString());
                rectangle = getGeometry(interp, component, argv);
                interp.setResult(rectangle.width + "x" + rectangle.height + "+"
                        + rectangle.x + "+" + rectangle.y);

                break;

            }
            case OPT_HEIGHT: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }
                Component component = getWindow(interp, argv[2].toString());
                rectangle = getGeometry(interp, component, argv);
                interp.setResult(rectangle.height);

                break;

            }
            case OPT_DEPTH: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }
                Component component = getWindow(interp, argv[2].toString());

                int pixelSize = (new Depth()).exec(component);
                interp.setResult(pixelSize);

                break;

            }
            case OPT_TOPLEVEL: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }
                String widgetName = argv[2].toString().intern();
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
            case OPT_VISUAL: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }
                Component component = getWindow(interp, argv[2].toString());
                interp.setResult("truecolor");
                break;


            }
            case OPT_WIDTH: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }
                Component component = getWindow(interp, argv[2].toString());
                rectangle = getGeometry(interp, component, argv);
                interp.setResult(rectangle.width);

                break;

            }
            case OPT_X: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }
                Component component = getWindow(interp, argv[2].toString());
                rectangle = getGeometry(interp, component, argv);
                interp.setResult(rectangle.x);

                break;

            }
            case OPT_Y: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }
                Component component = getWindow(interp, argv[2].toString());
                rectangle = getGeometry(interp, component, argv);
                interp.setResult(rectangle.y);

                break;

            }
            case OPT_FPIXELS: {

                if (argv.length != 4) {
                    throw new TclNumArgsException(interp, 2, argv,
                            "option ?arg arg ...?");
                }
                Component component = getWindow(interp, argv[2].toString());
                interp.setResult(SwankUtil.getTkSizeD(interp, component, argv[3]));

                break;

            }
            case OPT_PIXELS: {

                if (argv.length != 4) {
                    throw new TclNumArgsException(interp, 2, argv,
                            "option ?arg arg ...?");
                }
                Component component = getWindow(interp, argv[2].toString());
                interp.setResult(SwankUtil.getTkSize(interp, component, argv[3]));

                break;

            }
            case OPT_VIEWABLE: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }
                Component component = getWindow(interp, argv[2].toString());
                Winfo winfo = new Winfo();
                winfo.exec(component);
                interp.setResult(winfo.viewable);

                break;

            }
            case OPT_SCREENWIDTH: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }
                Component component = getWindow(interp, argv[2].toString());
                interp.setResult(component.getToolkit().getScreenSize().width);

                break;

            }
            case OPT_SCREENHEIGHT: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }
                Component component = getWindow(interp, argv[2].toString());
                interp.setResult(component.getToolkit().getScreenSize().width);

                break;

            }
           case OPT_MOUSEBUTTONS: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }
                Component component = getWindow(interp, argv[2].toString());
                SwkMouseInfo mouseInfo = new SwkMouseInfo();
                mouseInfo.exec(component);
                interp.setResult(mouseInfo.nButtons);

                break;

            }
          case OPT_POINTERX: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }
                Component component = getWindow(interp, argv[2].toString());
                SwkMouseInfo mouseInfo = new SwkMouseInfo();
                mouseInfo.exec(component);
                interp.setResult(mouseInfo.x);

                break;

            }
           case OPT_POINTERXY: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }
                Component component = getWindow(interp, argv[2].toString());
                SwkMouseInfo mouseInfo = new SwkMouseInfo();
                mouseInfo.exec(component);
                TclObject result = TclList.newInstance();
                TclList.append(interp,result,TclInteger.newInstance(mouseInfo.x));
                TclList.append(interp,result,TclInteger.newInstance(mouseInfo.y));
                interp.setResult(result);

                break;

            }
           case OPT_POINTERY: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }
                Component component = getWindow(interp, argv[2].toString());
                SwkMouseInfo mouseInfo = new SwkMouseInfo();
                mouseInfo.exec(component);
                interp.setResult(mouseInfo.y);

                break;

            }
            case OPT_REQWIDTH: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }
                Component component = getWindow(interp, argv[2].toString());
                Winfo winfo = new Winfo();
                winfo.exec(component);
                if (winfo.preferredSize == null) {
                    throw new TclException(interp, "Couldn't get width for \"" + argv[2].toString() + "\"");
                }
                interp.setResult(winfo.preferredSize.width);

                break;

            }
            case OPT_REQHEIGHT: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }
                Component component = getWindow(interp, argv[2].toString());
                Winfo winfo = new Winfo();
                winfo.exec(component);
                if (winfo.preferredSize == null) {
                    throw new TclException(interp, "Couldn't get height for \"" + argv[2].toString() + "\"");
                }
                interp.setResult(winfo.preferredSize.height);

                break;

            }
            case OPT_ROOTX: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }
                Component component = getWindow(interp, argv[2].toString());
                Winfo winfo = new Winfo();
                winfo.exec(component);

                if (!winfo.showing) {
                    throw new TclException(interp, "component is not showing");
                }

                interp.setResult(winfo.locationOnScreen.x);

                break;

            }
            case OPT_ROOTY: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }
                Component component = getWindow(interp, argv[2].toString());
                Winfo winfo = new Winfo();
                winfo.exec(component);

                if (!winfo.showing) {
                    throw new TclException(interp, "component is not showing");
                }

                interp.setResult(winfo.locationOnScreen.y);

                break;

            }
          case OPT_SCREEN: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }
                Component component = getWindow(interp, argv[2].toString());
                Winfo winfo = new Winfo();
                winfo.exec(component);

                if (!winfo.showing) {
                    throw new TclException(interp, "component is not showing");
                }

                interp.setResult(winfo.screenName);

                break;

            }
            case OPT_VROOTX: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }
                Component component = getWindow(interp, argv[2].toString());
                Winfo winfo = new Winfo();
                winfo.exec(component);
                /*
                if (!winfo.showing) {
                throw new TclException(interp, "component is not showing");
                }

                interp.setResult(winfo.locationOnScreen.x);
                 */
                interp.setResult(0);
                break;

            }
            case OPT_VROOTY: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }
                Component component = getWindow(interp, argv[2].toString());
                Winfo winfo = new Winfo();
                winfo.exec(component);
                /*
                if (!winfo.showing) {
                throw new TclException(interp, "component is not showing");
                }

                interp.setResult(winfo.locationOnScreen.y);
                 */
                interp.setResult(0);

                break;

            }
            case OPT_ISMAPPED: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }
                Component component = getWindow(interp, argv[2].toString());
                Winfo winfo = new Winfo();
                winfo.exec(component);
                interp.setResult(winfo.isMapped);

                break;

            }
            case OPT_NAME: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }

                int lastDot = argv[2].toString().lastIndexOf('.');

                if (lastDot == -1) {
                    throw new TclException(interp,
                            "bad window path name \"" + argv[2].toString() + "\"");
                }

                interp.setResult(argv[2].toString().substring(lastDot + 1));

                break;
            }
            case OPT_MANAGER: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }
                Component component = getWindow(interp, argv[2].toString());
                String manager = (new Manager()).exec(component);
                interp.setResult(manager);

                break;
            }
            default: {
                throw new TclException(interp, "Invalid option \"" + argv[1].toString() + "\"");
            }
        }
    }

    boolean windowExists(final Interp interp, final String windowName) {
        TclObject tObj = (TclObject) Widgets.getWidget(interp, windowName);
        return (tObj != null);
    }

    Component getWindow(final Interp interp, final String windowName) throws TclException {
        TclObject tObj = (TclObject) Widgets.getWidget(interp, windowName);
        if (tObj == null) {
            throw new TclException(interp,
                    "bad window path name \"" + windowName + "\"");
        }
        final Component component = (Component) ReflectObject.get(interp, tObj);
        return component;
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

    static class GetChildren extends GetValueOnEventThread {

        Component comp = null;
        ArrayList<String> childList = new ArrayList<String>();

        ArrayList<String> exec(final Component comp)
                throws TclException {
            this.comp = comp;
            execOnThread();
            return childList;
        }

        @Override
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
                size = ((JFrame) object).getRootPane().getSize();
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
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

        @Override
        public void run() {
            JRootPane jRoot;
            if (object instanceof JWindow) {
                JWindow window = (JWindow) object;
                jRoot = window.getRootPane();
            }

            rectangle = getGeometry(interp, object, widgetName);
            /*
            rectangle.x = location.x;
            rectangle.y = location.y;
            rectangle.width = size.width;
            rectangle.height = size.height;
             */
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

    static class Depth extends GetValueOnEventThread {

        Object object = null;
        int pixelSize;

        int exec(final Object object) throws TclException {
            this.object = object;
            execOnThread();

            return pixelSize;
        }

        @Override
        public void run() {
            pixelSize = ((Component) object).getColorModel().getPixelSize();
        }
    }

    static class SwkMouseInfo extends GetValueOnEventThread {
        Component component = null;
        int nButtons = 1;
        int x = 0;
        int y = 0;
        String graphicsDevice = "";

        void exec(final Component component) {
            this.component = component;
            execOnThread();
        }

        @Override
        public void run() {
            nButtons = MouseInfo.getNumberOfButtons();
            System.out.println(nButtons);
            PointerInfo pointerInfo = MouseInfo.getPointerInfo();
            Point pt = pointerInfo.getLocation();
            x = pt.x;
            y = pt.y;
            // fixme should check if device with pointer is same as device with window
            graphicsDevice = pointerInfo.getDevice().getIDstring();
        }
    }

    static class Winfo extends GetValueOnEventThread {

        Object object = null;
        boolean viewable = false;
        boolean showing = false;
        Dimension preferredSize = null;
        Point locationOnScreen = null;
        boolean isMapped = false;
        String screenName = "";

        void exec(final Object object) throws TclException {
            this.object = object;
            execOnThread();
        }

        @Override
        public void run() {
            Point wLocation = null;
            Point pLocation = null;
            Dimension size;
            Component comp = (Component) object;
          if (object instanceof JFrame) {
                Window window = (Window) object;
                locationOnScreen = window.getLocation();
                preferredSize = ((JFrame) window).getRootPane().getPreferredSize();
            } else if (object instanceof Window) {
                Window window = (Window) object;
                locationOnScreen = window.getLocation();
                preferredSize = window.getPreferredSize();
            } else {
                locationOnScreen = comp.getLocation();
                preferredSize = comp.getPreferredSize();
            }
            viewable = comp.isVisible();
            showing = comp.isShowing();

            if ((object instanceof Frame)
                    && ((((Frame) object).getExtendedState() & Frame.ICONIFIED) == Frame.ICONIFIED)) {
                isMapped = false;
            } else {
                isMapped = comp.isDisplayable();
            }
            screenName = comp.getGraphicsConfiguration().getDevice().getIDstring();

        }
    }

    static class Manager extends GetValueOnEventThread {

        Component component = null;
        String manager = "";

        String exec(final Object object) {
            component = (Component) object;
            execOnThread();
            return manager;
        }

        @Override
        public void run() {
            Container master = Widgets.getMaster(component, true);
            if (master != null) {
                LayoutManager layout = master.getLayout();
                if (layout instanceof com.onemoonscientific.swank.PackerLayout) {
                    manager = "pack";
                } else if (layout instanceof SwkGridBagLayout) {
                    manager = "grid";
                } else if (layout instanceof com.onemoonscientific.swank.PlacerLayout) {
                    manager = "place";
                } else {
                    manager = "";
                }
            }
        }
    }
}
