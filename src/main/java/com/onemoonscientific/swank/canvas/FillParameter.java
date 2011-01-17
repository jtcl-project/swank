package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.*;
import tcl.lang.*;
import java.awt.*;

/**
 *
 * @author brucejohnson
 */
public class FillParameter extends CanvasParameter {

    private static final String name = "fill";
    private Color newValue = null;

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
        return "";
    }

    /**
     *
     * @param s
     * @return
     */
    @Override
    public boolean isParameterLabel(String s) {
        if ((s.length() > 3) && "-fill".startsWith(s)) {
            return true;
        } else {
            return false;
        }
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

        Color colorVal;

        if (swkShape instanceof ItemLine) {
            colorVal = swkShape.outline;
        } else if (swkShape instanceof ItemText) {
            colorVal = ((ItemText) swkShape).getTextColor();
        } else {
            colorVal = swkShape.fill;
        }

        return (TclString.newInstance(SwankUtil.parseColor(colorVal)));
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
        newValue = SwankUtil.getColor(interp, arg);
    }

    /**
     *
     * @param swkCanvas
     * @param swkShape
     */
    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if (swkShape != null) {
            if (swkShape instanceof ItemLine) {
                swkShape.outline = newValue;
            } else if (swkShape instanceof ItemText) {
                ((ItemText) swkShape).setTextColor(newValue);
            } else {
                swkShape.fill = newValue;
            }
        }
    }
}
