package com.onemoonscientific.swank;

import tcl.lang.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.JPopupMenu;

public class EventRecorder implements AWTEventListener {

     ArrayList eventList = new ArrayList();
     long startTime = -1;
     Point lastLoc = null;
     String lastCompName = null;

    public  void start() {
 
        startTime = -1;
        eventList.clear();

        long flags = AWTEvent.MOUSE_EVENT_MASK
                | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.KEY_EVENT_MASK;
        Toolkit.getDefaultToolkit().addAWTEventListener(this, flags);
    }

    public  void stop() {
            Toolkit.getDefaultToolkit().removeAWTEventListener(this);
       
    }

    public void eventDispatched(AWTEvent event) {
        StringBuffer sbuf = new StringBuffer();

        if (event instanceof MouseEvent) {
            MouseEvent mouseEvent = (MouseEvent) event;
            Component component = mouseEvent.getComponent();
            String name = component.getName();
            int x = mouseEvent.getX();
            int y = mouseEvent.getY();

            if (mouseEvent.isConsumed()) {
                return;
            }

            sbuf.append("mouse ");

            int id = mouseEvent.getID();

            if (id == MouseEvent.MOUSE_PRESSED) {
                sbuf.append("press ");

                if ((name != null) && (!name.equals(""))) {
                    lastLoc = mouseEvent.getComponent().getLocationOnScreen();
                    lastCompName = name;
                }
            } else if (id == MouseEvent.MOUSE_RELEASED) {
                sbuf.append("release ");
            } else if (id == MouseEvent.MOUSE_DRAGGED) {
                sbuf.append("drag ");
            } else if (id == MouseEvent.MOUSE_MOVED) {
                sbuf.append("motion ");
            } else {
                return;
            }

            int button = (mouseEvent.getModifiers()
                    & (InputEvent.BUTTON1_MASK | InputEvent.BUTTON2_MASK
                    | InputEvent.BUTTON3_MASK));

            /*
            if (button == MouseEvent.BUTTON1) {
            sbuf.append("1 ");
            } else if (button == MouseEvent.BUTTON2) {
            sbuf.append("2 ");
            } else if (button == MouseEvent.BUTTON3) {
            sbuf.append("3 ");
            } else {
            sbuf.append("{} ");
            }
             */
            sbuf.append(button);
            sbuf.append(" ");

            if ((name == null) || (name.equals(""))) {
                if (component instanceof JPopupMenu) {
                    Point currentLoc = component.getLocationOnScreen();
                    x = currentLoc.x - lastLoc.x + x;
                    y = currentLoc.y - lastLoc.y + y;
                    sbuf.append(lastCompName);
                } else {
                    SwkWidget swkWidget = Widgets.swankParent(component);

                    if (swkWidget != null) {
                        sbuf.append(swkWidget.getName());

                        Point currentLoc = component.getLocationOnScreen();
                        Point parentLoc = ((Component) swkWidget).getLocationOnScreen();
                        x = currentLoc.x - parentLoc.x + x;
                        y = currentLoc.y - parentLoc.y + y;
                    }
                }
            } else {
                sbuf.append(name);
            }

            sbuf.append(" ");
            sbuf.append(x);
            sbuf.append(" ");
            sbuf.append(y);
            sbuf.append(" ");

            if (startTime < 0) {
                startTime = mouseEvent.getWhen();
            }

            sbuf.append(mouseEvent.getWhen() - startTime);
            eventList.add(sbuf.toString());
        } else if (event instanceof KeyEvent) {
            KeyEvent keyEvent = (KeyEvent) event;

            if (keyEvent.isConsumed()) {
                return;
            }

            int id = keyEvent.getID();
            sbuf.append("key ");

            if (id == KeyEvent.KEY_PRESSED) {
                sbuf.append("press ");
            } else if (id == KeyEvent.KEY_RELEASED) {
                sbuf.append("release ");
            } else {
                return;
            }

            sbuf.append(keyEvent.getKeyCode());
            sbuf.append(" ");

            if (startTime < 0) {
                startTime = keyEvent.getWhen();
            }

            sbuf.append(keyEvent.getWhen() - startTime);
            eventList.add(sbuf.toString());
        }
    }

    public  int eventCount() {
        return eventList.size();
    }

    public  String get(int i) {
        return (String) eventList.get(i);
    }

    public  void get(Interp interp) throws TclException {
        TclObject list = TclList.newInstance();

        for (int i = 0, n = eventList.size(); i < n; i++) {
            TclList.append(interp, list,
                    TclString.newInstance((String) eventList.get(i)));
        }

        interp.setResult(list);
    }
}
