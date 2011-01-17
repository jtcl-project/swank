/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * --------------------
 * StatisticalXYBarRenderer.java
 * --------------------
*/
package com.onemoonscientific.swank.chart;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.GradientPaint;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

/**
 * A renderer that draws high/low/open/close markers on an {@link XYPlot} 
 * (requires a {@link OHLCDataset}).  This renderer does not include code to 
 * calculate the crosshair point for the plot.
 */
public class StatisticalXYBarRenderer extends XYBarRenderer
                                        {
    
    /** For serialization. */
    private static final long serialVersionUID = -8135673815876552516L;
    

    /**
     * The default constructor.
     */
    public StatisticalXYBarRenderer() {
        super();
    }
   /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the plot is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color 
     *              information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  crosshair information for the plot 
     *                        (<code>null</code> permitted).
     * @param pass  the pass index.
     */
    public void drawItem(Graphics2D g2,
                         XYItemRendererState state,
                         Rectangle2D dataArea,
                         PlotRenderingInfo info,
                         XYPlot plot,
                         ValueAxis domainAxis,
                         ValueAxis rangeAxis,
                         XYDataset dataset,
                         int series,
                         int item,
                         CrosshairState crosshairState,
                         int pass) {

        if (!getItemVisible(series, item)) {
            return;   
        }
        IntervalXYDataset intervalDataset = (IntervalXYDataset) dataset;

        double value0;
        double value1;
        if (this.getUseYInterval()) {
            value0 = intervalDataset.getStartYValue(series, item);
            value1 = intervalDataset.getEndYValue(series, item);
        }
        else {
            value0 = this.getBase();
            value1 = intervalDataset.getYValue(series, item);
        }
        if (Double.isNaN(value0) || Double.isNaN(value1)) {
            return;
        }

        double translatedValue0 = rangeAxis.valueToJava2D(
            value0, dataArea, plot.getRangeAxisEdge()
        );
        double translatedValue1 = rangeAxis.valueToJava2D(
            value1, dataArea, plot.getRangeAxisEdge()
        );
        double translatedLineY1 = 0.0;
        double translatedLineY2 = 0.0;
        boolean drawStatLine = false;
        if (dataset instanceof XYTableStatsData) {
            XYTableStatsData sData = (XYTableStatsData) dataset;
            double mean = sData.getMeanValue(series, item);
            double sDev = sData.getSDevValue(series, item);
            translatedLineY1 = rangeAxis.valueToJava2D(
               mean-sDev/2.0, dataArea, plot.getRangeAxisEdge());
            translatedLineY2 = rangeAxis.valueToJava2D(
               mean+sDev/2.0, dataArea, plot.getRangeAxisEdge());
            drawStatLine = true;
        }

        RectangleEdge location = plot.getDomainAxisEdge();
        Number startXNumber = intervalDataset.getStartX(series, item);
        if (startXNumber == null) {
            return;
        }
        double translatedStartX = domainAxis.valueToJava2D(
            startXNumber.doubleValue(), dataArea, location
        );

        Number endXNumber = intervalDataset.getEndX(series, item);
        if (endXNumber == null) {
            return;
        }
        double translatedEndX = domainAxis.valueToJava2D(
            endXNumber.doubleValue(), dataArea, location
        );

        double translatedWidth = Math.max(
            1, Math.abs(translatedEndX - translatedStartX)
        );
        double translatedHeight = Math.abs(translatedValue1 - translatedValue0);

        if (getMargin() > 0.0) {
            double cut = translatedWidth * getMargin();
            translatedWidth = translatedWidth - cut;
            translatedStartX = translatedStartX + cut / 2;
        }

        Rectangle2D bar = null;
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            bar = new Rectangle2D.Double(
                Math.min(translatedValue0, translatedValue1), 
                Math.min(translatedStartX, translatedEndX),
                translatedHeight, translatedWidth);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            bar = new Rectangle2D.Double(
                Math.min(translatedStartX, translatedEndX), 
                Math.min(translatedValue0, translatedValue1), 
                translatedWidth, translatedHeight);
        }
        double translatedLineX = (translatedStartX+translatedEndX)/2.0;
        double translatedLineX1 = translatedLineX - translatedWidth/4;
        double translatedLineX2 = translatedLineX + translatedWidth/4;

        Paint itemPaint = getItemPaint(series, item);
        if (getGradientPaintTransformer() 
                != null && itemPaint instanceof GradientPaint) {
            GradientPaint gp = (GradientPaint) itemPaint;
            itemPaint = getGradientPaintTransformer().transform(gp, bar);
        }
        g2.setPaint(itemPaint);
        g2.fill(bar);
        if (isDrawBarOutline() 
                && Math.abs(translatedEndX - translatedStartX) > 3) {
            Stroke stroke = getItemOutlineStroke(series, item);
            Paint paint = getItemOutlinePaint(series, item);
            if (stroke != null && paint != null) {
                g2.setStroke(stroke);
                g2.setPaint(paint);
                g2.draw(bar);                
            }
        }
        if (drawStatLine) {
            Stroke itemStroke = getItemStroke(series, item);
            g2.setStroke(itemStroke);
            g2.setPaint(Color.BLACK);
            if (orientation == PlotOrientation.HORIZONTAL) {
                g2.draw(new Line2D.Double(translatedLineY1, translatedLineX, translatedLineY2, translatedLineX));
                g2.draw(new Line2D.Double(translatedLineY1, translatedLineX1, translatedLineY1, translatedLineX2));
                g2.draw(new Line2D.Double(translatedLineY2, translatedLineX1, translatedLineY2, translatedLineX2));
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                g2.draw(new Line2D.Double(translatedLineX, translatedLineY2, translatedLineX, translatedLineY1));
                g2.draw(new Line2D.Double(translatedLineX1, translatedLineY1, translatedLineX2, translatedLineY1));
                g2.draw(new Line2D.Double(translatedLineX1, translatedLineY2, translatedLineX2, translatedLineY2));
            }
        }
        
        // TODO: we need something better for the item labels
        if (isItemLabelVisible(series, item)) {
            drawItemLabel(
                g2, orientation, dataset, series, item, bar.getCenterX(), 
                bar.getY(), value1 < 0.0
            );
        }

        // add an entity for the item...
        if (info != null) {
            EntityCollection entities = info.getOwner().getEntityCollection();
            if (entities != null) {
                String tip = null;
                XYToolTipGenerator generator 
                    = getToolTipGenerator(series, item);
                if (generator != null) {
                    tip = generator.generateToolTip(dataset, series, item);
                } else {
                    tip = series+" "+item+" "+value1;
                }
                String url = null;
                if (getURLGenerator() != null) {
                    url = getURLGenerator().generateURL(dataset, series, item);
                }
                XYItemEntity entity = new XYItemEntity(
                    bar, dataset, series, item, tip, url
                );
                entities.add(entity);
            }
        }

    }

    
    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the plot is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color 
     *              information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  crosshair information for the plot 
     *                        (<code>null</code> permitted).
     * @param pass  the pass index.
     */
    public void drawItemOld(Graphics2D g2,
                         XYItemRendererState state,
                         Rectangle2D dataArea,
                         PlotRenderingInfo info,
                         XYPlot plot,
                         ValueAxis domainAxis,
                         ValueAxis rangeAxis,
                         XYDataset dataset,
                         int series,
                         int item,
                         CrosshairState crosshairState,
                         int pass) {

        // first make sure we have a valid x value...
        Number x = dataset.getX(series, item);
        if (x == null) {  
            return;    // if x is null, we can't do anything
        }
        double xdouble = x.doubleValue();
        if (!domainAxis.getRange().contains(xdouble)) {
            return;    // the x value is not within the axis range
        }
        double xx = domainAxis.valueToJava2D(xdouble, dataArea, 
                plot.getDomainAxisEdge());
        
        // setup for collecting optional entity info...
        Shape entityArea = null;
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }

        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge location = plot.getRangeAxisEdge();

        //Paint itemPaint = getItemPaint(series, item);
        Paint itemPaint = Color.BLACK;
        Stroke itemStroke = getItemStroke(series, item);
        g2.setPaint(itemPaint);
        g2.setStroke(itemStroke);
        
        if (dataset instanceof XYTableStatsData) {
            XYTableStatsData sData = (XYTableStatsData) dataset;
            double delta = 2.0;
            if (domainAxis.isInverted()) {
                delta = -delta;
            }
            
            double mean = sData.getMeanValue(series, item);
            double sDev = sData.getSDevValue(series, item);
            double yHigh = mean+sDev/2.0;
            double yLow = mean-sDev/2.0;

            if (!Double.isNaN(yHigh) && !Double.isNaN(yLow)) {
                double yyHigh = rangeAxis.valueToJava2D(yHigh, dataArea, 
                        location);
                double yyMean = rangeAxis.valueToJava2D(mean, dataArea, 
                        location);
                double yyLow = rangeAxis.valueToJava2D(yLow, dataArea, 
                        location);
                if (orientation == PlotOrientation.HORIZONTAL) {
                    g2.draw(new Line2D.Double(yyLow, xx, yyHigh, xx));
                    g2.draw(new Line2D.Double(yyMean, xx + delta, yyMean, 
                                xx-delta));   
                    entityArea = new Rectangle2D.Double(Math.min(yyLow, yyHigh),
                            xx - 1.0, Math.abs(yyHigh - yyLow), 2.0);
                }
                else if (orientation == PlotOrientation.VERTICAL) {
                    g2.draw(new Line2D.Double(xx, yyLow, xx, yyHigh));   
                    g2.draw(new Line2D.Double(xx - delta, yyMean, xx+delta, 
                                yyMean));   
                    entityArea = new Rectangle2D.Double(xx - 1.0, 
                            Math.min(yyLow, yyHigh), 2.0,  
                            Math.abs(yyHigh - yyLow));
                }
            }
        }
        else {
            // not a StatisticalXYDataset, so just draw a line connecting this point 
            // with the previous point...
            if (item > 0) {
                Number x0 = dataset.getX(series, item - 1);
                Number y0 = dataset.getY(series, item - 1);
                Number y = dataset.getY(series, item);
                if (x0 == null || y0 == null || y == null) {
                    return;
                }
                double xx0 = domainAxis.valueToJava2D(x0.doubleValue(), 
                        dataArea, plot.getDomainAxisEdge());
                double yy0 = rangeAxis.valueToJava2D(y0.doubleValue(), 
                        dataArea, location);
                double yy = rangeAxis.valueToJava2D(y.doubleValue(), dataArea, 
                        location);
                if (orientation == PlotOrientation.HORIZONTAL) {
                    g2.draw(new Line2D.Double(yy0, xx0, yy, xx));
                }
                else if (orientation == PlotOrientation.VERTICAL) {
                    g2.draw(new Line2D.Double(xx0, yy0, xx, yy));
                }
            }
        }
        
        // add an entity for the item...
        if (entities != null) {
            String tip = null;
            XYToolTipGenerator generator = getToolTipGenerator(series, item);
            if (generator != null) {
                tip = generator.generateToolTip(dataset, series, item);
            }
            String url = null;
            if (getURLGenerator() != null) {
                url = getURLGenerator().generateURL(dataset, series, item);
            }
            XYItemEntity entity = new XYItemEntity(entityArea, dataset, 
                    series, item, tip, url);
            entities.add(entity);
        }

    }
    
    /**
     * Returns a clone of the renderer.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException  if the renderer cannot be cloned.
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    /**
     * Tests this renderer for equality with an arbitrary object.
     * 
     * @param obj  the object (<code>null</code> permitted).
     * 
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof StatisticalXYBarRenderer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        return true;
    }
    
    /**
     * Provides serialization support.
     *
     * @param stream  the input stream.
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream) 
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
    }
    
    /**
     * Provides serialization support.
     *
     * @param stream  the output stream.
     *
     * @throws IOException  if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

}
