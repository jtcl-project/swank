package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.*;

import tcl.lang.*;


public class TextParameter extends CanvasParameter {
    private static String name = "text";
    private static String defValue = "";
    private String newValue = defValue;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return defValue;
    }

    public boolean isParameterLabel(String s) {
        if ((s.length() > 3) && "-text".startsWith(s)) {
            return true;
        } else {
            return false;
        }
    }

    public TclObject getValue(Interp interp, SwkShape swkShape)
        throws TclException {
        if (((SwkCanvText) swkShape).text == null) {
            ((SwkCanvText) swkShape).text = "";
        }

        return TclString.newInstance(((SwkCanvText) swkShape).text);
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
        if ((swkShape != null) && (swkShape instanceof SwkCanvText)) {
            ((SwkCanvText) swkShape).text = newValue;
        }
    }
}
