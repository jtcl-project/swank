/*
 * String.java
 *
 * Created on December 13, 2005, 2:07 PM
 */
package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

/**
 *
 * @author brucejohnson
 */
public abstract class StringParameter extends CanvasParameter {

    String newValue = "";


    public abstract String getValue(SwkShape swkShape);

    public String getNewValue() {
        return newValue;
    }

    public TclObject getValue(Interp interp, SwkShape swkShape)
            throws TclException {
        if (swkShape == null) {
            throw new TclException(interp, "shape doesn't exist");
        }

        return TclString.newInstance(getValue(swkShape));
    }

    @Override
    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
            throws TclException {
        newValue = arg.toString();
    }
}
