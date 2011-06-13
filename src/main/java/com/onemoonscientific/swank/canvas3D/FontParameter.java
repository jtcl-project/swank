package com.onemoonscientific.swank.canvas3D;

import com.onemoonscientific.swank.*;
import tcl.lang.*;
import java.awt.Font;

/**
 *
 * @author brucejohnson
 */
public class FontParameter extends CanvasParameter {

    private static String name = "font";
    private static String defValue = "";
    private Font newValue = null;

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

        return TclString.newInstance(SwankUtil.parseFont(
                ((TextInterface) swkShape).getFont()));
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
        newValue = SwankUtil.getFont(interp, arg);
    }

    /**
     *
     * @param swkCanvas
     * @param swkShape
     */
    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if ((swkShape != null) && (swkShape instanceof TextInterface)
                && (newValue != null)) {
            ((TextInterface) swkShape).setFont(newValue);
        }
    }
}
