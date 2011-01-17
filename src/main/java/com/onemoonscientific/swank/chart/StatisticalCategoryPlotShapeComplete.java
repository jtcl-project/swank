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

import org.jfree.ui.RectangleEdge;

import tcl.lang.*;

import java.awt.*;
import java.awt.geom.*;


import java.util.*;

public class StatisticalCategoryPlotShapeComplete extends SwkShape implements DatasetShape, NumberRange, PlotInterface {

	static CanvasParameter[] parameters = {
		new TagsParameter(), new DatasetParameter(), new FillParameter(), new LegendStateParameter(), new LegendLocParameter(),
		new RLabelParameter(), new RMinParameter(), new RMaxParameter(), new RAutoParameter(),
		new TransformerParameter()};
	static TreeMap parameterMap = new TreeMap();

	static {
		initializeParameters(parameters, parameterMap);
	}
	GeneralPath gPath = new GeneralPath();
	float radius = 2.0f;
	int symbolType = 3;
	boolean drawLine = false;
	CategoryPlot plot = new CategoryPlot();
	PlotLegend plotLegend = new PlotLegend(plot);
	ChartRenderingInfo chartInfo = new ChartRenderingInfo();
	PlotRenderingInfo state = new PlotRenderingInfo(chartInfo);
	Rectangle2D.Double plotArea = null;
	Rectangle2D rect2D= null;
	String plotType = "statplot";
	String legendLoc = "s.n";
	boolean legendState = false;

	public StatisticalCategoryPlotShapeComplete() {
		rect2D = new Rectangle2D.Double();
		plot.setDataset(new DefaultStatisticalCategoryData());
		plot.setDomainAxis(new CategoryAxis());
		plot.setRangeAxis(new NumberAxis());
		setRenderer("render");
		setShape(rect2D);
	}

        public String getType() {
            return plotType;
        }
	public CategoryPlot getPlot() {
		return plot;
	}

    /**
     *
     * @param renderName
     */
    public void setRenderer(String renderName) {
		plot.setRenderer(new StatisticalBarRenderer());
	}

	public void setDataset(String name) {
		DefaultStatisticalCategoryData categoryData = DefaultStatisticalCategoryData.get(name);
		//HighLowData categoryData = HighLowData.get(name);
		plot.setDataset(categoryData);
	}

	public void setDataset(int index, String name) {
		DefaultStatisticalCategoryData categoryData = DefaultStatisticalCategoryData.get(name);
		//HighLowData categoryData = HighLowData.get(name);
		plot.setDataset(index, categoryData);
	}

	public void setDrawline(boolean newValue) {
		drawLine = newValue;
	}

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

    /**
     *
     * @return
     */
    public String getLegendLoc() {
		return legendLoc;
	}

	public void setLegendLoc(String loc) {
		legendLoc = loc;
	}

	public boolean getLegendState() {
		return legendState;
	}

	public void setLegendState(boolean state) {
		legendState = state;
	}

    /**
     *
     * @param canvas
     * @param coords
     * @throws SwkException
     */
    public void coords(SwkImageCanvas canvas, double[] coords)
	    throws SwkException {
		if (coords.length != 4) {
			throw new SwkException("wrong # coordinates: expected 8, got "
			    + coords.length);
		}

		gPath.reset();

		if ((storeCoords == null) || (storeCoords.length != coords.length)) {
			storeCoords = new double[coords.length];
		}

		System.arraycopy(coords, 0, storeCoords, 0, coords.length);
		applyCoordinates();
	}

	public void applyCoordinates() {
                rect2D.setFrame(storeCoords[0], storeCoords[1],
                         storeCoords[2] - storeCoords[0], storeCoords[3] - storeCoords[1]);

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

	public TreeMap getParameterMap() {
		return parameterMap;
	}


	public void paintShape(Graphics2D g2) {
		Point2D anchor = new Point2D.Double();
		applyCoordinates();
		Rectangle2D plotAreaNow = (Rectangle2D) plotArea.clone();

		AffineTransform shapeTransform = getTransform();
		if (shapeTransform != null) {
			plotAreaNow = shapeTransform.createTransformedShape(plotAreaNow).getBounds2D();
		}

		if (legendState) {
			Rectangle2D legendArea = plotLegend.arrangeLegend(g2, plotAreaNow);
			plot.draw(g2, plotAreaNow, anchor, null, state);
			plotLegend.drawLegend(g2, legendArea);
		} else {
			plot.draw(g2, plotAreaNow, anchor, null, state);
		}
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


	public void addSymbol(float x1, float y1, float radius) {
	}

	public TclObject getDatasets(Interp interp) throws TclException {
		int nDatasets = plot.getDatasetCount();
		TclObject list = TclList.newInstance();
		for (int i = 0; i < nDatasets; i++) {
			DefaultStatisticalCategoryData catData = (DefaultStatisticalCategoryData) plot.getDataset(i);
			TclList.append(interp, list, TclString.newInstance(catData.getName()));
		}
		return list;
	}

	public void updateDatasets(String[] datasetNames) {
		int nDatasets = plot.getDatasetCount();
		for (int i = 0; i < datasetNames.length; i++) {
			setDataset(i, datasetNames[i]);
			if (i >= nDatasets) {
				StatisticalBarRenderer newRenderer = new StatisticalBarRenderer();
				//newRenderer.setToolTipGenerator(XYLineAndShapeComplete.generator);
				plot.setRenderer(i, newRenderer);
			}
		}
	}

	public TclObject getColors(Interp interp) throws TclException {
		TclObject list = TclList.newInstance();
		return list;

	}

	public void updateColors(Color[] colors) {
	}

	public CategoryAxis getDomainAxis() {
		return (CategoryAxis) plot.getDomainAxis();
	}

	public NumberAxis getRangeAxis() {
		return (NumberAxis) plot.getRangeAxis();
	}
}
