package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.*;

import tcl.lang.*;

import java.awt.image.BufferedImage;


public class ImageParameter extends CanvasParameter {
    private static String name = "image";
    private static String defValue = "";
    private BufferedImage newImage = null;
    private String newName = null;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return defValue;
    }

    public boolean isParameterLabel(String s) {
        if ((s.length() > 3) && "-image".startsWith(s)) {
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

        if (swkShape instanceof ItemImage) {
            return TclString.newInstance(swkShape.imageName);
        } else {
            throw new TclException(interp, "shape not image");
        }
    }

    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
        throws TclException {
        newImage = SwankUtil.getBufferedImage(interp, arg);
        newName = arg.toString();
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if ((swkShape != null) && (swkShape instanceof ItemImage)) {
            swkShape.imageName = newName;
            ((ItemImage) swkShape).setImage(newImage);
        }
    }
}
