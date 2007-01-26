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
    private static String defValue = "";
    String newValue = defValue;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return defValue;
    }

    public boolean isParameterLabel(String s) {
        if ((s.length() > 3) && ("-" + name).startsWith(s)) {
            return true;
        } else {
            return false;
        }
    }

    String getValue(SwkShape swkShape) {
        return "";
    }

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

    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
        throws TclException {
        newValue = arg.toString();
    }
}
