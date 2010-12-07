package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

import java.awt.BasicStroke;

public class StartstyleParameter extends EndstyleParameter {

    private static String name = "startstyle";
    private static String defValue = "none";

    public String getName() {
        return name;
    }

    public String getDefault() {
        return defValue + "";
    }

    public boolean isParameterLabel(String s) {
        if ((s.length() > 3) && "-startst".startsWith(s)) {
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
        String type = swkLine.endPointStyle1.getDescription();
        return TclString.newInstance(type);
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if ((swkShape != null) && (swkShape instanceof ItemLine)) {
            ItemLine swkLine = (ItemLine) swkShape;
            swkLine.endPointStyle1 = newValue;
        }
    }
}
