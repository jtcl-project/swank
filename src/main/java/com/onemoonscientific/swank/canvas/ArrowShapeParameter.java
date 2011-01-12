package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

public class ArrowShapeParameter extends CanvasParameter {

    private static String name = "arrowshape";
    private static String defValue = "8 10 3";
    private double arrowShapeA = 8.0;
    private double arrowShapeB = 10.0;
    private double arrowShapeC = 3.0;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return defValue;
    }

    public TclObject getValue(Interp interp, SwkShape swkShape)
            throws TclException {
        if ((swkShape == null) || !(swkShape instanceof ItemLine)) {
            throw new TclException(interp, "line shape doesn't exist");
        }
        ItemLine swkLine = (ItemLine) swkShape;

        TclObject list = TclList.newInstance();
        TclList.append(interp, list, TclDouble.newInstance(swkLine.arrowShapeA));
        TclList.append(interp, list, TclDouble.newInstance(swkLine.arrowShapeB));
        TclList.append(interp, list, TclDouble.newInstance(swkLine.arrowShapeC));

        return list;
    }

    @Override
    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
            throws TclException {
        TclObject[] arrowShapeList = TclList.getElements(interp, arg);

        if (arrowShapeList.length != 3) {
            throw new TclException(interp,
                    "bad arrowShape value, must be \"arrowShapeA arrowShapeB arrowShapeC\"");
        }

        arrowShapeA = TclDouble.get(interp, arrowShapeList[0]);
        arrowShapeB = TclDouble.get(interp, arrowShapeList[1]);
        arrowShapeC = TclDouble.get(interp, arrowShapeList[2]);
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if ((swkShape != null) && (swkShape instanceof ItemLine)) {
            ItemLine swkLine = (ItemLine) swkShape;
            swkLine.arrowShapeA = arrowShapeA;
            swkLine.arrowShapeB = arrowShapeB;
            swkLine.arrowShapeC = arrowShapeC;
            swkLine.newTransform = true;
        }
    }
}
