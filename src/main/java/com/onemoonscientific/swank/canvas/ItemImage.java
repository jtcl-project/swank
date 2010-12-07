/*
 *
 *
 * Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file \"LICENSE\" for information on usage and redistribution
 * of this file.
 * IN NO EVENT SHALL THE AUTHORS OR DISTRIBUTORS BE LIABLE TO
 * ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR
 * CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OF THIS
 * SOFTWARE, ITS DOCUMENTATION, OR ANY DERIVATIVES THEREOF,
 * EVEN IF THE AUTHORS HAVE BEEN ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * THE AUTHORS AND DISTRIBUTORS SPECIFICALLY DISCLAIM ANY
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, AND NON-INFRINGEMENT.  THIS SOFTWARE
 * IS PROVIDED ON AN "AS IS" BASIS, AND THE AUTHORS AND
 * DISTRIBUTORS HAVE NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 *
 */
/*
 * SwkImage.java
 *
 * Created on February 19, 2000, 3:14 PM
 */
/**
 *
 * @author  JOHNBRUC
 * @version
 */
package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.*;

import tcl.lang.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

import java.lang.*;

import java.util.*;

import javax.swing.*;

public class ItemImage extends SwkShape {

    static CanvasParameter[] parameters = {
        new ImageParameter(), new TagsParameter(), new StateParameter(),
        new TransformerParameter(), new NodeParameter(),};
    static Map parameterMap = new TreeMap();

    static {
        initializeParameters(parameters, parameterMap);
    }
    BufferedImage bufferedImage;

    ItemImage(Shape shape, SwkImageCanvas canvas) {
        super(shape, canvas);
        storeCoords = new double[2];
    }

    public void setImage(BufferedImage newImage) {
        bufferedImage = newImage;
    }

    public BufferedImage getImage() {
        return bufferedImage;
    }

    public boolean hitShape(double x1, double y1) {
        boolean hit = false;
        if (bufferedImage != null) {
            AffineTransform aT = new AffineTransform();
            AffineTransform shapeTransform = this.getTransform();

            if (shapeTransform != null) {
                aT.setTransform(shapeTransform);
            }

            double x = storeCoords[0];
            double y = storeCoords[1];
            aT.rotate(this.rotate, x, y);
            double width = bufferedImage.getWidth();
            double height = bufferedImage.getHeight();
            Rectangle2D.Double rf1 = new Rectangle2D.Double(x, y, width, height);
            Rectangle2D rf1d = aT.createTransformedShape(rf1).getBounds2D();
            shape = rf1d;

            hit = shape.contains(x1, y1);
        }
        return hit;
    }

    public void coords(SwkImageCanvas canvas, double[] coords)
            throws SwkException {
        if (coords.length != 2) {
            throw new SwkException("wrong # coordinates: expected 2, got "
                    + coords.length);
        }

        System.arraycopy(coords, 0, storeCoords, 0, 2);
    }

    @Override
    public void paintShape(Graphics2D g2) {
        AffineTransform aT = new AffineTransform();
        aT.translate((int) storeCoords[0], storeCoords[1]);

        if (bufferedImage != null) {
            // FIXME can last argument be null?
            g2.drawImage(bufferedImage, aT, null);
        }

    }

    public CanvasParameter[] getParameters() {
        return parameters;
    }

    public Map getParameterMap() {
        return parameterMap;
    }

    public String getType() {
        return "image";
    }
}
