package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.*;

import tcl.lang.*;


public class WindowParameter extends CanvasParameter {
    private static String name = "window";
    private static String defValue = "";
    private String newValue = defValue;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return defValue;
    }

    public boolean isParameterLabel(String s) {
        if ((s.length() > 3) && "-window".startsWith(s)) {
            return true;
        } else {
            return false;
        }
    }

    public TclObject getValue(Interp interp, SwkShape swkShape)
        throws TclException {
        if (((ItemWindow) swkShape).windowName == null) {
            ((ItemWindow) swkShape).windowName = "";
        }

        return TclString.newInstance(((ItemWindow) swkShape).windowName);
    }

    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
        throws TclException {
        if (arg == null) {
            newValue = "";
        } else {
            newValue = new String(arg.toString());
        }
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if ((swkShape != null) && (swkShape instanceof ItemWindow)) {
            ((ItemWindow) swkShape).windowName = newValue;
        }
    }
}
