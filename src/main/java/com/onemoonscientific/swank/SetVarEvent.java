package com.onemoonscientific.swank;

import tcl.lang.*;

import java.awt.*;
import java.awt.event.*;

import java.util.*;


public abstract class SetVarEvent extends TclEvent {
    Interp interp = null;
    String var1 = null;
    String var2 = null;
    boolean onlyIfVarNonExistant = false;
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

            boolean setVar = true;

            if (onlyIfVarNonExistant) {
                setVar = false;

                try {
                    TclObject tObjTest = interp.getVar(var1, var2,
                            TCL.GLOBAL_ONLY);
                } catch (TclException tclException) {
                    setVar = true;
                }
            }

            if (setVar) {
                SwkExceptionCmd.setVar(interp, var1, var2, tObj);
            }
        }

        return 1;
    }

    public void invokeLater() {
        interp.getNotifier().queueEvent(this, TCL.QUEUE_TAIL);
    }
}
