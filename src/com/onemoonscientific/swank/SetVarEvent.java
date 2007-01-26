package com.onemoonscientific.swank;

import tcl.lang.*;

import java.awt.*;
import java.awt.event.*;

import java.util.*;


public abstract class SetVarEvent extends TclEvent {
    Interp interp = null;
    String var1 = null;
    String var2 = null;
    TclObject tObj = null;
    Object comp = null;

    void setObject() {
    }

    public int processEvent(int flags) {
        setObject();

        if ((interp != null) && (var1 != null) && (tObj != null)) {
            if (comp instanceof TraceLock) {
                ((TraceLock) comp).setTraceLock(true);
            }

            SwkExceptionCmd.setVar(interp, var1, var2, tObj);
        }

        return 1;
    }

    public void invokeLater() {
        interp.getNotifier().queueEvent(this, TCL.QUEUE_TAIL);
    }
}
