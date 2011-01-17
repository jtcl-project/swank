/*
 * CanvasParameterConfigure.java
 *
 * Created on November 26, 2005, 10:45 AM
 */
package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

/**
 *
 * @author brucejohnson
 */
public interface CanvasParameterConfigure {

    /**
     *
     * @param swkCanvas
     * @param swkShape
     */
    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape);

    /**
     *
     * @param interp
     * @param swkCanvas
     * @param arg
     * @throws TclException
     */
    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
            throws TclException;
}
