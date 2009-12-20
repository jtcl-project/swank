package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.*;

import tcl.lang.*;

import java.awt.*;


public class TextColorParameter extends CanvasParameter {
    private static String name = "textcolor";
    private static Color defValue = null;
    private Color newValue = null;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return "";
    }

    public boolean isParameterLabel(String s) {
        if ((s.length() > 3) && "-textcolor".startsWith(s)) {
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

        Color colorVal;

        if (swkShape instanceof TextInterface) {
            colorVal = ((TextInterface) swkShape).getTextColor();
        } else {
            colorVal = swkShape.fill;
        }

        return (TclString.newInstance(SwankUtil.parseColor(colorVal)));
    }

    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
        throws TclException {
        newValue = SwankUtil.getColor(interp, arg);
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if (swkShape != null) {
            if (swkShape instanceof TextInterface) {
                ((TextInterface) swkShape).setTextColor(newValue);
            } else {
                swkShape.fill = newValue;
            }
        }
    }
}
