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

public class ItemLine extends SwkShape {

    public enum EndPointStyle {

        NONE("none") {
        },
        SQUARE("square") {

            @Override
            double[] getPoly() {
                return (new double[12]);
            }

            @Override
            double[] calcShape(double[] shapePars, double x1, double y1, double x2, double y2, double[] poly) {
                double[] thetaTrig = calcSquare(shapePars, x1, y1, x2, y2, poly);
                return thetaTrig;
            }
        },
        CIRCLE("circle") {

            @Override
            double[] getPoly() {
                return (new double[4]);
            }

            @Override
            double[] calcShape(double[] shapePars, double x1, double y1, double x2, double y2, double[] poly) {
                double[] thetaTrig = calcCircle(shapePars, x1, y1, x2, y2, poly);
                return thetaTrig;
            }
        },
        DIAMOND("diamond") {

            @Override
            double[] getPoly() {
                return (new double[10]);
            }

            @Override
            double[] calcShape(double[] shapePars, double x1, double y1, double x2, double y2, double[] poly) {
                double[] thetaTrig = calcDiamond(shapePars, x1, y1, x2, y2, poly);
                return thetaTrig;
            }
        },
        ARROW("arrow") {

            @Override
            double[] getPoly() {
                return (new double[12]);
            }

            @Override
            double[] calcShape(double[] shapePars, double x1, double y1, double x2, double y2, double[] poly) {
                double[] thetaTrig = calcArrow(shapePars, x1, y1, x2, y2, poly);
                return thetaTrig;
            }
        };
        private String description;

        EndPointStyle(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        double[] calcShape(double[] shapePars, double x1, double y1, double x2, double y2, double[] poly) {
            double[] thetaTrig = calcSquare(shapePars, x1, y1, x2, y2, poly);
            return thetaTrig;
        }

        double[] getPoly() {
            return (new double[12]);
        }

        double[] addArrowFirst(double[] shapePars, double[] storeCoords) {
            double x1 = storeCoords[0];
            double y1 = storeCoords[1];
            double x2 = storeCoords[2];
            double y2 = storeCoords[3];
            double[] poly = getPoly();

            double[] thetaTrig = calcShape(shapePars, x1, y1, x2, y2, poly);

            double backup = calcBackup(shapePars);
            storeCoords[0] = poly[0] - backup * thetaTrig[0];
            storeCoords[1] = poly[1] - backup * thetaTrig[1];
            return poly;
        }

        double[] addArrowLast(double[] shapePars, double[] storeCoords) {
            int nElems = storeCoords.length;
            double x1 = storeCoords[nElems - 4];
            double y1 = storeCoords[nElems - 3];
            double x2 = storeCoords[nElems - 2];
            double y2 = storeCoords[nElems - 1];
            double[] poly = getPoly();

            double[] thetaTrig = calcShape(shapePars, x2, y2, x1, y1, poly);

            double backup = calcBackup(shapePars);
            storeCoords[nElems - 2] = poly[0] - backup * thetaTrig[0];
            storeCoords[nElems - 1] = poly[1] - backup * thetaTrig[1];
            return poly;
        }

        double calcBackup(double[] shapePars) {
            double shapeA = shapePars[0] + 0.001;
            double shapeB = shapePars[1] + 0.001;
            double width = shapePars[3];
            double shapeC = shapePars[2] + width / 2.0 + 0.001;
            double fracHeight = (width / 2.0) / shapeC;
            double backup = fracHeight * shapeB + shapeA * (1.0 - fracHeight) / 2.0;
            return backup;
        }
    }
    static CanvasParameter[] parameters = {
        new FillParameter(), new SmoothParameter(), new DashParameter(),
        new DashPhaseParameter(), new WidthParameter(), new RotateParameter(),
        new ShearParameter(), new TagsParameter(), new StateParameter(), new NodeParameter(),
        new TransformerParameter(), new CapstyleParameter(),
        new JoinstyleParameter(), new ArrowParameter(), new ArrowShapeParameter(), new EndstyleParameter(), new StartstyleParameter()
    };

    static {
        initializeParameters(parameters, parameterMap);
    }
    GeneralPath gPath = null;
    GeneralPath firstArrowPath = null;
    GeneralPath lastArrowPath = null;
    boolean closePath = false;
    String smooth = "";
    double smoothValue = 1.0;
    final static private int PTS_IN_ARROW = 6;
    boolean arrowFirst = false;
    boolean arrowLast = false;
    double arrowShapeA = 8.0;
    double arrowShapeB = 10.0;
    double arrowShapeC = 3.0;
    EndPointStyle endPointStyle1 = EndPointStyle.NONE;
    EndPointStyle endPointStyle2 = EndPointStyle.NONE;

