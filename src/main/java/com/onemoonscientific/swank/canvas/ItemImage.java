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
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.TreeMap;

/**
 *
 * @author brucejohnson
 */
public class ItemImage extends SwkShape {
    static TreeMap<String,CanvasParameter> parameterMap = new TreeMap<String,CanvasParameter>();

    static CanvasParameter[] parameters = {
        new ImageParameter(), new TagsParameter(), new StateParameter(),
        new TransformerParameter(), new NodeParameter(),};

    static {
        initializeParameters(parameters, parameterMap);
    }
    BufferedImage bufferedImage;

    ItemImage(Shape shape, SwkImageCanvas canvas) {
        super(shape, canvas);
        storeCoords = new double[2];
    }
   
    /**
     *
     * @return
     */
    public TreeMap<String,CanvasParameter> getParameterMap() {
        return parameterMap;
    }

    /**
     *
     * @param newImage
     */
    protected void setImage(BufferedImage newImage) {
        bufferedImage = newImage;
    }

    /**
     *
     * @return
     */
    protected BufferedImage getImage() {
        return bufferedImage;
    }

    @Override
    protected boolean hitShape(double x1, double y1) {
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
            double imageWidth = bufferedImage.getWidth();
            double imageHeight = bufferedImage.getHeight();
            Rectangle2D.Double rf1 = new Rectangle2D.Double(x, y, imageWidth, imageHeight);
            Rectangle2D rf1d = aT.createTransformedShape(rf1).getBounds2D();
            shape = rf1d;

            hit = shape.contains(x1, y1);
        }
        return hit;
    }

    /**
     *
     * @param canvas
     * @param coords
     * @throws SwkException
     */
    @Override
    public void coords(SwkImageCanvas canvas, double[] coords)
            throws SwkException {
        if (coords.length != 2) {
            throw new SwkException("wrong # coordinates: expected 2, got "
                    + coords.length);
        }

        System.arraycopy(coords, 0, storeCoords, 0, 2);
    }

    /**
     *
     * @param g2
     */
    @Override
    protected void paintShape(Graphics2D g2) {
        AffineTransform aT = new AffineTransform();
        aT.translate((int) storeCoords[0], storeCoords[1]);

        if (bufferedImage != null) {
            // FIXME can last argument be null?
            g2.drawImage(bufferedImage, aT, null);
        }

    }

    /**
     *
     * @return
     */
    public String getType() {
        return "image";
    }
}
