package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

import java.awt.BasicStroke;

public class ArrowParameter extends CanvasParameter {

    private static String name = "arrow";
    private static String defValue = "none";
    private int newValue = 0;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return defValue;
    }

    public TclObject getValue(Interp interp, SwkShape swkShape)
            throws TclException {
        if (swkShape == null) {
            throw new TclException(interp, "shape doesn't exist");
        }
        ItemLine swkLine = (ItemLine) swkShape;
        String arrow = "none";
        if ((swkLine.arrowFirst) && (swkLine.arrowLast)) {
            arrow = "both";
        } else if (swkLine.arrowFirst) {
            arrow = "first";
        } else if (swkLine.arrowLast) {
            arrow = "last";
        } else {
            arrow = "none";
        }

        return TclString.newInstance(arrow);
    }

    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
            throws TclException {
        if (arg.toString().startsWith("both")) {
            newValue = 3;
        } else if (arg.toString().startsWith("first")) {
            newValue = 1;
        } else if (arg.toString().equals("last")) {
            newValue = 2;
        } else {
            newValue = 0;
        }
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if ((swkShape != null) && (swkShape instanceof ItemLine)) {
            ItemLine swkLine = (ItemLine) swkShape;
            switch (newValue) {
                case 0:
                    swkLine.arrowFirst = false;
                    swkLine.arrowLast = false;
                    break;
                case 1:
                    swkLine.arrowFirst = true;
                    swkLine.arrowLast = false;
                    break;
                case 2:
                    swkLine.arrowFirst = false;
                    swkLine.arrowLast = true;
                    break;
                case 3:
                    swkLine.arrowFirst = true;
                    swkLine.arrowLast = true;
                    break;
                default:
                    swkLine.arrowFirst = false;
                    swkLine.arrowLast = false;
            }
        }
    }
}
