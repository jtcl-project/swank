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
import java.awt.event.*;

import java.lang.*;

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;


public class SwkPopupListener extends MouseAdapter implements SwkListener {
    Interp interp;
    String popupName;
    SwkSMenu popup = null;
    Component component;

    SwkPopupListener(Interp interp, Component component, String popupName) {
        this.interp = interp;
        this.component = component;

        this.popupName = popupName.intern();
    }

    public void mousePressed(MouseEvent e) {
        System.out.println("pressed");
        maybeShowPopup(e, 0);
    }

    public void mouseReleased(MouseEvent e) {
        System.out.println("released");
        maybeShowPopup(e, 0);
    }

    public void mouseEntered(MouseEvent e) {
        System.out.println("entered");

        if (popup != null) {
            //popup.setVisible(false);
        }

        //maybeShowPopup(e,0);
    }

    public void mouseExited(MouseEvent e) {
        System.out.println("exited");

        //maybeShowPopup(e,2);
    }

    public void maybeShowPopup(MouseEvent e, int show) {
        BindEvent bEvent = new BindEvent(interp, (SwkListener) this,
                (EventObject) e, show);
        interp.getNotifier().queueEvent(bEvent, TCL.QUEUE_TAIL);
    }

    public void processEvent(EventObject eventObject, Object obj, int show) {
        MouseEvent e = (MouseEvent) eventObject;

        if (e.isPopupTrigger() || true) {
            System.out.println("popuptrigger");

            TclObject tObj = (TclObject) Widgets.getWidget(interp,popupName);
            System.out.println(popupName);

            if (tObj == null) {
                System.out.println("no popup");

                return;
            }

            try {
                if (ReflectObject.get(interp, tObj) instanceof SwkSMenu) {
                    popup = (SwkSMenu) ReflectObject.get(interp, tObj);

                    if (popup == null) {
                        System.out.println("no popup");

                        return;
                    }

                    Dimension dim = e.getComponent().getSize();
                    Point loc = e.getComponent().getLocationOnScreen();
                    Point loc1 = e.getComponent().getLocation();
                    int x = loc.x + dim.width;
                    int y = loc.y;
                    System.out.println("show popup " + x + " " + y);

                    if (show == 0) {
                        popup.show(component, loc1.x + dim.width, loc1.y);
                    } else if (show == 1) {
                        popup.setLocation(x, y);
                        popup.setVisible(true);
                    } else {
                        popup.setVisible(false);
                    }
                } else {
                    System.out.println("not SwkSMenu " + popupName);
                }
            } catch (TclException tE) {
                interp.addErrorInfo("\n    (\"binding\" script)");
                interp.backgroundError();
            }
        }
    }
}