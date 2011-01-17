package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.*;

import tcl.lang.*;

import java.awt.image.BufferedImage;

/**
 *
 * @author brucejohnson
 */
public class ImageParameter extends CanvasParameter {

    private static final String name = "image";
    private static String defValue = "";
    private BufferedImage newImage = null;
    private String newName = null;

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

        if (swkShape instanceof ItemImage) {
            return TclString.newInstance(swkShape.imageName);
        } else {
            throw new TclException(interp, "shape not image");
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
        newImage = SwankUtil.getBufferedImage(interp, arg);
        newName = arg.toString();
    }

    /**
     *
     * @param swkCanvas
     * @param swkShape
     */
    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if ((swkShape != null) && (swkShape instanceof ItemImage)) {
            swkShape.imageName = newName;
            ((ItemImage) swkShape).setImage(newImage);
        }
    }
}
