/*
 * IntegerParameter.java
 *
 * Created on December 13, 2005, 2:05 PM
 */
package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

/**
 *
 * @author brucejohnson
 */
public abstract class IntegerParameter extends CanvasParameter {

    private static int defValue = 0;
    int newValue = defValue;

    public abstract int getValue(SwkShape swkShape);

    public TclObject getValue(Interp interp, SwkShape swkShape)
            throws TclException {
        if (swkShape == null) {
            throw new TclException(interp, "shape doesn't exist");
        }

        return TclInteger.newInstance(getValue(swkShape));
    }

    public int getNewValue() {
        return newValue;
    }

    @Override
    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
            throws TclException {
        newValue = TclInteger.getInt(interp, arg);
    }
}
