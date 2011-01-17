package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

/**
 *
 * @author brucejohnson
 */
public class EndstyleParameter extends CanvasParameter {

    private static final String name = "endstyle";
    private static String defValue = "none";
    ItemLine.EndPointStyle newValue = ItemLine.EndPointStyle.NONE;

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
        return defValue + "";
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
        ItemLine swkLine = (ItemLine) swkShape;
        String type = swkLine.endPointStyle2.getDescription();
        return TclString.newInstance(type);
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
        if (arg.toString().startsWith("circle")) {
            newValue = ItemLine.EndPointStyle.CIRCLE;
        } else if (arg.toString().startsWith("arrow")) {
            newValue = ItemLine.EndPointStyle.ARROW;
        } else if (arg.toString().equals("square")) {
            newValue = ItemLine.EndPointStyle.SQUARE;
        } else if (arg.toString().equals("diamond")) {
            newValue = ItemLine.EndPointStyle.DIAMOND;
        } else {
            newValue = ItemLine.EndPointStyle.NONE;
        }
    }

    /**
     *
     * @param swkCanvas
     * @param swkShape
     */
    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if ((swkShape != null) && (swkShape instanceof ItemLine)) {
            ItemLine swkLine = (ItemLine) swkShape;
            swkLine.endPointStyle2 = newValue;
        }
    }
}
