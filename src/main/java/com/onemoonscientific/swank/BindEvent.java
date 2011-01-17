package com.onemoonscientific.swank;

import tcl.lang.*;
import java.util.*;

/**
 *
 * @author brucejohnson
 */
public class BindEvent extends TclEvent {

    Interp interp = null;
    String cmd = null;
    String var1 = null;
    String var2 = null;
    String value = null;
    SwkListener swkListener = null;
    Object obj = null;
    EventObject event = null;
    int subtype = 0;

    /**
     * 
     * @param interp
     * @param cmd
     */
    public BindEvent(Interp interp, String cmd) {
        this.interp = interp;
        this.cmd = cmd.intern();
    }

    /**
     *
     * @param interp
     * @param var1
     * @param var2
     * @param value
     */
    public BindEvent(Interp interp, String var1, String var2, String value) {
        this.interp = interp;
        this.var1 = var1;
        this.var2 = var2;
        this.value = value;
    }

    /**
     *
     * @param interp
     * @param swkListener
     * @param event
     * @param subtype
     */
    public BindEvent(Interp interp, SwkListener swkListener, EventObject event,
            int subtype) {
        this.interp = interp;
        this.swkListener = swkListener;
        this.event = event;
        this.subtype = subtype;
    }

    /**
     *
     * @param interp
     * @param swkListener
     * @param event
     * @param obj
     * @param subtype
     */
    public BindEvent(Interp interp, SwkListener swkListener, EventObject event,
            Object obj, int subtype) {
        this.interp = interp;
        this.swkListener = swkListener;
        this.event = event;
        this.obj = obj;
        this.subtype = subtype;
    }

    /**
     *
     * @param flags
     * @return
     */
    public int processEvent(int flags) {
        if ((interp != null) && (swkListener != null)) {
            try {
                swkListener.processEvent(event, obj, subtype);
            } catch (Exception e) {
                interp.addErrorInfo(e.getMessage());
                interp.backgroundError();
                e.printStackTrace();
            }
        } else if ((interp != null) && (cmd != null)) {
            SwkExceptionCmd.doExceptionCmdBG(interp, cmd);
        } else if ((interp != null) && (var1 != null)) {
            SwkExceptionCmd.setVar(interp, var1, var2, value);
        }

        return 1;
    }

    /**
     *
     */
    public void invokeLater() {
        interp.getNotifier().queueEvent(this, TCL.QUEUE_TAIL);
    }

    // using following can result in a deadlock 
    /**
     *
     */
    public void invokeAndWait() {
        interp.getNotifier().queueEvent(this, TCL.QUEUE_TAIL);
        sync();
    }
}
