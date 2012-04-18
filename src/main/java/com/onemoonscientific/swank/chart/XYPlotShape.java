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


import tcl.lang.*;

import java.awt.*;
import java.awt.geom.*;

import java.lang.*;

import java.util.*;


public class XYPlotShape extends SwkShape implements DatasetShape, NumberDomain,NumberRange,PlotInterface {
    static TreeMap<String,CanvasParameter> parameterMap = new TreeMap<String,CanvasParameter>();
    static CanvasParameter[] parameters = {
       new TagsParameter(), new DatasetParameter(), new FillParameter()
    };

    static {
        initializeParameters(parameters, parameterMap);
    }

    GeneralPath gPath = new GeneralPath();
    float radius = 2.0f;
    int symbolType = 3;
    boolean drawLine = false;
    XYPlot plot = new XYPlot();
    PlotLegend plotLegend = new PlotLegend(plot);
    ChartRenderingInfo chartInfo = new ChartRenderingInfo();
    PlotRenderingInfo state = new PlotRenderingInfo(chartInfo);
    Rectangle2D.Double plotArea = null;
    String plotType = "lineandshape";
    XYItemRenderer renderer = null;
    Rectangle2D rect2D = null;
    String legendLoc = "s.n";
    boolean legendState = false;
    Transformer plotTransformer = null;
    Transformer fracTransformer = null;
    final static Shape[] seriesShapes = DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE;
    final static String[] shapeNames = {"square","circle","utriangle","diamond","hrectangle",
    "dtriangle","hellipse","rtriangle","vrectangle","ltriangle"};
    final static HashMap<String,Shape> shapeMap = new HashMap<String,Shape>();
    final static HashMap<Shape,String> shapeNameMap = new HashMap<Shape,String>();
    final static double baseSize = 6.0;
    static {
        int i=0;
        for (String shapeName:shapeNames) {
            shapeMap.put(shapeName,seriesShapes[i]);
            shapeNameMap.put(seriesShapes[i],shapeName);
            i++;
        }    
    }
    public XYPlotShape() {
        rect2D = new Rectangle2D.Double();
        setRenderer();
        setShape(rect2D);
    }
    public XYPlot getPlot() {
        return plot;
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
    public String getType() {
	    return plotType;
    }
    public  void setRenderer() {
        renderer = new XYLineAndShapeRenderer();
        plot.setRenderer(renderer);
    }
    public  void setRenderer(XYItemRenderer newRenderer) {
        renderer = newRenderer;
        plot.setRenderer(renderer);
    }
    public void setSplineRenderer(final int precision) {
        XYSplineRenderer splineRenderer = new XYSplineRenderer();
        splineRenderer.setPrecision(precision);
        renderer = splineRenderer;
        plot.setRenderer(renderer);
    }

    public void setDataset(String name) {
        XYData xyData = XYData.get(name);
        plot.setDataset(xyData);
    }
    /**
     *
     * @param index
     * @param name
     */
    public void setDataset(int index, String name) {
        XYData xyData = XYData.get(name);
        plot.setDataset(index,xyData);
    }
    public void setDataset(XYData xyData) {
        plot.setDataset(xyData);
    }
    public void setDrawline(boolean newValue) {
        drawLine = newValue;
    }

    /**
     *
     * @return
     */
    public boolean getDrawline() {
        return drawLine;
    }

    /**
     *
     * @param newSymbolType
     */
    public void setSymbolType(int newSymbolType) {
        symbolType = newSymbolType;
    }

    public String getSymbolType() {
        return SymbolParameter.getSymbolType(symbolType);
    }

    /**
     *
     * @param newRadius
     */
    public void setRadius(double newRadius) {
        radius = (float) newRadius;
    }

    /**
     *
     * @return
     */
    public double getRadius() {
        return radius;
    }
    public String getLegendLoc() {
        return legendLoc;
    }
    public void setLegendLoc(String loc) {
        legendLoc = loc;
    }
    /**
     *
     * @return
     */
    public boolean getLegendState() {
        return legendState;
    }
    public void setLegendState(boolean state) {
        legendState = state;
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
           boolean hit = false;
           Shape checkShape = getShape();
            AffineTransform shapeTransform = getTransform();
            if (shapeTransform != null) {
                checkShape = shapeTransform.createTransformedShape(checkShape);
            }
            Rectangle bounds = checkShape.getBounds();
            if (bounds.contains(x, y)) {
                hit = true;
            }
            return hit;
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
        checkCoordinates(storeCoords);
        rect2D.setFrame(storeCoords[0], storeCoords[1],
                storeCoords[2] - storeCoords[0], storeCoords[3] - storeCoords[1]);

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

    /**
     *
     * @param g2
     */
    public void paintShape(Graphics2D g2) {
        Point2D anchor = new Point2D.Double();
        applyCoordinates();
        Rectangle2D plotAreaNow = (Rectangle2D) plotArea.clone();;
        AffineTransform shapeTransform = getTransform();
        if (shapeTransform != null) {
                plotAreaNow = shapeTransform.createTransformedShape(plotAreaNow).getBounds2D();
        }

        if (legendState) {
            Rectangle2D  legendArea = plotLegend.arrangeLegend(g2, plotAreaNow);
            plot.draw(g2, plotAreaNow, anchor, null, state);
            plotLegend.drawLegend(g2,legendArea);
        } else {
            plot.draw(g2, plotAreaNow, anchor, null, state);
        }

        ValueAxis dAxis = plot.getDomainAxis();
        double lowerBoundX = dAxis.getLowerBound();
        double upperBoundX = dAxis.getUpperBound();
        ValueAxis rAxis = plot.getRangeAxis();
        double lowerBoundY = rAxis.getLowerBound();
        double upperBoundY= rAxis.getUpperBound();
         Rectangle2D dataArea = state.getDataArea();
        double x1 = dataArea.getMinX();
        double x2 = dataArea.getMaxX();
        double y1 = dataArea.getMinY();
        double y2 = dataArea.getMaxY();
        double scaleX = (x2-x1) /(upperBoundX-lowerBoundX);
        double scaleY = (y2-y1) /(lowerBoundY-upperBoundY);
        SwkImageCanvas canvas = getCanvas();
        if (plotTransformer == null) {
            plotTransformer = canvas.setTransformer("xyplot"+getId(), null);
            fracTransformer = canvas.setTransformer("frac" + getId(), null);
        }

        AffineTransform aT = plotTransformer.getTransform();
        aT.setToIdentity();
        aT.translate(x1,y1);
        aT.scale(scaleX, scaleY);
        aT.translate(-lowerBoundX,-upperBoundY);
        aT = fracTransformer.getTransform();
        aT.setToIdentity();
        aT.translate(plotAreaNow.getX(),plotAreaNow.getY());
        aT.scale(plotAreaNow.getWidth(),plotAreaNow.getHeight());
    }

    public void addSymbol(float x1, float y1, float radius) {
    }
    public TclObject getDatasets(Interp interp) throws TclException {
                int nDatasets = plot.getDatasetCount();
                TclObject list = TclList.newInstance();
                for (int i = 0; i < nDatasets; i++) {
                        XYData xyData = (XYData) plot.getDataset(i);
			if (xyData != null) {
                            TclList.append(interp, list, TclString.newInstance(xyData.getName()));
			}
                }
                return list;
    }
    /**
     *
     * @param datasetNames
     */
    public void updateDatasets(String[] datasetNames) {
                int nDatasets = plot.getDatasetCount();
                for (int i = 0; i < datasetNames.length; i++) {
                        setDataset(i, datasetNames[i]);
                        if (i >= nDatasets) {
                                XYLineAndShapeRenderer newRenderer = new XYLineAndShapeRenderer();
                                newRenderer.setToolTipGenerator(XYLineAndShapeComplete.generator);
                                plot.setRenderer(i, newRenderer);
                        }
                }
        }
       public TclObject getColors(Interp interp) throws TclException {
                int nDatasets = plot.getDatasetCount();
                TclObject list = TclList.newInstance();
                for (int iData = 0; iData < nDatasets; iData++) {
                        XYItemRenderer renderer = plot.getRenderer(iData);
                        if (renderer == null) {
                                renderer = plot.getRenderer();
                        }
                        if (renderer instanceof XYLineAndShapeRenderer) {
                                XYData data = (XYData) plot.getDataset(iData);
                                if (data != null) {
                                int nSeries = data.getSeriesCount();
                                for (int i = 0; i < nSeries; i++) {
                                        Color color = (Color) (((XYLineAndShapeRenderer) renderer).getSeriesPaint(i));
                                        TclList.append(interp, list, TclString.newInstance(SwankUtil.parseColor(color)));
                                }
                                }
                        } else {
                                TclList.append(interp, list, TclString.newInstance(""));
                        }
                }
                return list;
        }

     public void updateColors(Color[] colors) {
                Color color = Color.BLACK;
                int nDatasets = plot.getDatasetCount();
                int j = 0;
                for (int iData = 0; iData < nDatasets; iData++) {
                        XYItemRenderer renderer = plot.getRenderer(iData);
                        if (renderer == null) {
                                renderer = plot.getRenderer();
                        }
                        if (renderer instanceof XYLineAndShapeRenderer) {
                                XYData data = (XYData) plot.getDataset(iData);
                                if (data != null) {
                                    int nSeries = data.getSeriesCount();
                                    for (int i = 0; i < nSeries; i++) {
                                        color = Color.BLACK;
                                        if (colors.length > j) {
                                                color = colors[j];
                                        }
                                        ((XYLineAndShapeRenderer) renderer).setSeriesPaint(i, color);
                                        //((XYLineAndShapeRenderer) renderer).setSeriesFillPaint(i, color);
                                        ((XYLineAndShapeRenderer) renderer).setUseOutlinePaint(true);
                                        ((XYLineAndShapeRenderer) renderer).setSeriesOutlinePaint(i, Color.BLACK);
                                        j++;
                                    }
                                }
                        }
                }
    }
    public TclObject getShapes(Interp interp) throws TclException {
        int nDatasets = plot.getDatasetCount();
        TclObject list = TclList.newInstance();
        for (int iData = 0; iData < nDatasets; iData++) {
            XYItemRenderer renderer = plot.getRenderer(iData);
            if (renderer == null) {
                renderer = plot.getRenderer();
            }
            if (renderer instanceof XYLineAndShapeRenderer) {
                XYData data = (XYData) plot.getDataset(iData);
                if (data != null) {
                    int nSeries = data.getSeriesCount();
                    for (int i = 0; i < nSeries; i++) {
                        Shape shape = (((XYLineAndShapeRenderer) renderer).getSeriesShape(i));
                        String shapeName = shapeNameMap.get(shape);
                        if (shapeName == null) {
                            shapeName = "unknown";
                        }
                        TclList.append(interp, list, TclString.newInstance(shapeName));
                    }
                }
            } else {
                TclList.append(interp, list, TclString.newInstance(""));
            }
        }
        return list;
    }
    public static Shape getSymbolShape(String shapeName) {
         Shape shape = shapeMap.get(shapeName);
         if (shape == null) {
             int index = shapeName.indexOf(':');
             if (index != -1) {
                 String baseName = shapeName.substring(0,index); 
                 int size = Integer.parseInt(shapeName.substring(index+1)); 
                 double scale = size/baseSize;
                 Shape baseShape = shapeMap.get(baseName);
                 if (baseShape != null) {
                     AffineTransform aTrans = AffineTransform.getScaleInstance(scale,scale);
                     Shape newShape = aTrans.createTransformedShape(baseShape);
                     shapeMap.put(shapeName,newShape);
                     shapeNameMap.put(newShape,shapeName);
                     shape = newShape;
                 }
             }
         }
         return shape;
    }

    public void updateShapes(String[] shapeNames) {
        if ((shapeNames == null) || (shapeNames.length == 0)) {
            return;
        }
        int nDatasets = plot.getDatasetCount();
        int j = 0;
        for (int iData = 0; iData < nDatasets; iData++) {
            XYItemRenderer renderer = plot.getRenderer(iData);
            if (renderer == null) {
                renderer = plot.getRenderer();
            }
            if (renderer instanceof XYLineAndShapeRenderer) {
                XYData data = (XYData) plot.getDataset(iData);
                if (data != null) {
                    int nSeries = data.getSeriesCount();
                    for (int i = 0; i < nSeries; i++) {

                        Shape shape = getSymbolShape(shapeNames[j % shapeNames.length]);
                        if (shape == null) {
                            shape = seriesShapes[0];
                        }
                        ((XYLineAndShapeRenderer) renderer).setSeriesShape(i, shape);
                        j++;
                    }
                }
            }
        }
    }
    public NumberAxis getDomainAxis () {
	    return (NumberAxis) plot.getDomainAxis();
    }
  public NumberAxis getRangeAxis () {
	    return (NumberAxis) plot.getRangeAxis();
    }

 }