    ItemLine(Shape shape, SwkImageCanvas canvas) {
        super(shape, canvas);
        gPath = (GeneralPath) shape;
        fill = null;
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
        aT.translate(storeCoords[0], storeCoords[1]);
        aT.shear(xShear, yShear);
        aT.translate(-storeCoords[0], -storeCoords[1]);
        // should rotation be about average coordinate (could be more than 2 points)
        aT.rotate(rotate, ((storeCoords[0] + storeCoords[2]) / 2.0),
                ((storeCoords[1] + storeCoords[3]) / 2.0));
        AffineTransform shapeTransform = getTransform();

        if ((smooth == null) || smooth.equals("")) {
            genPath(storeCoords);
        } else {
            genSmoothPath(storeCoords);
        }
        shape = aT.createTransformedShape(gPath);

        if ((endPointStyle1 != EndPointStyle.NONE) || (endPointStyle2 != EndPointStyle.NONE)) {
            double[] tempCoords = new double[storeCoords.length];

            if (shapeTransform != null) {
                shapeTransform.transform(storeCoords, 0, tempCoords, 0, storeCoords.length / 2);
            } else {
                System.arraycopy(storeCoords, 0, tempCoords, 0, storeCoords.length);
            }
            aT.transform(tempCoords, 0, tempCoords, 0, tempCoords.length / 2);
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
            if (shapeTransform != null) {
                // we need to put the now shortened coordinates back into the original transform
                // if we can't generate the inverse transform, we just use the original, unshortened coordinates
                try {
                    shapeTransform.inverseTransform(tempCoords, 0, tempCoords, 0, tempCoords.length / 2);
                } catch (java.awt.geom.NoninvertibleTransformException niTE) {
                    System.arraycopy(storeCoords, 0, tempCoords, 0, storeCoords.length);
                }
            }

            if ((smooth == null) || smooth.equals("")) {
                genPath(tempCoords);
            } else {
                genSmoothPath(tempCoords);
            }
            shape = gPath;
            // shape = aT.createTransformedShape(gPath);

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

    double calcBackup() {
        double shapeA = arrowShapeA + 0.001;
        double shapeB = arrowShapeB + 0.001;
        double shapeC = arrowShapeC + width / 2.0 + 0.001;
        double fracHeight = (width / 2.0) / shapeC;
        double backup = fracHeight * shapeB + shapeA * (1.0 - fracHeight) / 2.0;
        return backup;
    }

    double[] addArrowFirst(double[] shapePars, double[] storeCoords) {
        double x1 = storeCoords[0];
        double y1 = storeCoords[1];
        double x2 = storeCoords[2];
        double y2 = storeCoords[3];
        double[] poly = new double[PTS_IN_ARROW * 2];

        double[] thetaTrig = calcSquare(shapePars, x1, y1, x2, y2, poly);

        double backup = calcBackup();
        storeCoords[0] = poly[0] - backup * thetaTrig[0];
        storeCoords[1] = poly[1] - backup * thetaTrig[1];
        return poly;
    }

    double[] addArrowLast(double[] shapePars, double[] storeCoords) {
        int nElems = storeCoords.length;
        double x1 = storeCoords[nElems - 4];
        double y1 = storeCoords[nElems - 3];
        double x2 = storeCoords[nElems - 2];
        double y2 = storeCoords[nElems - 1];
        double[] poly = new double[PTS_IN_ARROW * 2];

        double[] thetaTrig = calcSquare(shapePars, x2, y2, x1, y1, poly);

        double backup = calcBackup();
        storeCoords[nElems - 2] = poly[0] - backup * thetaTrig[0];
        storeCoords[nElems - 1] = poly[1] - backup * thetaTrig[1];
        return poly;
    }

    static double[] calcArrow(double[] shapePars, double x1, double y1, double x2, double y2, double[] poly) {
        double shapeA = shapePars[0] + 0.001;
        double shapeB = shapePars[1] + 0.001;
        double width = shapePars[3];
        double shapeC = shapePars[2] + width / 2.0 + 0.001;
        poly[0] = poly[10] = x1;
        poly[1] = poly[11] = y1;
        double dx = poly[0] - x2;
        double dy = poly[1] - y2;
        double length = Math.hypot(dx, dy);
        double sinTheta = 0.0;
        double cosTheta = 0.0;
        if (length != 0) {
            sinTheta = dy / length;
            cosTheta = dx / length;
        }
        double vertX = poly[0] - shapeA * cosTheta;
        double vertY = poly[1] - shapeA * sinTheta;
        double temp = shapeC * sinTheta;
        poly[2] = poly[0] - shapeB * cosTheta + temp;
        poly[8] = poly[2] - 2 * temp;
        temp = shapeC * cosTheta;
        poly[3] = poly[1] - shapeB * sinTheta - temp;
        poly[9] = poly[3] + 2 * temp;
        double fracHeight = (width / 2.0) / shapeC;
        poly[4] = poly[2] * fracHeight + vertX * (1.0 - fracHeight);
        poly[5] = poly[3] * fracHeight + vertY * (1.0 - fracHeight);
        poly[6] = poly[8] * fracHeight + vertX * (1.0 - fracHeight);
        poly[7] = poly[9] * fracHeight + vertY * (1.0 - fracHeight);
        double[] thetaTrig = new double[2];
        thetaTrig[0] = cosTheta;
        thetaTrig[1] = sinTheta;
        return thetaTrig;
    }

    static double[] calcDiamond(double[] shapePars, double x1, double y1, double x2, double y2, double[] poly) {
        double shapeA = shapePars[0] + 0.001;
        poly[0] = poly[8] = x1;
        poly[1] = poly[9] = y1;
        double dx = poly[0] - x2;
        double dy = poly[1] - y2;
        double length = Math.hypot(dx, dy);
        double sinTheta = 0.0;
        double cosTheta = 0.0;
        if (length != 0) {
            sinTheta = dy / length;
            cosTheta = dx / length;
        }
        double vertX = poly[0] - shapeA * cosTheta;
        double vertY = poly[1] - shapeA * sinTheta;
        double temp = shapeA / 2 * sinTheta;
        poly[2] = poly[0] - shapeA / 2 * cosTheta + temp;
        poly[6] = poly[2] - 2 * temp;

        poly[4] = vertX;
        poly[5] = vertY;
        temp = shapeA / 2 * cosTheta;
        poly[3] = poly[1] - shapeA / 2 * sinTheta - temp;
        poly[7] = poly[3] + 2 * temp;

        double[] thetaTrig = new double[2];
        thetaTrig[0] = cosTheta;
        thetaTrig[1] = sinTheta;
        return thetaTrig;
    }

    static double[] calcCircle(double[] shapePars, double x1, double y1, double x2, double y2, double[] poly) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double length = Math.hypot(dx, dy);
        double sinTheta = 0.0;
        double cosTheta = 0.0;
        if (length != 0) {
            sinTheta = dy / length;
            cosTheta = dx / length;
        }
        double shapeA = shapePars[0] + 0.001;
        poly[0] = x1 - shapeA / 2 * cosTheta;
        poly[1] = y1 - shapeA / 2 * sinTheta;
        poly[2] = shapeA;
        poly[3] = shapeA;
        double[] thetaTrig = new double[2];
        thetaTrig[0] = 0;
        thetaTrig[1] = 0;
        return thetaTrig;
    }

    static double[] calcSquare(double[] shapePars, double x1, double y1, double x2, double y2, double[] poly) {
        double shapeA = shapePars[0] + 0.001;
        poly[0] = poly[10] = x1;
        poly[1] = poly[11] = y1;
        double dx = poly[0] - x2;
        double dy = poly[1] - y2;

        double length = Math.hypot(dx, dy);
        double sinTheta = 0.0;
        double cosTheta = 0.0;
        if (length != 0) {
            sinTheta = dy / length;
            cosTheta = dx / length;
        }
        double vertX = poly[0] - shapeA * cosTheta;
        double vertY = poly[1] - shapeA * sinTheta;

        double temp = shapeA / 2 * sinTheta;
        poly[2] = poly[0] + temp;
        poly[8] = poly[2] - 2 * temp;

        poly[4] = vertX + temp;
        poly[6] = poly[4] - 2 * temp;

        temp = shapeA / 2 * cosTheta;
        poly[3] = poly[1] - temp;
        poly[9] = poly[3] + 2 * temp;

        poly[5] = vertY - temp;
        poly[7] = poly[5] + 2 * temp;

        double[] thetaTrig = new double[2];
        thetaTrig[0] = cosTheta;
        thetaTrig[1] = sinTheta;
        return thetaTrig;
    }

    public void genPath(double[] coords) {
        float x1;
        float y1;
        gPath.reset();

        for (int i = 0; i < coords.length; i += 2) {
            x1 = (float) coords[i];
            y1 = (float) coords[i + 1];

            if (i == 0) {
                gPath.moveTo(x1, y1);
            } else {
                gPath.lineTo(x1, y1);
            }
        }

        if (closePath) {
            gPath.closePath();
        }
    }

    public void addArrowPathCircle(GeneralPath arrowPath, double[] arrowCoords) {
        arrowPath.reset();
        float x1 = (float) arrowCoords[0];
        float y1 = (float) arrowCoords[1];
        float radius = (float) (arrowCoords[2] / 2);
        Arc2D.Double arc = new Arc2D.Double(x1 - radius, y1 - radius, radius * 2, radius * 2, 0, 360, Arc2D.CHORD);
        arrowPath.append(arc, false);
    }

    public void addArrowPath(GeneralPath arrowPath, double[] arrowCoords) {
        arrowPath.reset();
        float x1;
        float y1;
        int nCoords = arrowCoords.length;
        for (int i = 0; i < nCoords; i += 2) {
            x1 = (float) arrowCoords[i];
            y1 = (float) arrowCoords[i + 1];

            if (i == 0) {
                arrowPath.moveTo(x1, y1);
            } else {
                arrowPath.lineTo(x1, y1);
            }
        }
        arrowPath.closePath();
    }

    public void genSmoothPath(double[] coords) {
        gPath.reset();
        BezierPath.makeBezierCurve(coords, 1, gPath, smoothValue);

        if (closePath) {
            gPath.closePath();
        }
    }

    public String getType() {
        return "line";
    }

    @Override
    public void drawHandles(Graphics2D g2) {
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

    @Override
    public int hitHandles(double testX, double testY) {
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

    @Override
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
