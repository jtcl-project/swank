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
 * SwkEllipse.java
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
import java.awt.font.*;
import java.awt.geom.*;
import java.util.TreeMap;


import javax.swing.*;

/**
 *
 * @author brucejohnson
 */
public class ItemHTML extends SwkShape implements TextInterface {
    static TreeMap<String,CanvasParameter> parameterMap = new TreeMap<String,CanvasParameter>();

    static CanvasParameter[] parameters = {
        new TextParameter(), new AnchorParameter(), new FontParameter(), new StateParameter(),
        new WidthParameter(), new FillParameter(), new GradientParameter(), new TagsParameter(),
        new TransformerParameter(), new OutlineParameter(), new RotateParameter(), new NodeParameter(),};

    static {
        initializeParameters(parameters, parameterMap);
    }
    TextParameters textPar = TextParameters.getDefault();
    Rectangle2D.Double rect2D = new Rectangle2D.Double();
    JLabel jLabel = new JLabel();

    ItemHTML(Shape shape, SwkImageCanvas canvas) {
        super(shape, canvas);
        rect2D = (Rectangle2D.Double) shape;
        width = 0;
        storeCoords = new double[4];
        setFont(new Font("Courier", Font.PLAIN, 12));
        fill = Color.WHITE;
        jLabel.setVerticalAlignment(SwingConstants.TOP);
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
     * @param canvas
     * @param coords
     * @throws SwkException
     */
    @Override
    public void coords(SwkImageCanvas canvas, double[] coords)
            throws SwkException {
        if (coords.length != 4) {
            throw new SwkException("wrong # coordinates: expected 4, got "
                    + coords.length);
        }

        if ((storeCoords == null) || (storeCoords.length != coords.length)) {
            storeCoords = new double[coords.length];
        }

        System.arraycopy(coords, 0, storeCoords, 0, coords.length);
        applyCoordinates();
    }

    /**
     *
     */
    @Override
    protected void applyCoordinates() {
        checkCoordinates(storeCoords);
        AffineTransform aT = new AffineTransform();
        /*
        aT.translate(storeCoords[0], storeCoords[1]);
        aT.shear(xShear, yShear);
        aT.translate(-storeCoords[0], -storeCoords[1]);
        aT.rotate(rotate, ((storeCoords[0] + storeCoords[2]) / 2.0),
        ((storeCoords[1] + storeCoords[3]) / 2.0));
         */
        aT.translate(-storeCoords[0], -storeCoords[1]);
        genGradient(aT);
        rect2D.setFrame(storeCoords[0], storeCoords[1],
                storeCoords[2] - storeCoords[0], storeCoords[3] - storeCoords[1]);
    }

    /**
     *
     * @param coords
     */
    @Override
    protected void checkCoordinates(double[] coords) {
        double hold;

        if ((coords == null) || (coords.length != 4)) {
            return;
        }

        if (coords[0] > coords[2]) {
            hold = coords[0];
            coords[0] = coords[2];
            coords[2] = hold;
        }

        if (coords[1] > coords[3]) {
            hold = coords[1];
            coords[1] = coords[3];
            coords[3] = hold;
        }
    }

    /**
     *
     * @param g2
     */
    @Override
    protected void paintShape(Graphics2D g2) {
        paint(g2, getCanvas().getFontRenderContext());
    }

    /**
     *
     * @return
     */
    public String getType() {
        return "htext";
    }

    /**
     *
     * @return
     */
    protected double getX() {
        return storeCoords[0];
    }

    /**
     *
     * @param x
     */
    protected void setX(final double x) {
        storeCoords[0] = x;
    }

    /**
     *
     * @return
     */
    protected double getY() {
        return storeCoords[1];
    }

    /**
     *
     * @param y
     */
    protected void setY(final double y) {
        storeCoords[1] = y;
    }

    /**
     *
     * @return
     */
    public String getText() {
        return jLabel.getText();
    }

    /**
     *
     * @param text
     */
    public void setText(final String text) {
        jLabel.setText(text);
    }

    /**
     *
     * @return
     */
    public float[] getAnchor() {
        return textPar.getAnchor();
    }

    /**
     *
     * @param newValue
     */
    public void setAnchor(float[] newValue) {
        textPar = TextParameters.setAnchor(textPar, newValue);
    }

    /**
     *
     * @return
     */
    public Font getFont() {
        return textPar.getFont();
    }

    /**
     *
     * @param newValue
     */
    public void setFont(Font newValue) {
        textPar = TextParameters.setFont(textPar, newValue);
    }

    /**
     *
     * @return
     */
    public Color getTextColor() {
        return textPar.getTextColor();
    }

    /**
     *
     * @param newValue
     */
    public void setTextColor(Color newValue) {
        textPar = TextParameters.setTextColor(textPar, newValue);
    }

    @Override
    protected boolean hitShape(double x1, double y1) {
        boolean result = false;
        if (shape != null) {
            Shape shape2 = shape;
            AffineTransform shapeTransform = this.getTransform();
            if (shapeTransform != null) {
                shape2 = shapeTransform.createTransformedShape(shape);
            }
            result = shape2.contains(x1, y1);
        }
        return result;
    }

    /**
     *
     * @param g2
     * @param fRC
     */
    protected void paint(Graphics2D g2, FontRenderContext fRC) {
        if (this.getFont() != null) {
            g2.setFont(this.getFont());
        }


        AffineTransform aT = new AffineTransform();
        AffineTransform shapeTransform = this.getTransform();

        if (shapeTransform != null) {
            aT.setTransform(shapeTransform);
        }

        Shape shape2 = shape;
        if (shapeTransform != null) {
            shape2 = shapeTransform.createTransformedShape(shape);
        }
        Rectangle2D rectBounds = shape2.getBounds();





        jLabel.setVisible(true);
        jLabel.setSize((int) rectBounds.getWidth(), (int) rectBounds.getHeight());
        g2.translate((int) rectBounds.getX(), (int) rectBounds.getY());

        if (getTexturePaint() != null) {
            g2.setPaint(getTexturePaint());
            g2.fillRect(0, 0, (int) rectBounds.getWidth(), (int) rectBounds.getHeight());
        } else if (getFillGradient() != null) {
            g2.setPaint(getFillGradient());
            g2.fillRect(0, 0, (int) rectBounds.getWidth(), (int) rectBounds.getHeight());
        } else if (getFill() != null) {
            g2.setPaint(getFill());
            g2.fillRect(0, 0, (int) rectBounds.getWidth(), (int) rectBounds.getHeight());
        }

        jLabel.setForeground(getTextColor());


        //jLabel.setText("hello");
        jLabel.paint((Graphics) g2);

        g2.translate(-(int) rectBounds.getX(), -(int) rectBounds.getY());
    }
}
