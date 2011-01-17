package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

/**
 *
 * @author brucejohnson
 */
public class StartstyleParameter extends EndstyleParameter {

    private static final String name = "startstyle";
    private static String defValue = "none";

    /**
     *
     * @return
     */
    @Override
    public String getName() {
        return name;
    }

    @Override
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
    @Override
    public TclObject getValue(Interp interp, SwkShape swkShape)
            throws TclException {
        if (swkShape == null) {
            throw new TclException(interp, "shape doesn't exist");
        }
        ItemLine swkLine = (ItemLine) swkShape;
        String type = swkLine.endPointStyle1.getDescription();
        return TclString.newInstance(type);
    }

    @Override
    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if ((swkShape != null) && (swkShape instanceof ItemLine)) {
            ItemLine swkLine = (ItemLine) swkShape;
            swkLine.endPointStyle1 = newValue;
        }
    }
}
