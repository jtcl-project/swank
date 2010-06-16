package com.onemoonscientific.swank.chart;

import com.onemoonscientific.swank.canvas.CanvasParameter;
import com.onemoonscientific.swank.canvas.StringParameter;
import com.onemoonscientific.swank.canvas.SwkImageCanvas;
import com.onemoonscientific.swank.canvas.SwkShape;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

class ShapesvisibleParameter extends StringParameter {

	private static String name = "shapesvisible";

	ShapesvisibleParameter() {
		CanvasParameter.addParameter(this);
	}

	public String getName() {
		return name;
	}

	public String getValue(SwkShape swkShape) {
		XYPlot plot = ((XYPlotShape) swkShape).plot;
		StringBuffer sBuf = new StringBuffer();
		int nDatasets = plot.getDatasetCount();
		int j = 0;
		for (int iData = 0; iData < nDatasets; iData++) {
			XYItemRenderer renderer = ((XYPlotShape) swkShape).plot.getRenderer(iData);
			if (renderer == null) {
				renderer = ((XYPlotShape) swkShape).plot.getRenderer();
			}
			if (renderer instanceof XYLineAndShapeRenderer) {
				XYData data = (XYData) plot.getDataset(iData);
				int nSeries = data.getSeriesCount();
				for (int i = 0; i < nSeries; i++) {
					boolean bValue = (((XYLineAndShapeRenderer) renderer).getSeriesShapesVisible(i)).booleanValue();
					if (i > 0) {
						sBuf.append(' ');
					}
					if (bValue) {
						sBuf.append('1');
					} else {
						sBuf.append('0');
					}
					j++;
				}
			} else {
				sBuf.append('0');
			}
		}
		return sBuf.toString();
	}

	public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
		XYPlot plot = ((XYPlotShape) swkShape).plot;
		String newValue = getNewValue();
		String[] values = newValue.split(" ");
		int nDatasets = plot.getDatasetCount();
		int j = 0;
		for (int iData = 0; iData < nDatasets; iData++) {
			XYItemRenderer renderer = ((XYPlotShape) swkShape).plot.getRenderer(iData);
			if (renderer == null) {
				renderer = ((XYPlotShape) swkShape).plot.getRenderer();
			}
			if (renderer instanceof XYLineAndShapeRenderer) {
				XYData data = (XYData) plot.getDataset(iData);
				int nSeries = data.getSeriesCount();
				for (int i = 0; i < nSeries; i++) {
					boolean bValue = true;
					if (values.length > j) {
						bValue = values[j].charAt(0) == '1';
					}
					((XYLineAndShapeRenderer) renderer).setSeriesShapesVisible(i, bValue);
					j++;
				}
			}
		}
	}
}
