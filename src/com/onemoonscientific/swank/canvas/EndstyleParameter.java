package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

import java.awt.BasicStroke;


public class EndstyleParameter extends CanvasParameter {
    private static String name = "endstyle";
    private static String defValue = "none";
    SwkLine.EndPointStyle newValue = SwkLine.EndPointStyle.NONE;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return defValue + "";
    }

    public boolean isParameterLabel(String s) {
        if ((s.length() > 3) && "-arrow".startsWith(s)) {
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
        SwkLine swkLine = (SwkLine) swkShape;
        String type = swkLine.endPointStyle2.getDescription();
        return TclString.newInstance(type);
    }

    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
        throws TclException {
        if (arg.toString().startsWith("circle")) {
            newValue = SwkLine.EndPointStyle.CIRCLE;
        } else if (arg.toString().startsWith("arrow")) {
            newValue = SwkLine.EndPointStyle.ARROW;
        } else if (arg.toString().equals("square")) {
            newValue = SwkLine.EndPointStyle.SQUARE;
        } else if (arg.toString().equals("diamond")) {
            newValue = SwkLine.EndPointStyle.DIAMOND;
        } else {
            newValue = SwkLine.EndPointStyle.NONE;
        }
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if ((swkShape != null) && (swkShape instanceof SwkLine)) {
            SwkLine swkLine = (SwkLine) swkShape;
            swkLine.endPointStyle2 = newValue;
        }
    }
}
