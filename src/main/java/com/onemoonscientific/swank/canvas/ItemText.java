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
import java.text.*;

public class ItemText extends SwkShape implements TextInterface {

    static BreakIterator wordIterator = BreakIterator.getWordInstance();
    static CanvasParameter[] parameters = {
        new TextParameter(), new AnchorParameter(), new FontParameter(),
        new WidthParameter(), new FillParameter(), new TagsParameter(), new StateParameter(),
        new TransformerParameter(), new RotateParameter(), new NodeParameter(),};

    static {
        initializeParameters(parameters, parameterMap);
    }
    TextParameters textPar = TextParameters.getDefault();

    ItemText(Shape shape, SwkImageCanvas canvas) {
        super(shape, canvas);
        width = 0;
        storeCoords = new double[2];
        setFont(new Font("Courier", Font.PLAIN, 12));
        fill = Color.BLACK;
    }

    @Override
    public void coords(SwkImageCanvas canvas, double[] coords)
            throws SwkException {
        if (coords.length != 2) {
            throw new SwkException("wrong # coordinates: expected 2, got "
                    + coords.length);
        }

        setX(coords[0]);
        setY(coords[1]);
    }

    @Override
    public void paintShape(Graphics2D g2) {
        shape = textPar.paint(g2, getCanvas().getFontRenderContext(), this, storeCoords[0], storeCoords[1]);
    }


    public String getType() {
        return "text";
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

    @Override
    public boolean hitShape(double x1, double y1) {
        boolean result = false;
        if (shape != null) {
            result = shape.contains(x1, y1);
        }
        return result;
    }

    // FIXME getting bounds of multiline text not correct
    public Rectangle2D getBounds() {
        if (shape != null) {
            return shape.getBounds2D();
        } else {

            String text = getText();

            if (text == null) {
                return new Rectangle2D.Float((float) getX(), (float) getY(), 1, 1);
            }

            FontRenderContext fRC = canvas.getFontRenderContext();

            if (fRC == null) {
                return new Rectangle2D.Float((float) getX(), (float) getY(), 1, 1);
            }

            AffineTransform aT = new AffineTransform();
            Font font = getFont();
            float width1 = (float) (font.getStringBounds(text, fRC).getWidth());
            float height1 = (float) (font.getStringBounds(text, fRC).getHeight());
            float width2 = (float) (width1 * getAnchor()[1]);
            float height2 = (float) (height1 * getAnchor()[0]);
            Rectangle2D rf1 = new Rectangle2D.Double((float) (getX() - width2),
                    (float) (getY() - height1 + height2), width1, height1);
            rf1 = aT.createTransformedShape(rf1).getBounds2D();

            return rf1;
        }

    }

    @Override
    public void drawHandles(Graphics2D g2) {
        if (shape != null) {
            Rectangle2D bounds = shape.getBounds2D();
            double y1 = bounds.getMinY();
            double x2 = bounds.getMaxX();
            double y2 = bounds.getMaxY();
            double ym = (y1 + y2) / 2;
            double[] xy = {x2, ym};
            for (int i = 0; i < xy.length; i += 2) {
                drawHandle(g2, (int) xy[i], (int) xy[i + 1]);
            }
        }
    }

    @Override
    public int hitHandles(double testX, double testY) {
        int hitIndex = -1;
        if (shape != null) {
            Rectangle2D bounds = shape.getBounds2D();
            double y1 = bounds.getMinY();
            double x2 = bounds.getMaxX();
            double y2 = bounds.getMaxY();
            double ym = (y1 + y2) / 2;
            double[] xy = {x2, ym};

            for (int i = 0; i < xy.length; i += 2) {
                if (hitHandle((int) xy[i], (int) xy[i + 1], testX, testY)) {
                    hitIndex = i / 2;
                    break;
                }
            }
        }
        return hitIndex;
    }

    @Override
    public Cursor getHandleCursor(int handle) {
        final Cursor cursor;
        switch (handle) {
            case 0:
                cursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
                break;
            default:
                cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        }
        return cursor;
    }
}
