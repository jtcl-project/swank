package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.*;

import tcl.lang.*;

/**
 *
 * @author brucejohnson
 */
public class AnchorParameter extends CanvasParameter {

    private static String defValue = "";
    private float[] newValue = null;

    /**
     *
     * @return
     */
    public String getName() {
        return "anchor";
    }

    /**
     *
     * @return
     */
    public String getDefault() {
        return defValue;
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

        return TclString.newInstance(SwankUtil.parseAnchor(
                ((TextInterface) swkShape).getAnchor()));
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
        newValue = SwankUtil.getAnchor(interp, arg);
    }

    /**
     *
     * @param swkCanvas
     * @param swkShape
     */
    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if ((swkShape != null) && (newValue != null)) {
            if (swkShape instanceof TextInterface) {
                ((TextInterface) swkShape).setAnchor(newValue);
            }
        }
    }
}
