/*
 * BooleanParameter.java
 *
 * Created on December 13, 2005, 2:05 PM
 */
package com.onemoonscientific.swank.canvas;

import tcl.lang.*;


/**
 *
 * @author brucejohnson
 */
public abstract class BooleanParameter extends CanvasParameter {
    private static boolean defValue = false;
    boolean newValue = defValue;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return defValue + "";
    }

    public boolean isParameterLabel(String s) {
        if ((s.length() > 3) && ("-" + name).startsWith(s)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean getValue(SwkShape swkShape) {
        return false;
    }

    public TclObject getValue(Interp interp, SwkShape swkShape)
        throws TclException {
        if (swkShape == null) {
            throw new TclException(interp, "shape doesn't exist");
        }

        return TclBoolean.newInstance(getValue(swkShape));
    }

    public boolean getNewValue() {
        return newValue;
    }

    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
        throws TclException {
        newValue = TclBoolean.get(interp, arg);
    }
}
