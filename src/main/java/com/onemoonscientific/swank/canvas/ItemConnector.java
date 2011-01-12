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
import java.awt.geom.*;

public class ItemConnector extends ItemLine implements TextInterface {

    TextParameters textPar = TextParameters.getDefault();
    static CanvasParameter[] parameters = {
        new FillParameter(), new SmoothParameter(), new DashParameter(),
        new DashPhaseParameter(), new WidthParameter(),
        new TagsParameter(), new StateParameter(), new NodeParameter(),
        new TransformerParameter(), new CapstyleParameter(),
        new JoinstyleParameter(), new ArrowParameter(), new ArrowShapeParameter(), new EndstyleParameter(), new StartstyleParameter(),
        new TextParameter(), new FontParameter(), new AnchorParameter(), new TextcolorParameter(),};
    String startCon = "";
    String endCon = "";
    double textX = 0.0;
    double textY = 0.0;

    ItemConnector(Shape shape, SwkImageCanvas canvas) {
        super(shape, canvas);
        storeCoords = new double[4];
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
        applyCoordinates();
        if ((endPointStyle1 == EndPointStyle.NONE) && (endPointStyle2 == EndPointStyle.NONE)) {
            if (shapeTransform != null) {
                g2.draw(shapeTransform.createTransformedShape(shape));
            } else {
                g2.draw(shape);
            }
        } else {

            // draw line
            if (shapeTransform != null) {
                g2.draw(shapeTransform.createTransformedShape(shape));
            } else {
                g2.draw(shape);
            }

            // draw first arrow head
            if (firstArrowPath != null) {
                if (shapeTransform != null) {
                    //g2.fill(shapeTransform.createTransformedShape(firstArrowPath));
                    //g2.draw(shapeTransform.createTransformedShape(firstArrowPath));
                } else {
                    //g2.fill(firstArrowPath);
                    //g2.draw(firstArrowPath);
                }
                g2.fill(firstArrowPath);
                g2.setStroke(bstroke);
                g2.draw(firstArrowPath);
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

        textPar.paint(g2, getCanvas().getFontRenderContext(), this, textX, textY);
    }

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
    public void applyCoordinates() {
        double[] shapePars = {arrowShapeA, arrowShapeB, arrowShapeC, width};
        AffineTransform aT = new AffineTransform();
        SwkShape shape1 = null;
        SwkShape shape2 = null;
        try {
            shape1 = canvas.getShape(startCon);
            shape2 = canvas.getShape(endCon);
        } catch (SwkException swkE) {
            System.out.println(swkE.getMessage());
            return;
        }
        double x1, y1, x2, y2;
        if ((shape1.shape != null) && (shape2.shape != null)) {
            Rectangle2D bounds1 = shape1.shape.getBounds2D();
            Rectangle2D bounds2 = shape2.shape.getBounds2D();
            x1 = bounds1.getMinX() + bounds1.getWidth() * storeCoords[0];
            y1 = bounds1.getMinY() + bounds1.getHeight() * storeCoords[1];
            x2 = bounds2.getMinX() + bounds2.getWidth() * storeCoords[2];
            y2 = bounds2.getMinY() + bounds2.getHeight() * storeCoords[3];
        } else {
            return;
        }
        double[] tempCoords;
        boolean segmented = true;
        if (segmented) {
            if (x2 > (x1 + 10)) {
                double xm = (x1 + x2) / 2;
                double ym = (y1 + y2) / 2;
                double[] coords = {x1, y1, xm, y1, xm, y2, x2, y2};
                tempCoords = coords;
                textX = xm;
                textY = ym;
            } else {
                double dX = 15;
                double x1a = x1 + dX;
                double y1a = y1;
                double x2a = x2 - dX;
                double ym = (y1 + y2) / 2.0;
                double[] coords = {x1, y1, x1a, y1a, x1a, ym, x2a, ym, x2a, y2, x2, y2};
                tempCoords = coords;
                textX = (x1a + x2a) / 2.0;
                textY = ym;
            }
        } else {
            double[] coords = {x1, y1, x2, y2};
            tempCoords = coords;
            textX = (tempCoords[0] + tempCoords[2]) / 2.0;
            textY = (tempCoords[1] + tempCoords[3]) / 2.0;
        }


        if ((smooth == null) || smooth.equals("")) {
            genPath(tempCoords);
        } else {
            genSmoothPath(tempCoords);
        }
        shape = aT.createTransformedShape(gPath);

        if ((endPointStyle1 != EndPointStyle.NONE) || (endPointStyle2 != EndPointStyle.NONE)) {
            double[] arrowFirstCoords = null;
            double[] arrowLastCoords = null;
            if (endPointStyle1 != EndPointStyle.NONE) {
                arrowFirstCoords = endPointStyle1.addArrowFirst(shapePars, tempCoords);
                if (firstArrowPath == null) {
                    firstArrowPath = new GeneralPath();
                }
            } else {
                firstArrowPath = null;
            }
            if (endPointStyle2 != EndPointStyle.NONE) {
                arrowLastCoords = endPointStyle2.addArrowLast(shapePars, tempCoords);
                if (lastArrowPath == null) {
                    lastArrowPath = new GeneralPath();
                }
            } else {
                lastArrowPath = null;
            }

            if ((smooth == null) || smooth.equals("")) {
                genPath(tempCoords);
            } else {
                genSmoothPath(tempCoords);
            }
            shape = aT.createTransformedShape(gPath);

            if (endPointStyle1 != EndPointStyle.NONE) {
                if (endPointStyle1 == EndPointStyle.CIRCLE) {
                    addArrowPathCircle(firstArrowPath, arrowFirstCoords);
                } else {
                    addArrowPath(firstArrowPath, arrowFirstCoords);
                }
            }
            if (endPointStyle2 != EndPointStyle.NONE) {
                if (endPointStyle2 == EndPointStyle.CIRCLE) {
                    addArrowPathCircle(lastArrowPath, arrowLastCoords);
                } else {
                    addArrowPath(lastArrowPath, arrowLastCoords);
                }
            }
        }

    }
}
