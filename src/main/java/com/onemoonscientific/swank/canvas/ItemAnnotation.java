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
import java.awt.*;
import java.awt.geom.*;
import java.util.TreeMap;


/**
 *
 * @author brucejohnson
 */
public class ItemAnnotation extends ItemLine implements TextInterface {
    static TreeMap<String,CanvasParameter> parameterMap = new TreeMap<String,CanvasParameter>();

    static CanvasParameter[] parametersAnno = {
        new FillParameter(), new SmoothParameter(), new DashParameter(),
        new DashPhaseParameter(), new WidthParameter(), new RotateParameter(),
        new ShearParameter(), new TagsParameter(), new StateParameter(), new NodeParameter(),
        new TransformerParameter(), new CapstyleParameter(),
        new JoinstyleParameter(), new ArrowShapeParameter(),
        new TextParameter(), new FontParameter(), new TextcolorParameter(),};

    static {
        initializeParameters(parametersAnno, parameterMap);
    }
    TextParameters textPar = TextParameters.getDefault();

    ItemAnnotation(Shape shape, SwkImageCanvas canvas) {
        super(shape, canvas);
        gPath = (GeneralPath) shape;
        fill = null;
        endPointStyle1 = EndPointStyle.SQUARE;
        endPointStyle2 = EndPointStyle.ARROW;
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
    public String getText() {
        return textPar.getText();
    }

    /**
     *
     * @param newValue
     */
    public void setText(String newValue) {
        textPar = TextParameters.setText(textPar, newValue);
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

    /**
     *
     * @param g2
     */
    @Override
    protected void paintShape(Graphics2D g2) {
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
        double[] xy = {storeCoords[0], storeCoords[1], storeCoords[2], storeCoords[3]};
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
                shapeTransform.transform(xy, 0, xy, 0, xy.length / 2);
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
        float[] anchor = new float[2];
        anchor[1] = 0.5f;
        if (xy[1] > xy[3]) {
            anchor[0] = 1.0f;
        } else {
            anchor[0] = 0.0f;
        }
        setAnchor(anchor);
        textPar.paint(g2, getCanvas().getFontRenderContext(), this, storeCoords[0], storeCoords[1]);

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
        float x1;
        float y1;
        if ((storeCoords == null) || (storeCoords.length != coords.length)) {
            storeCoords = new double[coords.length];
        }

        System.arraycopy(coords, 0, storeCoords, 0, coords.length);
        applyCoordinates();
    }

    @Override
    public String getType() {
        return "annotation";
    }

    @Override
    protected void drawHandles(Graphics2D g2) {
        if (shape != null) {
            double[] xy = {storeCoords[0], storeCoords[1], storeCoords[2], storeCoords[3]};
            AffineTransform shapeTransform = getTransform();
            if (shapeTransform != null) {
                shapeTransform.transform(xy, 0, xy, 0, xy.length / 2);
            }
            drawHandle(g2, (int) xy[0], (int) xy[1]);
            drawHandle(g2, (int) xy[2], (int) xy[3]);
        }
    }

    /**
     *
     * @param testX
     * @param testY
     * @return
     */
    @Override
    protected int hitHandles(double testX, double testY) {
        int hitIndex = -1;
        if (shape != null) {
            double[] xy = {storeCoords[0], storeCoords[1], storeCoords[2], storeCoords[3]};
            AffineTransform shapeTransform = getTransform();
            if (shapeTransform != null) {
                shapeTransform.transform(xy, 0, xy, 0, xy.length / 2);
            }
            if (hitHandle((int) xy[0], (int) xy[1], testX, testY)) {
                hitIndex = 0;
            } else if (hitHandle((int) xy[2], (int) xy[3], testX, testY)) {
                hitIndex = 1;
            }
        }
        return hitIndex;
    }

    /**
     *
     * @param handle
     * @return
     */
    @Override
    protected Cursor getHandleCursor(int handle) {
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
