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
 * SwkBitmap.java
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
import javax.swing.*;
import java.util.TreeMap;

/**
 * 
 * @author brucejohnson
 */
public class ItemBitmap extends SwkShape {
    static TreeMap<String,CanvasParameter> parameterMap = new TreeMap<String,CanvasParameter>();

    static CanvasParameter[] parameters = {
        new BitmapParameter(), new TagsParameter(), new StateParameter(), new NodeParameter(),
        new TransformerParameter(),};

    static {
        initializeParameters(parameters, parameterMap);
    }
    ImageIcon image;

    ItemBitmap(Shape shape, SwkImageCanvas canvas) {
        super(shape, canvas);
        storeCoords = new double[2];
    }

    /**
     *
     * @param newImage
     */
    protected void setImageIcon(ImageIcon newImage) {
        image = newImage;
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
     * @return
     */
    protected ImageIcon getImageIcon() {
        return image;
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
        if (image != null) {
            int imageWidth = image.getIconWidth();
            int imageHeight = image.getIconHeight();
            g2.drawImage(image.getImage(),
                    (int) storeCoords[0] - (imageWidth / 2),
                    (int) storeCoords[1] - (imageHeight / 2), null);
        }

    }

    /**
     *
     * @return
     */
    public String getType() {
        return "bitmap";
    }
}
