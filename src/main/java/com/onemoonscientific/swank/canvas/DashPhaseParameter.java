package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

/**
 *
 * @author brucejohnson
 */
public class DashPhaseParameter extends CanvasParameter {

    private static String name = "dashphase";
    private static float defValue = 90.0f;
    private float newValue = defValue;

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

        return TclDouble.newInstance(swkShape.getDashPhase());
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
        newValue = (float) TclDouble.get(interp, arg);
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

        swkShape.setDashPhase(newValue);
        swkShape.newStroke = true;
    }
}
