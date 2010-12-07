package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.*;

import tcl.lang.*;

public class DashParameter extends CanvasParameter {

    private static String name = "dash";
    private static double[] defValue = {};
    String dashString = "";
    boolean dashIntPattern = false;
    float[] dash = null;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return "";
    }

    public boolean isParameterLabel(String s) {
        if (s.equals("-dash")) {
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

        return TclString.newInstance(swkShape.getDashString());
    }

    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
            throws TclException {
        dashString = arg.toString();
        dashIntPattern = SwankUtil.isDashIntPattern(interp, arg);
        dash = SwankUtil.getDash(interp, arg);
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        swkShape.setDashString(dashString);
        swkShape.setDashIntPattern(dashIntPattern);

        swkShape.setDash(dash);
        swkShape.newStroke = true;
    }
}
