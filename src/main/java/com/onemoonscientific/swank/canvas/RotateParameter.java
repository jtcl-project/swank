package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

import java.awt.geom.*;

public class RotateParameter extends CanvasParameter {

    private static String name = "rotate";
    private static double defValue = 0.0;
    private float newValue = 0.0f;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return defValue + "";
    }

    public boolean isParameterLabel(String s) {
        if ((s.length() > 3) && "-rotate".startsWith(s)) {
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

        return TclDouble.newInstance(swkShape.rotate / Math.PI * 180.0);
    }

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
