package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.*;

import tcl.lang.*;

import javax.swing.ImageIcon;

/**
 *
 * @author brucejohnson
 */
public class BitmapParameter extends CanvasParameter {

    private static String name = "bitmap";
    private static String defValue = "";
    private ImageIcon newValue = null;

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

        if (swkShape instanceof ItemBitmap) {
            return TclString.newInstance(SwankUtil.parseImageIcon(
                    ((ItemBitmap) swkShape).getImageIcon()));
        } else {
            throw new TclException(interp, "shape not bitmap");
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
        newValue = SwankUtil.getImageIcon(interp, arg);
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

        if ((swkShape instanceof ItemBitmap) && (newValue != null)) {
            ((ItemBitmap) swkShape).setImageIcon(newValue);
        }
    }
}
