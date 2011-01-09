package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.*;

import tcl.lang.*;

public class HeightParameter extends CanvasParameter {

    private static String name = "height";
    private static double defValue = 1.0;
    private float newValue = 1.0f;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return String.valueOf(defValue);
    }

    public TclObject getValue(Interp interp, SwkShape swkShape)
            throws TclException {
        if (swkShape == null) {
            throw new TclException(interp, "shape doesn't exist");
        }

        return (TclDouble.newInstance(swkShape.height));
    }

    @Override
    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
            throws TclException {
        newValue = (float) SwankUtil.getTkSizeD(interp,
                swkCanvas.getComponent(), arg);
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if (swkShape != null) {
            swkShape.height = newValue;
            swkShape.newStroke = true;
        }
    }
}
