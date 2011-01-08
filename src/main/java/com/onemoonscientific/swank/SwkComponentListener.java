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
import java.awt.event.*;
import java.util.*;

public class SwkComponentListener implements ComponentListener, SwkListener {

    Interp interp;
    String command = "puts component";
    ArrayList<SwkBinding> bindings;
    Component component;
    boolean shown = false;
    long lastResize = 0;

    SwkComponentListener(Interp interp, Component component) {
        this.interp = interp;
        this.component = component;
        bindings = new ArrayList<SwkBinding>();
    }

    public void setCommand(String name) {
        command = name;
    }

    public ArrayList<SwkBinding> getBindings() {
        return bindings;
    }

    public void setBinding(SwkBinding newBinding) {
        SwkBind.setBinding(bindings, newBinding);
    }

    public String getCommand() {
        return (command);
    }

    public void componentHidden(ComponentEvent e) {
        processComponent(e, SwkBinding.HIDDEN);
    }

    public void componentShown(ComponentEvent e) {
        shown = true;
        processComponent(e, SwkBinding.SHOWN);
    }

    public void componentMoved(ComponentEvent e) {
        processComponent(e, SwkBinding.MOVED);
    }

    public void componentResized(ComponentEvent e) {
        if (component instanceof SwkJFrame) {
            long timeNow = System.currentTimeMillis();
            if (!((SwkJFrame) component).isPacking && ((timeNow - lastResize) > 500)) {
                SwkJFrame jframe = (SwkJFrame) component;
                Dimension dim = jframe.getRootPane().getSize();

                if ((dim.width != jframe.swkwidth)
                        || (dim.height != jframe.swkheight)) {
                    shown = true;
                }

                if (shown) {
                    jframe.setGeometry(dim.width, dim.height);

                    //   jframe.swkwidth = dim.width;
                    //   jframe.swkheight = dim.height;
                    //   jframe.sizeConfigured = true;
                }
            }
            lastResize = System.currentTimeMillis();

            ((SwkJFrame) component).isPacking = false;
        }

        processComponent(e, SwkBinding.RESIZE);
    }

    public void processComponent(ComponentEvent e, int subtype) {
        BindEvent bEvent = new BindEvent(interp, (SwkListener) this,
                (EventObject) e, subtype);
        interp.getNotifier().queueEvent(bEvent, TCL.QUEUE_TAIL);
    }

    public void processEvent(EventObject eventObject, Object obj, int subtype) {
        if (eventObject instanceof ComponentEvent) {
            ComponentEvent e = (ComponentEvent) eventObject;
            SwkBinding binding;

            for (int i = 0; i < bindings.size(); i++) {
                binding = (SwkBinding) bindings.get(i);

                if ((binding.command != null) && (binding.command.length() != 0)) {
                    try {
                        BindCmd.doCmd(interp, binding, component, e);
                    } catch (TclException tclE) {
                        if (tclE.getCompletionCode() == TCL.BREAK) {
                            return;
                        } else {
                            interp.addErrorInfo("\n    (\"binding\" script)");
                            interp.backgroundError();
                        }
                    }
                }
            }
        }
    }
}
