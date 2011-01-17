package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

import java.awt.geom.*;

/**
 *
 * @author brucejohnson
 */
public class AngleStartParameter extends CanvasParameter {

    private static String name = "start";
    private static double defValue = 0.0;
    private double newValue = defValue;

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @return
     */
    public String getDefault() {
        return String.valueOf(defValue);
    }

 
    /**
     *
     * @param interp
     * @param swkShape
     * @return
     * @throws TclException
     */
    public TclObject getValue(Interp interp, SwkShape swkShape)
            throws TclException {
        if (swkShape == null) {
            throw new TclException(interp, "shape doesn't exist");
        }

        if (swkShape instanceof ItemArc) {
            Arc2D arc2D = ((ItemArc) swkShape).arc2D;

            return TclDouble.newInstance(arc2D.getAngleStart());
        } else {
            throw new TclException(interp, "shape not arc");
        }
    }

    /**
     *
     * @param interp
     * @param swkCanvas
     * @param arg
     * @throws TclException
     */
    @Override
    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
            throws TclException {
        newValue = TclDouble.get(interp, arg);
    }

    /**
     *
     * @param swkCanvas
     * @param swkShape
     */
    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if (swkShape == null) {
            return;
        }

        if (swkShape instanceof ItemArc) {
            Arc2D arc2D = ((ItemArc) swkShape).arc2D;
            arc2D.setAngleStart(newValue);
            swkShape.applyCoordinates();
        }
    }
}
