/*
 * DoubleParameter.java
 *
 * Created on December 13, 2005, 2:05 PM
 */
package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

/**
 *
 * @author brucejohnson
 */
public abstract class DoubleParameter extends CanvasParameter {

    private static double defValue = 1.0;
    double newValue = defValue;

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

    public abstract double getValue(SwkShape swkShape);

    public TclObject getValue(Interp interp, SwkShape swkShape)
            throws TclException {
        if (swkShape == null) {
            throw new TclException(interp, "shape doesn't exist");
        }

        return TclDouble.newInstance(getValue(swkShape));
    }

    public double getNewValue() {
        return newValue;
    }

    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
            throws TclException {
        newValue = TclDouble.get(interp, arg);
    }
}
