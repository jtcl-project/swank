package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

import java.awt.geom.*;

public class AngleStartParameter extends CanvasParameter {

    private static String name = "start";
    private static double defValue = 0.0;
    private double newValue = defValue;

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

        if (swkShape instanceof ItemArc) {
            Arc2D arc2D = ((ItemArc) swkShape).arc2D;

            return TclDouble.newInstance(arc2D.getAngleStart());
        } else {
            throw new TclException(interp, "shape not arc");
        }
    }

    @Override
    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
            throws TclException {
        newValue = TclDouble.get(interp, arg);
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if (swkShape == null) {
            return;
        }

        if (swkShape instanceof ItemArc) {
            Arc2D arc2D = ((ItemArc) swkShape).arc2D;
            arc2D.setAngleStart(newValue);
            swkShape.applyCoordinates();
        }
    }
}
