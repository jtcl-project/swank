package com.onemoonscientific.swank.canvas3D;

import tcl.lang.*;


public class AppearanceParameter extends CanvasParameter {
    private static String name = "appearance";
    private static String defValue = "default";
    String newValue = defValue;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return defValue;
    }

    /**
     *
     * @param s
     * @return
     */
    public boolean isParameterLabel(String s) {
        if ((s.length() > 3) && "-appearance".startsWith(s)) {
            return true;
        } else {
            return false;
        }
    }

    public TclObject getValue(Interp interp, SwkShape swkShape)
        throws TclException {
        String appearanceName="";

        if (swkShape != null) {
            appearanceName = swkShape.getAppearanceName();
        }
        return TclString.newInstance(appearanceName);
    }

    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
        throws TclException {
        newValue = arg.toString();
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if (swkShape != null) {
            swkShape.setAppearanceByName(newValue);
            swkShape.updateShape();

        }
    }
}

