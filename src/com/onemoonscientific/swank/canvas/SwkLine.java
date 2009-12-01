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


public class SwkLine extends SwkShape {
    static CanvasParameter[] parameters = {
        new FillParameter(), new SmoothParameter(), new DashParameter(),
        new DashPhaseParameter(), new WidthParameter(), new RotateParameter(),
        new ShearParameter(), new TagsParameter(), new StateParameter(),
        new TransformerParameter(), new CapstyleParameter(),
        new JoinstyleParameter(), new ArrowParameter(), new ArrowShapeParameter()
    };
    static Map parameterMap = new TreeMap();

    static {
        initializeParameters(parameters, parameterMap);
    }

    GeneralPath gPath = null;
    GeneralPath firstArrowPath = null;
    GeneralPath lastArrowPath = null;
    boolean closePath = false;
    String smooth = "";
    double smoothValue = 1.0;
    final private int PTS_IN_ARROW = 6;
    
    boolean arrowFirst = false;
    boolean arrowLast = false;
    double arrowShapeA = 8.0;
    double arrowShapeB = 10.0;
    double arrowShapeC = 3.0;

    SwkLine(Shape shape, SwkImageCanvas canvas) {
        super(shape, canvas);
        gPath = (GeneralPath) shape;
        fill = null;
    }
    public void paintShape(Graphics2D g2) {
        AffineTransform shapeTransform = getTransform();

        g2.setPaint(outline);

        // draw line
        if (shapeTransform != null) {
            g2.draw(shapeTransform.createTransformedShape(shape));
        } else {
            g2.draw(shape);
        }

        // draw first arrow head
        if (firstArrowPath != null) {
            if (shapeTransform != null) {
                g2.fill(shapeTransform.createTransformedShape(firstArrowPath));
                g2.draw(shapeTransform.createTransformedShape(firstArrowPath));
            } else {
                g2.fill(firstArrowPath);
                g2.draw(firstArrowPath);
            }
        }
        if (lastArrowPath != null) {
            if (shapeTransform != null) {
                g2.fill(shapeTransform.createTransformedShape(lastArrowPath));
                g2.draw(shapeTransform.createTransformedShape(lastArrowPath));
            } else {
                g2.fill(lastArrowPath);
                g2.draw(lastArrowPath);
            }
        }
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

    public void applyCoordinates() {
        AffineTransform aT = new AffineTransform();
        aT.translate(storeCoords[0], storeCoords[1]);
        aT.shear(xShear, yShear);
        aT.translate(-storeCoords[0], -storeCoords[1]);
        aT.rotate(rotate, ((storeCoords[0] + storeCoords[2]) / 2.0),
            ((storeCoords[1] + storeCoords[3]) / 2.0));
        double[] arrowFirstCoords = null;
        double[] arrowLastCoords = null;
        if (arrowFirst) {
            arrowFirstCoords = addArrowFirst(storeCoords);
            if (firstArrowPath == null) {
                firstArrowPath = new GeneralPath();
            }
        } else {
            firstArrowPath = null;
        }
        if (arrowLast) {
            arrowLastCoords = addArrowLast(storeCoords);
            if (lastArrowPath == null) {
                lastArrowPath = new GeneralPath();
            }
        } else {
            lastArrowPath = null;
        }

        if ((smooth == null) || smooth.equals("")) {
            genPath();
        } else {
            genSmoothPath();
        }
        shape = aT.createTransformedShape(gPath);
        if (arrowFirst) {
            addArrowPath(firstArrowPath,arrowFirstCoords);
        }
        if (arrowLast) {
            addArrowPath(lastArrowPath, arrowLastCoords);
        }


    }
    double[] addArrowFirst(double[] storeCoords) {
            double shapeA = arrowShapeA + 0.001;
            double shapeB = arrowShapeB + 0.001;
            double shapeC = arrowShapeC + width/2.0 + 0.001;

            double fracHeight = (width/2.0)/shapeC;
            double backup = fracHeight*shapeB + shapeA*(1.0 - fracHeight)/2.0;

            double x1 =  storeCoords[0];
            double y1 =  storeCoords[1];
            double x2 =  storeCoords[2];
            double y2 =  storeCoords[3];
            double[] poly = new double[PTS_IN_ARROW*2];

            poly[0] = poly[10] = x1;
            poly[1] = poly[11] = y1;
            double dx = poly[0] - x2;
            double dy = poly[1] - y2;
            double length = Math.hypot(dx, dy);
            double sinTheta = 0.0;
            double cosTheta = 0.0;
            if (length != 0) {
                sinTheta = dy/length;
                cosTheta = dx/length;
            }
        double vertX = poly[0] - shapeA*cosTheta;
        double vertY = poly[1] - shapeA*sinTheta;
        double temp = shapeC*sinTheta;
        poly[2] = poly[0] - shapeB*cosTheta + temp;
        poly[8] = poly[2] - 2*temp;
        temp = shapeC*cosTheta;
        poly[3] = poly[1] - shapeB*sinTheta - temp;
        poly[9] = poly[3] + 2*temp;
        poly[4] = poly[2]*fracHeight + vertX*(1.0-fracHeight);
        poly[5] = poly[3]*fracHeight + vertY*(1.0-fracHeight);
        poly[6] = poly[8]*fracHeight + vertX*(1.0-fracHeight);
        poly[7] = poly[9]*fracHeight + vertY*(1.0-fracHeight);

        storeCoords[0] = poly[0] - backup*cosTheta;
        storeCoords[1] = poly[1] - backup*sinTheta;
        return poly;
    }

    double[] addArrowLast(double[] storeCoords) {

            double shapeA = arrowShapeA + 0.001;
            double shapeB = arrowShapeB + 0.001;
            double shapeC = arrowShapeC + width/2.0 + 0.001;

            double fracHeight = (width/2.0)/shapeC;
            double backup = fracHeight*shapeB + shapeA*(1.0 - fracHeight)/2.0;

            int nElems = storeCoords.length;
            double x1 =  storeCoords[nElems-4];
            double y1 =  storeCoords[nElems-3];
            double x2 =  storeCoords[nElems-2];
            double y2 =  storeCoords[nElems-1];
            double[] poly = new double[PTS_IN_ARROW*2];

            poly[0] = poly[10] = x2;
            poly[1] = poly[11] = y2;
            double dx = poly[0] - x1;
            double dy = poly[1] - y1;
            double length = Math.hypot(dx, dy);
            double sinTheta = 0.0;
            double cosTheta = 0.0;
            if (length != 0) {
                sinTheta = dy/length;
                cosTheta = dx/length;
            }
            double vertX = poly[0] - shapeA*cosTheta;
            double vertY = poly[1] - shapeA*sinTheta;
            double temp = shapeC*sinTheta;
            poly[2] = poly[0] - shapeB*cosTheta + temp;
            poly[8] = poly[2] - 2*temp;
            temp = shapeC*cosTheta;
            poly[3] = poly[1] - shapeB*sinTheta - temp;
            poly[9] = poly[3] + 2*temp;
            poly[4] = poly[2]*fracHeight + vertX*(1.0-fracHeight);
            poly[5] = poly[3]*fracHeight + vertY*(1.0-fracHeight);
            poly[6] = poly[8]*fracHeight + vertX*(1.0-fracHeight);
            poly[7] = poly[9]*fracHeight + vertY*(1.0-fracHeight);
            storeCoords[nElems-2] = poly[0] - backup*cosTheta;
            storeCoords[nElems-1] = poly[1] - backup*sinTheta;
            return poly;
    }
    public void genPath() {
        float x1;
        float y1;
        gPath.reset();

        for (int i = 0; i < storeCoords.length; i += 2) {
            x1 = (float) storeCoords[i];
            y1 = (float) storeCoords[i + 1];

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

    public void addArrowPath(GeneralPath arrowPath, double[] arrowCoords) {
        arrowPath.reset();
        float x1;
        float y1;

        for (int i = 0; i < arrowCoords.length; i += 2) {
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

    public void genSmoothPath() {
        float x1;
        float y1;
        float x2;
        float y2;
        gPath.reset();
        BezierPath.makeBezierCurve(storeCoords, 1, gPath, smoothValue);

        if (closePath) {
            gPath.closePath();
        }
    }

    public CanvasParameter[] getParameters() {
        return parameters;
    }

    public Map getParameterMap() {
        return parameterMap;
    }

    public String getType() {
        return "line";
    }
}
