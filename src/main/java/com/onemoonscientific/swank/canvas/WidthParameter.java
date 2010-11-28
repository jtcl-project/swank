package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.*;

import tcl.lang.*;


public class WidthParameter extends CanvasParameter {
    private static String name = "width";
    private static double defValue = 1.0;
    private float newValue = 1.0f;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return defValue + "";
    }

    public boolean isParameterLabel(String s) {
        if ((s.length() > 3) && "-width".startsWith(s)) {
            return true;
        } else {
            return false;
        }
    }

    public TclObject getValue(Interp interp, SwkShape swkShape)
        throws TclException {
        if (swkShape == null) {
            throw new TclException(interp, "shape doesn't exist");
        }

        return (TclDouble.newInstance(swkShape.width));
    }

    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
        throws TclException {
        newValue = (float) SwankUtil.getTkSizeD(interp,
                swkCanvas.getComponent(), arg);
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if (swkShape != null) {
            swkShape.width = newValue;
            swkShape.newStroke = true;
        }
    }
}
