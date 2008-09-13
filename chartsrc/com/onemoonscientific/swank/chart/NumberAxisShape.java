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

/**
 *
 * @author  JOHNBRUC
 * @version
 */
package com.onemoonscientific.swank.chart;

import com.onemoonscientific.swank.*;
import com.onemoonscientific.swank.canvas.*;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.PlotRenderingInfo;

import org.jfree.ui.RectangleEdge;

import tcl.lang.*;

import java.awt.*;
import java.awt.geom.*;

import java.lang.*;

import java.util.*;


public class NumberAxisShape extends SwkShape {

    static CanvasParameter[] parameters = {
        new TagsParameter(), new EdgeParameter(), new CursorParameter(),
        new LabelParameter(), new MinParameter(), new MaxParameter()
    };
    static Map parameterMap = new TreeMap();

    static {
        initializeParameters(parameters, parameterMap);
    }

    GeneralPath gPath = new GeneralPath();
    private double xMin = 0.0;
    private double yMin = 0.0;
    private double xMax = 1.0;
    private double yMax = 1.0;
    float radius = 2.0f;
    int symbolType = 3;
    boolean drawLine = false;
    boolean closePath = false;
    Axis axis = null;
    Rectangle2D.Double dataArea = null;
    Rectangle2D.Double plotArea = null;
    double cursor = 0.0;
    RectangleEdge edge = RectangleEdge.BOTTOM;
    String edgeString = "bottom";
    String label = "";
    ChartRenderingInfo chartInfo = new ChartRenderingInfo();
    PlotRenderingInfo state = new PlotRenderingInfo(chartInfo);

    public NumberAxisShape() {
        setShape(null);
        axis = (Axis) new NumberAxis();
    }

    public void setXmin(double newValue) {
        xMin = newValue;
    }

    public void setXmax(double newValue) {
        xMax = newValue;
    }

    public void setYmin(double newValue) {
        yMin = newValue;
    }

    public void setYmax(double newValue) {
        yMax = newValue;
    }

    public double getXmin() {
        return xMin;
    }

    public double getXmax() {
        return xMax;
    }

    public double getYmin() {
        return yMin;
    }

    public double getYmax() {
        return yMax;
    }

    public void setDrawline(boolean newValue) {
        drawLine = newValue;
    }

    public boolean getDrawline() {
        return drawLine;
    }

    public void setSymbolType(int newSymbolType) {
        symbolType = newSymbolType;
    }

    public String getSymbolType() {
        return SymbolParameter.getSymbolType(symbolType);
    }

    public void setRadius(double newRadius) {
        radius = (float) newRadius;
    }

    public double getRadius() {
        return radius;
    }

    public void coords(SwkImageCanvas canvas, double[] coords)
        throws SwkException {

        if (coords.length != 8) {
            throw new SwkException("wrong # coordinates: expected 8, got " +
                coords.length);
        }

        gPath.reset();

        if ((storeCoords == null) || (storeCoords.length != coords.length)) {
            storeCoords = new double[coords.length];
        }

        System.arraycopy(coords, 0, storeCoords, 0, coords.length);
        applyCoordinates();
    }

    void applyCoordinates() {
        AffineTransform aT = new AffineTransform();
        aT.translate(storeCoords[0], storeCoords[1]);
        aT.shear(getXShear(), getYShear());
        aT.translate(-storeCoords[0], -storeCoords[1]);
        aT.rotate(getRotate(), ((storeCoords[0] + storeCoords[2]) / 2.0),
            ((storeCoords[1] + storeCoords[3]) / 2.0));
        plotArea = new Rectangle2D.Double();
        plotArea.setFrameFromDiagonal(storeCoords[0], storeCoords[1],
            storeCoords[2], storeCoords[3]);
        dataArea = new Rectangle2D.Double();
        dataArea.setFrameFromDiagonal(storeCoords[4], storeCoords[5],
            storeCoords[6], storeCoords[7]);

        //shape = aT.createTransformedShape(gPath);
    }

    public CanvasParameter[] getParameters() {
        return parameters;
    }

    public Map getParameterMap() {
        return parameterMap;
    }

    public String getType() {
        return "naxis";
    }

    public void paintShape(Graphics2D g2) {
        if (axis.getPlot() != null) {
            //axis.draw(g2, cursor, plotArea, dataArea, edge, state);
        }
    }

    public void addSymbol(float x1, float y1, float radius) {
    }

    static class LabelParameter extends StringParameter {
        private static String name = "label";

        LabelParameter() {
            CanvasParameter.addParameter(this);
        }

        public String getName() {
            return name;
        }

        public String getValue(SwkShape swkShape) {
            return ((NumberAxisShape) swkShape).axis.getLabel();
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
            ((NumberAxisShape) swkShape).axis.setLabel(getNewValue());
        }
    }

    static class EdgeParameter extends StringParameter {
        private static String name = "edge";

        EdgeParameter() {
            CanvasParameter.addParameter(this);
        }

        public String getName() {
            return name;
        }

        public String getValue(SwkShape swkShape) {
            return ((NumberAxisShape) swkShape).edgeString;
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
            String edgeString = getNewValue();
            RectangleEdge edge = RectangleEdge.BOTTOM;

            if ("bottom".equals(edgeString)) {
                edge = RectangleEdge.BOTTOM;
            } else if ("top".equals(edgeString)) {
                edge = RectangleEdge.TOP;
            } else if ("left".equals(edgeString)) {
                edge = RectangleEdge.LEFT;
            } else if ("right".equals(edgeString)) {
                edge = RectangleEdge.RIGHT;
            }

            ((NumberAxisShape) swkShape).edgeString = edgeString;
            ((NumberAxisShape) swkShape).edge = edge;
        }
    }

    static class CursorParameter extends DoubleParameter {
        private static String name = "cursor";

        CursorParameter() {
            CanvasParameter.addParameter(this);
        }

        public String getName() {
            return name;
        }

        public double getValue(SwkShape swkShape) {
            return ((NumberAxisShape) swkShape).cursor;
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
            ((NumberAxisShape) swkShape).cursor = getNewValue();
        }
    }
    static class MinParameter extends DoubleParameter {
        private static String name = "min";

        MinParameter() {
            CanvasParameter.addParameter(this);
        }

        public String getName() {
            return name;
        }

        public double getValue(SwkShape swkShape) {
            return ((NumberAxis) ((NumberAxisShape) swkShape).axis).getLowerBound();
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
            ((NumberAxis) ((NumberAxisShape) swkShape).axis).setLowerBound(getNewValue());
        }
    }
    static class MaxParameter extends DoubleParameter {
        private static String name = "max";

        MaxParameter() {
            CanvasParameter.addParameter(this);
        }

        public String getName() {
            return name;
        }

        public double getValue(SwkShape swkShape) {
            return ((NumberAxis) ((NumberAxisShape) swkShape).axis).getUpperBound();
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
            ( (NumberAxis) ((NumberAxisShape) swkShape).axis).setUpperBound(getNewValue());
        }
    }

}
