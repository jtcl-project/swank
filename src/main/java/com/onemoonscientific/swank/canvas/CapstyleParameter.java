package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

import java.awt.BasicStroke;

public class CapstyleParameter extends CanvasParameter {

    private static String name = "capstyle";
    private static String defValue = "butt";
    private int newValue = BasicStroke.CAP_BUTT;

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

        String cap = "butt";

        switch (swkShape.getCap()) {
            case BasicStroke.CAP_BUTT:
                cap = "butt";

                break;

            case BasicStroke.CAP_SQUARE:
                cap = "projecting";

                break;

            case BasicStroke.CAP_ROUND:
                cap = "round";

                break;
        }

        return TclString.newInstance(cap);
    }

    @Override
    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
            throws TclException {
        if (arg.toString().startsWith("butt")) {
            newValue = BasicStroke.CAP_BUTT;
        } else if (arg.toString().startsWith("projecting")) {
            newValue = BasicStroke.CAP_SQUARE;
        } else if (arg.toString().equals("round")) {
            newValue = BasicStroke.CAP_ROUND;
        }
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if (swkShape != null) {
            swkShape.setCap(newValue);
        }
    }
}
