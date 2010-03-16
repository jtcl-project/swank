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
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.*;
import org.jfree.data.category.*;

import org.jfree.ui.RectangleEdge;

import tcl.lang.*;

import java.awt.*;
import java.awt.geom.*;

import java.lang.*;

import java.util.*;


public class BoxPlotShapeComplete extends SwkShape {
    static CanvasParameter[] parameters = {
       new TagsParameter(), new DatasetParameter(),
       new RLabelParameter(), new RMinParameter(), new RMaxParameter(), new RAutoParameter(),

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
    CategoryPlot plot = new CategoryPlot();
    ChartRenderingInfo chartInfo = new ChartRenderingInfo();
    PlotRenderingInfo state = new PlotRenderingInfo(chartInfo);
    Rectangle2D.Double plotArea = null;
    double cursor = 0.0;
    String plotType = "boxandwhisker";
    BoxAndWhiskerRenderer renderer = null;

    public BoxPlotShapeComplete() {
        plot.setDataset(new BoxAndWhiskerTableData());
        plot.setDomainAxis(new CategoryAxis());
        plot.setRangeAxis(new NumberAxis());
        setRenderer("render");
        setShape(null);
    }
    public CategoryPlot getPlot() {
        return plot;
    } 
    public  void setRenderer(String renderName) {
        renderer = new BoxAndWhiskerRenderer();
        plot.setRenderer(renderer);
    }
    public void setDataset(String name) {
        System.out.println("setdataset");
        BoxAndWhiskerTableData categoryData = BoxAndWhiskerTableData.get(name);
        //HighLowData categoryData = HighLowData.get(name);
        plot.setDataset(categoryData);
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

    public void applyCoordinates() {
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

    public CanvasParameter[] getParameters() {
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

        public String getValue(SwkShape swkShape) {
             BoxAndWhiskerTableData categoryData = (BoxAndWhiskerTableData) ((BoxPlotShapeComplete) swkShape).plot.getDataset();
             return categoryData.getName();
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
            String datasetName = getNewValue();
            ((BoxPlotShapeComplete) swkShape).setDataset(datasetName);
        }
    }
    static class RLabelParameter extends StringParameter {
        private static String name = "rlabel";

        RLabelParameter() {
            CanvasParameter.addParameter(this);
        }

        public String getName() {
            return name;
        }

        public String getValue(SwkShape swkShape) {
            NumberAxis axis =(NumberAxis)  ((BoxPlotShapeComplete) swkShape).plot.getRangeAxis();
            return axis.getLabel();
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
             NumberAxis axis =(NumberAxis)  ((BoxPlotShapeComplete) swkShape).plot.getRangeAxis();
             axis.setLabel(getNewValue());
        }
    }

    static class RMinParameter extends DoubleParameter {
        private static String name = "rmin";

        RMinParameter() {
            CanvasParameter.addParameter(this);
        }

        public String getName() {
            return name;
        }

        public double getValue(SwkShape swkShape) {
            NumberAxis axis =(NumberAxis)  ((BoxPlotShapeComplete) swkShape).plot.getRangeAxis();
            return axis.getLowerBound();
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
             NumberAxis axis =(NumberAxis)  ((BoxPlotShapeComplete) swkShape).plot.getRangeAxis();
             axis.setLowerBound(getNewValue());
        }
    }
   static class RMaxParameter extends DoubleParameter {
        private static String name = "rmax";

        RMaxParameter() {
            CanvasParameter.addParameter(this);
        }

        public String getName() {
            return name;
        }

        public double getValue(SwkShape swkShape) {
            NumberAxis axis =(NumberAxis)  ((BoxPlotShapeComplete) swkShape).plot.getRangeAxis();
            return axis.getUpperBound();
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
             NumberAxis axis =(NumberAxis)  ((BoxPlotShapeComplete) swkShape).plot.getRangeAxis();
             axis.setUpperBound(getNewValue());
        }
    }
   static class RAutoParameter extends BooleanParameter {
        private static String name = "rauto";

        RAutoParameter() {
            CanvasParameter.addParameter(this);
        }

        public String getName() {
            return name;
        }

        public boolean getValue(SwkShape swkShape) {
            NumberAxis axis =(NumberAxis)  ((BoxPlotShapeComplete) swkShape).plot.getRangeAxis();
            return axis.isAutoRange();
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
            NumberAxis axis = (NumberAxis) ((BoxPlotShapeComplete) swkShape).plot.getRangeAxis();
            axis.setAutoRange(getNewValue());
        }
    }
}
