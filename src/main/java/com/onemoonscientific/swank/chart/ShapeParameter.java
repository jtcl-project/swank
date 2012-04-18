package com.onemoonscientific.swank.chart;

import com.onemoonscientific.swank.canvas.CanvasParameter;
import com.onemoonscientific.swank.canvas.SwkImageCanvas;
import com.onemoonscientific.swank.canvas.SwkShape;
import tcl.lang.Interp;
import tcl.lang.TclException;
import tcl.lang.TclList;
import tcl.lang.TclObject;

class ShapeParameter extends CanvasParameter {

    private static String name = "shape";
    String[] shapeNames = new String[0];

    ShapeParameter() {
        CanvasParameter.addParameter(this);
    }

    public String getName() {
        return name;
    }

    public String getDefault() {
        return "";
    }

    @Override
    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg) throws TclException {
        TclObject[] shapeNamesList = TclList.getElements(interp, arg);
        if (shapeNamesList.length == 0) {
            throw new TclException(interp, "bad shape value, must be \"shape1 shape2 ...\"");
        }
        String[] shapesTemp = new String[shapeNamesList.length];
        for (int i = 0; i < shapeNamesList.length; i++) {
            shapesTemp[i] = shapeNamesList[i].toString();
        }
        shapeNames = shapesTemp;
    }

    public TclObject getValue(Interp interp, SwkShape swkShape) throws TclException {
        return ((DatasetShape) swkShape).getShapes(interp);
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        ((DatasetShape) swkShape).updateShapes(shapeNames);
    }
}
