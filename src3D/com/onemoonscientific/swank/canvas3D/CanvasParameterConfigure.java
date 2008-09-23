/*
 * CanvasParameterConfigure.java
 *
 * Created on November 26, 2005, 10:45 AM
 */
package com.onemoonscientific.swank.canvas3D;

import tcl.lang.*;


/**
 *
 * @author brucejohnson
 */
public interface CanvasParameterConfigure {
    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape);

    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
        throws TclException;
}
