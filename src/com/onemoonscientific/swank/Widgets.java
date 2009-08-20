/*

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

import java.lang.*;

import java.util.*;

import javax.swing.*;


public class Widgets {
    

    static Hashtable getWidgetMap(Interp interp) {
    
         WidgetsMap widgetsMap = (WidgetsMap) interp.getAssocData("Widgets");
         if (widgetsMap == null) {
               widgetsMap = new WidgetsMap();
               interp.setAssocData("Widgets",widgetsMap);
         }
         return widgetsMap.getWidgets();
               
    }
    public static boolean exists(Interp interp, String widgetName) {
        
        if (getWidgetMap(interp).get(widgetName) == null) {
            return false;
        } else {
            return true;
        }
    }

    public static void addNewWidget(Interp interp, String widgetName,
        TclObject widgetObject) throws TclException {
        if (exists(interp, widgetName)) {
            throw new TclException(interp,
                "widget \"" + widgetName + "\" already exists");
        }

        if (!widgetName.equals(".")) {
            String parentName = pathParent(interp, widgetName);

            if (!exists(interp, parentName)) {
                throw new TclException(interp,
                    "bad window path name \"" + parentName + "\"");
            }

            SwkWidget parent = (SwkWidget) get(interp, parentName);

            LinkedList children = parent.getChildrenList();

            if (children == null) {
                parent.initChildrenList();
                children = parent.getChildrenList();
            }

            children.add(widgetObject);
        }

        getWidgetMap(interp).put(widgetName, widgetObject);
    }

    public static void removeChild(Interp interp, String widgetName)
        throws TclException {
        TclObject tObj = (TclObject) getWidgetMap(interp).get(widgetName);

        if (tObj == null) {
            return;
        }

        String parentName = pathParent(interp, widgetName);

        if (!exists(interp, parentName)) {
            return;
        }

        SwkWidget parent = (SwkWidget) get(interp, parentName);

        LinkedList children = parent.getChildrenList();

        if (children == null) {
            return;
        }

        children.remove(tObj);
    }

    public static Vector children(Interp interp, String parentName)
        throws TclException {
        SwkWidget parent = (SwkWidget) get(interp, parentName);
        LinkedList children = parent.getChildrenList();
        Vector childrenNames = new Vector();

        if (children == null) {
            return childrenNames;
        }

        ListIterator list = children.listIterator(0);

        while (list.hasNext()) {
            TclObject tObj = (TclObject) list.next();
            SwkWidget swkWidget = (SwkWidget) ReflectObject.get(interp, tObj);
            String widgetName = swkWidget.getName();
            childrenNames.add(widgetName);
        }

        return childrenNames;
    }
    public static void removeWidget(Interp interp, String widgetName) {
          getWidgetMap(interp).remove(widgetName);
    }
    
    public static void remove(Interp interp, String widgetName)
        throws TclException {
        TclObject tObj = (TclObject) getWidgetMap(interp).get(widgetName);

        if (tObj == null) {
            return;
        }

        Object object = (Object) ReflectObject.get(interp, tObj);

        if (object == null) {
            return;
        }

        if (object instanceof Component) {
            Component comp = (Component) object;

            Container container = getContainer(interp, widgetName);

            if (container != null) {
                container.remove(comp);
            }
        }
    }

    public static JViewport getViewport(Component comp)
        throws TclException {
        Container container = comp.getParent();

        while (container != null) {
            if (container instanceof SwkWidget) {
                break;
            } else {
                container = container.getParent();
            }
        }

        if (container == null) {
            return null;
        } else {
            if (container instanceof JScrollPane) {
                return ((JScrollPane) container).getViewport();
            } else {
                return null;
            }
        }
    }

    public static String pathParent(Interp interp, String widgetName)
        throws TclException {
        String masterName = null;
        int lastDot = widgetName.lastIndexOf(".");

        if (lastDot == 0) {
            masterName = ".";
        } else if (lastDot == -1) {
            throw new TclException(interp,
                "bad window path name \"" + widgetName + "\"");
        } else {
            masterName = widgetName.substring(0, lastDot);
        }

        return (masterName);
    }

    public static String parent(Interp interp, String widgetName)
        throws TclException {
        Object object = get(interp, widgetName);
        SwkWidget swkWidget = swankParent(object);

        if (swkWidget != null) {
            return swkWidget.getName();
        } else {
            return "";
        }
    }

    public static SwkWidget swankParent(Object object) {
        Container container = null;

        if (object instanceof Component) {
            container = ((Component) object).getParent();

            while (container != null) {
                if (container instanceof SwkWidget) {
                    break;
                } else {
                    container = container.getParent();
                }
            }
        }

        return (SwkWidget) container;
    }

    public static Object getWidget(Interp interp, String widgetName) {
         return getWidgetMap(interp).get(widgetName);
    }
    // Map a widget name to the Swing widget it represents
    public static Object get(Interp interp, String widgetName)
        throws TclException {
        TclObject tObj = null;

        if (widgetName.equals("any")) {
            Enumeration e = Widgets.getWidgetMap(interp).elements();

            if (e.hasMoreElements()) {
                tObj = (TclObject) e.nextElement();
            }
        } else {
            if (!Widgets.exists(interp,widgetName)) {
                throw new TclException(interp,
                    "bad window path name \"" + widgetName + "\"");
            }

            tObj = (TclObject) Widgets.getWidget(interp,widgetName);
        }

        if (tObj == null) {
            throw new TclException(interp, "no widget for " + widgetName);
        }

        return (Object) ReflectObject.get(interp, tObj);
    }

    // Get the swing Container object for the given window name
    public static Container getContainer(Interp interp, String widgetName)
        throws TclException {
        Object o = get(interp, widgetName);

        return (getContainer(o));
    }

    public static Container getContainer(Object o) {
        Container c;

        if (o instanceof JFrame) {
            c = ((JFrame) o).getContentPane();
        } else if (o instanceof JInternalFrame) {
            c = ((JInternalFrame) o).getContentPane();
        } else if (o instanceof JWindow) {
            c = ((JWindow) o).getContentPane();
        } else {
            c = (Container) o;
        }

        return c;
    }
    public static void relayoutContainer(Container c) {
        Component component = (Component) c;
 
        while (true) {
            if (component == null) {
                break;
            }
            component.repaint();
            if (component instanceof Window) {
                if (component instanceof SwkJFrame) {
                    ((SwkJFrame) component).isPacking = true;
                    ((Window) component).pack();
                } else if (component instanceof SwkJWindow) {
                    ((Window) component).pack();
                } else {
                    ((Window) component).validate();
                }
 
                break;
            }
 
            if (component instanceof JFrame) {
                ((JFrame) component).validate();
                break;
            } else if (component instanceof JWindow) {
                ((JWindow) component).validate();
                break;
            }
            component = ((Component) component).getParent();
        }
    }

    public static Component getFrameOrWindow(Container c) {
        Component component = (Component) c;

        while (true) {
            if (component == null) {
                break;
            }

            if (component instanceof Window) {
                return component;
            }

            if (component instanceof JFrame) {
                return component;
            }

            component = ((Component) component).getParent();
        }

        return component;
    }
}
