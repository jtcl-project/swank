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
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.*;

import org.jfree.ui.RectangleEdge;

import tcl.lang.*;

import java.awt.*;
import java.awt.geom.*;

import java.lang.*;

import java.util.*;


public class XYPlotShape extends SwkShape {
    static CanvasParameter[] parameters = {
       new TagsParameter(), new FontParameter(), new RangeaxisParameter(), new DomainaxisParameter(), new DatasetParameter(), new FillParameter()
    };
    static Map parameterMap = new TreeMap();

    static {
        initializeParameters(parameters, parameterMap);
    }

    GeneralPath gPath = new GeneralPath();
    float radius = 2.0f;
    int symbolType = 3;
    boolean drawLine = false;
    boolean closePath = false;
    XYPlot plot = new XYPlot();
    ChartRenderingInfo chartInfo = new ChartRenderingInfo();
    PlotRenderingInfo state = new PlotRenderingInfo(chartInfo);
    Rectangle2D.Double plotArea = null;
    double cursor = 0.0;
    ValueAxis domainAxis = null;
    String domainAxisTag = "";
    ValueAxis rangeAxis = null;
    String rangeAxisTag = "";
    RectangleEdge edge = RectangleEdge.BOTTOM;
    String edgeString = "bottom";
    String plotType = "lineandshape";
    XYItemRenderer renderer = null;

    public XYPlotShape() {
        setRenderer();
        setShape(null);
    }
    public XYPlot getPlot() {
        return plot;
    } 
    public  void setRenderer() {
        renderer = new XYLineAndShapeRenderer();
        plot.setRenderer(renderer);
    }
    public void setDataset(String name) {
        XYData xyData = XYData.get(name);
        plot.setDataset(xyData);
    }
    public void setDataset(XYData xyData) {
        plot.setDataset(xyData);
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
    public String hit(double x, double y) { 
        String result = "";
        if (state != null) {
            EntityCollection entities = state.getOwner().getEntityCollection();
            if (entities != null) {
                ChartEntity entity = entities.getEntity(x, y);
                if (entity != null) {
                    result = entity.getToolTipText();
                }
            }
         }
 
         return result;
    }
    public boolean hitShape(double x, double y) {
        boolean result = false;
        if (state != null) {
            EntityCollection entities = state.getOwner().getEntityCollection();
            if (entities != null) {
                ChartEntity entity = entities.getEntity(x, y);
                if (entity != null) {
                    result = true;
                }
            }
         }

         return result;
    }

    public void coords(SwkImageCanvas canvas, double[] coords)
        throws SwkException {
        if (coords.length != 4) {
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

        //shape = aT.createTransformedShape(gPath);
    }

    CanvasParameter[] getParameters() {
        return parameters;
    }

    public Map getParameterMap() {
        return parameterMap;
    }

    public String getType() {
        return "vector";
    }

    public void paintShape(Graphics2D g2) {
        Point2D anchor = new Point2D.Double();
        applyCoordinates();
        plot.draw(g2, plotArea, anchor, null, state);
    }

    public void addSymbol(float x1, float y1, float radius) {
    }


    static class DatasetParameter extends StringParameter {
        private static String name = "dataset";

        DatasetParameter() {
            CanvasParameter.addParameter(this);
        }

        public String getName() {
            return name;
        }

        String getValue(SwkShape swkShape) {
             XYData xyData = (XYData) ((XYPlotShape) swkShape).plot.getDataset();
             return xyData.getName();
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
            String datasetName = getNewValue();
            ((XYPlotShape) swkShape).setDataset(datasetName);
        }
    }

    static class DomainaxisParameter extends StringParameter {
        private static String name = "domainaxis";

        DomainaxisParameter() {
            CanvasParameter.addParameter(this);
        }

        public String getName() {
            return name;
        }

        String getValue(SwkShape swkShape) {
            return ((XYPlotShape) swkShape).domainAxisTag;
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
            String domainAxisTag = getNewValue();
            ((XYPlotShape) swkShape).domainAxisTag = domainAxisTag;

            try {
                SwkShape axisShape = swkCanvas.getShape(domainAxisTag);

                if (axisShape instanceof NumberAxisShape) {
                    ValueAxis domainAxis = (NumberAxis) ((NumberAxisShape) axisShape).axis;
                    ((XYPlotShape) swkShape).domainAxis = domainAxis;
                    ((XYPlotShape) swkShape).plot.setDomainAxis(domainAxis);
                }
            } catch (SwkException swkE) {
            }
        }
    }

    static class RangeaxisParameter extends StringParameter {
        private static String name = "rangeaxis";

        RangeaxisParameter() {
            CanvasParameter.addParameter(this);
        }

        public String getName() {
            return name;
        }

        String getValue(SwkShape swkShape) {
            return ((XYPlotShape) swkShape).rangeAxisTag;
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
            String rangeAxisTag = getNewValue();
            ((XYPlotShape) swkShape).rangeAxisTag = rangeAxisTag;

            try {
                SwkShape axisShape = swkCanvas.getShape(rangeAxisTag);
                if (axisShape instanceof NumberAxisShape) {
                    ValueAxis rangeAxis = (NumberAxis) ((NumberAxisShape) axisShape).axis;
                    ((XYPlotShape) swkShape).rangeAxis = rangeAxis;
                    ((XYPlotShape) swkShape).plot.setRangeAxis(rangeAxis);
                }
            } catch (SwkException swkE) {
            }
        }
    }
}