package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.*;

import tcl.lang.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

import javax.swing.*;

/**
 *
 * @author brucejohnson
 */
public class TextureParameter extends CanvasParameter {

    private static String name = "texture";
    private static TexturePaint defValue = null;
    private String newName = "";
    private TexturePaint newValue = null;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return "";
    }

    /**
     *
     * @param interp
     * @param arg
     * @return
     * @throws TclException
     */
    public static TexturePaint getFromImage(Interp interp, TclObject arg)
            throws TclException {
        ImageIcon image = SwankUtil.getImageIcon(interp, arg);

        if (image == null) {
            throw new TclException(interp,
                    "image \"" + arg.toString() + "\" doesn't exist");
        }

        BufferedImage bufferedImage = new BufferedImage(image.getIconWidth(),
                image.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g2 = bufferedImage.createGraphics();
        g2.drawImage(image.getImage(), 0, 0, image.getIconWidth(),
                image.getIconHeight(), null);

        // g2.setColor(Color.orange);
        //g2.drawOval(0,0,50,50);
        Rectangle2D tR = new Rectangle2D.Double(0, 0, bufferedImage.getWidth(),
                bufferedImage.getHeight());

        return (new TexturePaint(bufferedImage, tR));
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

        return (TclString.newInstance(swkShape.imageName));
    }

    @Override
    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
            throws TclException {
        newName = arg.toString();
        if (newName.equals("")) {
            newValue = null;
        } else {
            newValue = getFromImage(interp, arg);
        }
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if (swkShape.shape != null) {
            swkShape.imageName = newName;
            swkShape.texturePaint = newValue;
        }
    }
}
