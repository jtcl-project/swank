package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

/**
 *
 * @author brucejohnson
 */
public class WindowParameter extends CanvasParameter {

    private static final String name = "window";
    private static String defValue = "";
    private String newValue = defValue;

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    public String getDefault() {
        return defValue;
    }

    public TclObject getValue(Interp interp, SwkShape swkShape)
            throws TclException {
        if (((ItemWindow) swkShape).windowName == null) {
            ((ItemWindow) swkShape).windowName = "";
        }

        return TclString.newInstance(((ItemWindow) swkShape).windowName);
    }

    @Override
    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
            throws TclException {
        if (arg == null) {
            newValue = "";
        } else {
            newValue = arg.toString();
        }
    }

    /**
     *
     * @param swkCanvas
     * @param swkShape
     */
    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if ((swkShape != null) && (swkShape instanceof ItemWindow)) {
            ((ItemWindow) swkShape).windowName = newValue;
        }
    }
}
