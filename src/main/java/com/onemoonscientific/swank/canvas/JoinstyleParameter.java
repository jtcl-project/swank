package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

import java.awt.BasicStroke;

public class JoinstyleParameter extends CanvasParameter {

    private static String name = "joinstyle";
    private static String defValue = "miter";
    private int newValue = BasicStroke.JOIN_MITER;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return defValue + "";
    }

    public boolean isParameterLabel(String s) {
        if ((s.length() > 3) && "-joinstyle".startsWith(s)) {
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

        String join = "miter";

        switch (swkShape.getJoin()) {
            case BasicStroke.JOIN_BEVEL:
                join = "bevel";

                break;

            case BasicStroke.JOIN_MITER:
                join = "miter";

                break;

            case BasicStroke.JOIN_ROUND:
                join = "round";

                break;
        }

        return TclString.newInstance(join);
    }

    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
            throws TclException {
        if (arg.toString().startsWith("bevel")) {
            newValue = BasicStroke.JOIN_BEVEL;
        } else if (arg.toString().startsWith("miter")) {
            newValue = BasicStroke.JOIN_MITER;
        } else if (arg.toString().equals("round")) {
            newValue = BasicStroke.JOIN_ROUND;
        }
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if (swkShape != null) {
            swkShape.setJoin(newValue);
        }
    }
}
