package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

import java.awt.geom.*;

public class RadiusParameter extends CanvasParameter {

    private static String name = "radius";
    private static double defValue = 90.0;
    double newValue = defValue;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return defValue + "";
    }

    public boolean isParameterLabel(String s) {
        if ((s.length() > 3) && "-radius".startsWith(s)) {
            return true;
        } else {
            return false;
        }
    }

    public TclObject getValue(Interp interp, SwkShape swkShape)
            throws TclException {
        double radius = 0.0;

        if ((swkShape != null) && (swkShape instanceof SymbolInterface)) {
            radius = ((SymbolInterface) swkShape).getRadius();
        }

        return TclDouble.newInstance(radius);
    }

    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
            throws TclException {
        newValue = TclDouble.get(interp, arg);
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if ((swkShape != null) && (swkShape instanceof SymbolInterface)) {
            ((SymbolInterface) swkShape).setRadius(newValue);
        }
    }
}
