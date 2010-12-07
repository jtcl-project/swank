package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

public class StateParameter extends CanvasParameter {

    private static String name = "state";
    private static int defValue = SwkShape.ACTIVE;
    private byte newValue = SwkShape.ACTIVE;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return defValue + "";
    }

    public boolean isParameterLabel(String s) {
        if ((s.length() > 3) && "-state".startsWith(s)) {
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

        return TclString.newInstance(swkShape.getStateString());
    }

    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
            throws TclException {
        String stateString = arg.toString();

        if ("normal".startsWith(stateString)) {
            newValue = SwkShape.ACTIVE;
        } else if ("hidden".startsWith(stateString)) {
            newValue = SwkShape.HIDDEN;
        } else if ("disabled".startsWith(stateString)) {
            newValue = SwkShape.DISABLED;
        } else {
            throw new TclException(interp,
                    "invalid state argument \"" + stateString + "\"");
        }
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if (swkShape != null) {
            swkShape.setState(newValue);
        }
    }
}
