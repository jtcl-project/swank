package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

import java.awt.geom.*;


public class JustifyParameter extends CanvasParameter {
    private static String name = "justify";
    private static String defValue = "";
    private String newValue = defValue;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return defValue;
    }

    public boolean isParameterLabel(String s) {
        if ((s.length() > 3) && "-justify".startsWith(s)) {
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

        return TclString.newInstance(swkLine.smooth);
    }

    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
        throws TclException {
        if (arg == null) {
            newValue = "";
        } else {
            newValue = arg.toString();
        }
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if ((swkShape != null) && (swkShape instanceof SwkCanvText)) {
        }
    }
}
