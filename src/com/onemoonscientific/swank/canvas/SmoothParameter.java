package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

import java.awt.geom.*;


public class SmoothParameter extends CanvasParameter {
    private static String name = "smooth";
    private static String defValue = "";
    private String newValue = defValue;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return defValue;
    }

    public boolean isParameterLabel(String s) {
        if ((s.length() > 3) && "-smooth".startsWith(s)) {
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

        return TclString.newInstance(swkLine.smooth);
    }

    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
        throws TclException {
        boolean doSmooth = false;

        if (arg.toString().equals("bezier")) {
            doSmooth = true;
        } else {
            doSmooth = TclBoolean.get(interp, arg);
        }

        if (!doSmooth) {
            newValue = "";
        } else {
            newValue = arg.toString();
        }
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if ((swkShape != null) && (swkShape instanceof ItemLine)) {
            ItemLine swkLine = (ItemLine) swkShape;
            swkLine.smooth = newValue;
        }
    }
}
