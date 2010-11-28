package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

import java.awt.BasicStroke;


public class EndstyleParameter extends CanvasParameter {
    private static String name = "endstyle";
    private static String defValue = "none";
    ItemLine.EndPointStyle newValue = ItemLine.EndPointStyle.NONE;

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
        ItemLine swkLine = (ItemLine) swkShape;
        String type = swkLine.endPointStyle2.getDescription();
        return TclString.newInstance(type);
    }

    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
        throws TclException {
        if (arg.toString().startsWith("circle")) {
            newValue = ItemLine.EndPointStyle.CIRCLE;
        } else if (arg.toString().startsWith("arrow")) {
            newValue = ItemLine.EndPointStyle.ARROW;
        } else if (arg.toString().equals("square")) {
            newValue = ItemLine.EndPointStyle.SQUARE;
        } else if (arg.toString().equals("diamond")) {
            newValue = ItemLine.EndPointStyle.DIAMOND;
        } else {
            newValue = ItemLine.EndPointStyle.NONE;
        }
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if ((swkShape != null) && (swkShape instanceof ItemLine)) {
            ItemLine swkLine = (ItemLine) swkShape;
            swkLine.endPointStyle2 = newValue;
        }
    }
}
