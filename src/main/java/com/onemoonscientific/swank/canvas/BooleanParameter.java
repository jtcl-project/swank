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

    /**
     *
     * @return
     */
    public String getDefault() {
        return defValue + "";
    }

    /**
     *
     * @param swkShape
     * @return
     */
    public boolean getValue(SwkShape swkShape) {
        return false;
    }

    /**
     *
     * @param interp
     * @param swkShape
     * @return
     * @throws TclException
     */
    public TclObject getValue(Interp interp, SwkShape swkShape)
            throws TclException {
        if (swkShape == null) {
            throw new TclException(interp, "shape doesn't exist");
        }

        return TclBoolean.newInstance(getValue(swkShape));
    }

    /**
     *
     * @return
     */
    public boolean getNewValue() {
        return newValue;
    }

    /**
     *
     * @param interp
     * @param swkCanvas
     * @param arg
     * @throws TclException
     */
    @Override
    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
            throws TclException {
        newValue = TclBoolean.get(interp, arg);
    }
}
