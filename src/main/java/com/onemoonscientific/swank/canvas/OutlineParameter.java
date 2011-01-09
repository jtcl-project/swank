package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.*;
import tcl.lang.*;
import java.awt.*;

public class OutlineParameter extends CanvasParameter {

    private static final String name = "outline";
    private static Color defValue = null;
    private Color newValue = defValue;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return "";
    }

    public TclObject getValue(Interp interp, SwkShape swkShape)
            throws TclException {
        if (swkShape == null) {
            throw new TclException(interp, "shape doesn't exist");
        }

        return (TclString.newInstance(SwankUtil.parseColor(swkShape.outline)));
    }

    @Override
    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
            throws TclException {
        newValue = SwankUtil.getColor(interp, arg);
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if (swkShape != null) {
            swkShape.outline = newValue;
        }
    }
}
