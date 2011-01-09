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

    public String getDefault() {
        return defValue + "";
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

    @Override
    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
            throws TclException {
        newValue = TclBoolean.get(interp, arg);
    }
}
