package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

import java.awt.geom.*;

public class ShearParameter extends CanvasParameter {

    private static final String name = "shear";
    private static String defValue = "0.0 0.0";
    private float xShear = 0.0f;
    private float yShear = 0.0f;

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

        TclObject list = TclList.newInstance();
        TclList.append(interp, list, TclDouble.newInstance(swkShape.xShear));
        TclList.append(interp, list, TclDouble.newInstance(swkShape.yShear));

        return list;
    }

    @Override
    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
            throws TclException {
        TclObject[] shearList = TclList.getElements(interp, arg);

        if (shearList.length != 2) {
            throw new TclException(interp,
                    "bad shear value, must be \"xShear yShear\"");
        }

        xShear = (float) (TclDouble.get(interp, shearList[0]));
        yShear = (float) (TclDouble.get(interp, shearList[1]));
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if (swkShape != null) {
            swkShape.xShear = xShear;
            swkShape.yShear = yShear;
            swkShape.newTransform = true;
        }
    }
}
