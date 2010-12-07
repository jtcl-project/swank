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

public class SwkActionListener implements ActionListener, SwkListener {

    Interp interp;
    String menu = null;
    Component component;

    SwkActionListener(Interp interp, Component component) {
        this.interp = interp;
        this.component = component;
    }

    public void setMenu(String name) {
        menu = name;
    }

    public String getMenu() {
        return (menu);
    }

    public void actionPerformed(ActionEvent e) {
        if (!EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkActionListener: actionPerformed not on event thread");
        }

        BindEvent bEvent = new BindEvent(interp, (SwkListener) this,
                (EventObject) e, 0);
        bEvent.invokeLater();
    }

    public void processEvent(EventObject eventObject, Object obj, int subtype) {
        if (EventQueue.isDispatchThread()) {
            System.out.println(
                    "SwkActionListener: processEvent on event thread");
        }

        ActionEvent e = (ActionEvent) eventObject;

        if ((menu != null) && (menu.length() != 0)) {
            TclObject tObj = (TclObject) Widgets.getWidget(interp, menu);

            if (tObj != null) {
                try {
                    final Object object = ReflectObject.get(interp, tObj);
                    final Dimension dim = component.getSize();

                    if (object instanceof javax.swing.JPopupMenu) {
                        SwingUtilities.invokeLater(new Runnable() {

                            public void run() {
                                ((JPopupMenu) object).show(component, 0,
                                        dim.height + 2);
                            }
                        });

                        //((JPopupMenu) object).setLocation (100,100);
                        //((JPopupMenu) object).setVisible(true);
                    } else if (object instanceof javax.swing.JMenu) {
                        final SwkJMenu swkjmenu = (SwkJMenu) object;

                        if ((swkjmenu.postCommand != null)
                                && (swkjmenu.postCommand.length() > 0)) {
                            try {
                                interp.eval(swkjmenu.postCommand);
                            } catch (TclException tclE) {
                                if (tclE.getCompletionCode() == TCL.BREAK) {
                                    return;
                                } else {
                                    interp.addErrorInfo(
                                            "\n    (\"binding\" script)");
                                    interp.backgroundError();
                                }
                            }
                        }

                        SwingUtilities.invokeLater(new Runnable() {

                            public void run() {
                                swkjmenu.getPopupMenu().show(component, 0,
                                        dim.height + 2);
                            }
                        });
                    }
                } catch (TclException tclE) {
                }
            }
        }
    }
}
