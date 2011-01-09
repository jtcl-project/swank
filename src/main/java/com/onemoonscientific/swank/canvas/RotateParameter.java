package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

public class RotateParameter extends CanvasParameter {

    private static final String name = "rotate";
    private static double defValue = 0.0;
    private float newValue = 0.0f;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return defValue + "";
    }

    public TclObject getValue(Interp interp, SwkShape swkShape)
            throws TclException {
        if (swkShape == null) {
            throw new TclException(interp, "shape doesn't exist");
        }

        return TclDouble.newInstance(swkShape.rotate / Math.PI * 180.0);
    }

    @Override
    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
            throws TclException {
        newValue = (float) (TclDouble.get(interp, arg) / 180.0 * Math.PI);
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if (swkShape == null) {
            return;
        }

        swkShape.rotate = newValue;
        swkShape.newTransform = true;
    }
}
