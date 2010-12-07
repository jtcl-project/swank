package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.*;

import tcl.lang.*;

import javax.swing.ImageIcon;

public class BitmapParameter extends CanvasParameter {

    private static String name = "bitmap";
    private static String defValue = "";
    private ImageIcon newValue = null;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return defValue;
    }

    public boolean isParameterLabel(String s) {
        if ((s.length() > 3) && "-bitmap".startsWith(s)) {
            return true;
        } else {
            return false;
        }
    }

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

    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
            throws TclException {
        newValue = SwankUtil.getImageIcon(interp, arg);
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if (swkShape == null) {
            return;
        }

        if ((swkShape instanceof ItemBitmap) && (newValue != null)) {
            ((ItemBitmap) swkShape).setImageIcon(newValue);
        }
    }
}
