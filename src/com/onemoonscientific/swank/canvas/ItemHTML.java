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

import tcl.lang.*;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;

import java.lang.*;

import java.text.*;

import java.util.*;

import javax.swing.*;


public class ItemHTML extends SwkShape implements TextInterface {
    static CanvasParameter[] parameters = {
        new TextParameter(), new AnchorParameter(), new FontParameter(),
        new WidthParameter(), new FillParameter(), new TagsParameter(),
        new TransformerParameter(), new OutlineParameter(),new RotateParameter(),
    };
    static Map parameterMap = new TreeMap();

    static {
        initializeParameters(parameters, parameterMap);
    }
    TextParameters textPar = TextParameters.getDefault();

    double x = 0.0;
    double y = 0.0;
    int[] ends = null;
    Rectangle2D.Double rect2D = new Rectangle2D.Double();
    JLabel jLabel = new JLabel();

    ItemHTML(Shape shape, SwkImageCanvas canvas) {
        super(shape, canvas);
        rect2D = (Rectangle2D.Double) shape;
        width = 0;
        storeCoords = new double[4];
        setFont(new Font("Courier", Font.PLAIN, 12));
        fill = Color.BLACK;
        jLabel.setVerticalAlignment(SwingConstants.TOP);
    }
    public void coords(SwkImageCanvas canvas, double[] coords)
            throws SwkException {
        if (coords.length != 4) {
            throw new SwkException("wrong # coordinates: expected 4, got " +
                coords.length);
        }

        if ((storeCoords == null) || (storeCoords.length != coords.length)) {
            storeCoords = new double[coords.length];
        }

        System.arraycopy(coords, 0, storeCoords, 0, coords.length);
        applyCoordinates();
    }
   public void applyCoordinates() {
        checkCoordinates(storeCoords);
        rect2D.setFrame(storeCoords[0], storeCoords[1],
                storeCoords[2] - storeCoords[0], storeCoords[3] - storeCoords[1]);
    }

    public void checkCoordinates(double[] coords) {
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


    public void paintShape(Graphics2D g2) {
        paint(g2, getCanvas().getFontRenderContext());
    }
    public CanvasParameter[] getParameters() {
        return parameters;
    }

    public Map getParameterMap() {
        return parameterMap;
    }

    public String getType() {
        return "htext";
    }

    public double getX() {
        return storeCoords[0];
    }

    public void setX(final double x) {
        storeCoords[0] = x;
    }

    public double getY() {
        return storeCoords[1];
    }

    public void setY(final double y) {
        storeCoords[1] = y;
    }

    public String getText() {
        return jLabel.getText();
    }

    public void setText(final String text) {
        jLabel.setText(text);
    }


    public float[] getAnchor() {
        return textPar.getAnchor();
    }

    public void setAnchor(float[] newValue) {
        textPar = TextParameters.setAnchor(textPar, newValue);
    }

    public Font getFont() {
        return textPar.getFont();
    }

    public void setFont(Font newValue) {
        textPar = TextParameters.setFont(textPar, newValue);
    }

    public Color getTextColor() {
        return textPar.getTextColor();
    }

    public void setTextColor(Color newValue) {
        textPar = TextParameters.setTextColor(textPar, newValue);
    }
    public void paint(Graphics2D g2, FontRenderContext fRC) {
        if (this.getFont() != null) {
            g2.setFont(this.getFont());
        }


        AffineTransform aT = new AffineTransform();
        AffineTransform shapeTransform = this.getTransform();

        if (shapeTransform != null) {
            aT.setTransform(shapeTransform);
        }
        jLabel.setVisible(true);
        jLabel.setSize((int) rect2D.getWidth(),(int) rect2D.getHeight());
        g2.translate((int) rect2D.getX(), (int) rect2D.getY());

        if (fill != null) {
            g2.setColor(fill);
            g2.fillRect(0,0,(int) rect2D.getWidth(),(int) rect2D.getHeight());
        }

        jLabel.setForeground(getTextColor());
        

        //jLabel.setText("hello");
        jLabel.paint((Graphics) g2);
        
        g2.translate(-(int) rect2D.getX(), -(int) rect2D.getY());
    }
}