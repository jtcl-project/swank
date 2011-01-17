package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.*;
import tcl.lang.*;

/**
 *
 * @author brucejohnson
 */
public class TextWidthParameter extends CanvasParameter {

    private static String name = "width";
    private static double defValue = 1.0;
    private float newValue = 1.0f;

    public String getName() {
        return name;
    }

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

        return (TclDouble.newInstance(((ItemText) swkShape).width));
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
        newValue = (float) SwankUtil.getTkSizeD(interp,
                swkCanvas.getComponent(), arg);
    }

    /**
     *
     * @param swkCanvas
     * @param swkShape
     */
    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if ((swkShape != null) && (swkShape instanceof ItemText)) {
            ((ItemText) swkShape).width = newValue;
            swkShape.newStroke = true;
        }
    }
}
