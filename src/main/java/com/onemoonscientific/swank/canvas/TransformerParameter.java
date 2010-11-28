package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

import java.awt.geom.*;


public class TransformerParameter extends CanvasParameter {
    private static String name = "transformer";
    private static String defValue = "";
    private String newValue = defValue;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return defValue + "";
    }

    public boolean isParameterLabel(String s) {
        if ((s.length() > 3) && "-transformer".startsWith(s)) {
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

        if (swkShape.transformer != null) {
            if (swkShape.transformer.isValid()) {
                return TclString.newInstance(swkShape.transformer.getName());
            } else {
                swkShape.transformer = null;
            }
        }

        return TclString.newInstance("");
    }

    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
        throws TclException {
        newValue = arg.toString();
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if (swkShape != null) {
            swkShape.canvas.setTransformer(newValue, swkShape);
        }
    }
}
