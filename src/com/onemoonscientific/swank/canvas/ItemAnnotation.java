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
 * SwkLine.java
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

import java.lang.*;

import java.util.*;

public class ItemAnnotation extends ItemLine implements TextInterface {

    static CanvasParameter[] parameters = {
        new FillParameter(), new SmoothParameter(), new DashParameter(),
        new DashPhaseParameter(), new WidthParameter(), new RotateParameter(),
        new ShearParameter(), new TagsParameter(), new StateParameter(),new NodeParameter(),
        new TransformerParameter(), new CapstyleParameter(),
        new JoinstyleParameter(), new ArrowShapeParameter(),
        new TextParameter(), new FontParameter(), new TextcolorParameter(),
    };
    static Map parameterMap = new TreeMap();

    static {
        initializeParameters(parameters, parameterMap);
    }
    TextParameters textPar = TextParameters.getDefault();

    ItemAnnotation(Shape shape, SwkImageCanvas canvas) {
        super(shape, canvas);
        gPath = (GeneralPath) shape;
        fill = null;
        endPointStyle1 = EndPointStyle.SQUARE;
        endPointStyle2 = EndPointStyle.ARROW;
    }
    public String getText() {
        return textPar.getText();
    }

    public void setText(String newValue) {
        textPar = TextParameters.setText(textPar, newValue);
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

    public void paintShape(Graphics2D g2) {
        if (stroke != null) {
            g2.setStroke(stroke);
        } else {
            g2.setStroke(bstroke);
        }
        if (storeCoords == null) {
            return;
        }
        AffineTransform shapeTransform = getTransform();

        g2.setPaint(outline);
        double[] xy = {storeCoords[0],storeCoords[1],storeCoords[2],storeCoords[3]};
        if ((endPointStyle1 == EndPointStyle.NONE) && (endPointStyle2 == EndPointStyle.NONE)) {
            if (shapeTransform != null) {
                g2.draw(shapeTransform.createTransformedShape(shape));
            } else {
                g2.draw(shape);
            }
        } else {
            applyCoordinates();

            // draw line
            if (shapeTransform != null) {
                g2.draw(shapeTransform.createTransformedShape(shape));
                shapeTransform.transform(xy,0,xy,0,xy.length/2);
            } else {
                g2.draw(shape);
            }

            if (lastArrowPath != null) {
                if (shapeTransform != null) {
                    //g2.fill(shapeTransform.createTransformedShape(lastArrowPath));
                    //g2.draw(shapeTransform.createTransformedShape(lastArrowPath));
                } else {
                    //g2.fill(lastArrowPath);
                    //g2.draw(lastArrowPath);
                }
                g2.fill(lastArrowPath);
                g2.setStroke(bstroke);
                g2.draw(lastArrowPath);
            }
        }
        float[]  anchor = new float[2];
        anchor[1] = 0.5f;
        if (xy[1] > xy[3]) {
            anchor[0] = 1.0f;
        } else {
            anchor[0] = 0.0f;
        }
        setAnchor(anchor);
        textPar.paint(g2, getCanvas().getFontRenderContext(), this,storeCoords[0],storeCoords[1]);

    }

    public void coords(SwkImageCanvas canvas, double[] coords)
            throws SwkException {
        float x1;
        float y1;
        if ((storeCoords == null) || (storeCoords.length != coords.length)) {
            storeCoords = new double[coords.length];
        }

        System.arraycopy(coords, 0, storeCoords, 0, coords.length);
        applyCoordinates();
    }

    public CanvasParameter[] getParameters() {
        return parameters;
    }

    public Map getParameterMap() {
        return parameterMap;
    }

    public String getType() {
        return "annotation";
    }

    public void drawHandles(Graphics2D g2) {
        if (shape != null) {
            double[] xy = {storeCoords[0],storeCoords[1],storeCoords[2],storeCoords[3]};
            AffineTransform shapeTransform = getTransform();
            if (shapeTransform != null) {
                shapeTransform.transform(xy,0,xy,0,xy.length/2);
            }
            drawHandle(g2, (int) xy[0],(int) xy[1]);
            drawHandle(g2, (int) xy[2],(int) xy[3]);
        }
    }

    public int hitHandles(double testX, double testY) {
        int hitIndex = -1;
        if (shape != null) {
            double[] xy = {storeCoords[0],storeCoords[1],storeCoords[2],storeCoords[3]};
            AffineTransform shapeTransform = getTransform();
            if (shapeTransform != null) {
                shapeTransform.transform(xy,0,xy,0,xy.length/2);
            }
            if (hitHandle((int) xy[0],(int) xy[1], testX, testY)) {
                hitIndex = 0;
            } else if (hitHandle((int) xy[2],(int) xy[3], testX, testY)) {
                hitIndex = 1;
            }
        }
        return hitIndex;
    }

    public Cursor getHandleCursor(int handle) {
        final Cursor cursor;
        switch (handle) {
            case 0:
                cursor = Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
                break;
            case 1:
                cursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
                break;
            default:
                cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        }
        return cursor;
    }
}
