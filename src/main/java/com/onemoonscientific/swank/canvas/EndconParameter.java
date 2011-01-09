package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

public class EndconParameter extends CanvasParameter {

    private static final String name = "endcon";
    private static String defValue = "";
    private String newValue = null;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return defValue;
    }

    public TclObject getValue(Interp interp, SwkShape swkShape)
            throws TclException {
        String value = "";
        if ((swkShape != null) && (swkShape instanceof ItemConnector)) {
            value = ((ItemConnector) swkShape).endCon;

        }

        return TclString.newInstance(value);
    }

    @Override
    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
            throws TclException {
        newValue = arg.toString();
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if ((swkShape != null) && (swkShape instanceof ItemConnector)) {
            ((ItemConnector) swkShape).endCon = newValue;
        }
    }
}
