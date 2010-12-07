package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.*;

import tcl.lang.*;

public class AnchorParameter extends CanvasParameter {

    private static String name = "anchor";
    private static String defValue = "";
    private float[] newValue = null;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return defValue;
    }

    public boolean isParameterLabel(String s) {
        if ((s.length() > 3) && "-anchor".startsWith(s)) {
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

        return TclString.newInstance(SwankUtil.parseAnchor(
                ((TextInterface) swkShape).getAnchor()));
    }

    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
            throws TclException {
        newValue = SwankUtil.getAnchor(interp, arg);
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if ((swkShape != null) && (newValue != null)) {
            if (swkShape instanceof TextInterface) {
                ((TextInterface) swkShape).setAnchor(newValue);
            }
        }
    }
}
