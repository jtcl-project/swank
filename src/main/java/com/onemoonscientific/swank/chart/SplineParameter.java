package com.onemoonscientific.swank.chart;

import com.onemoonscientific.swank.canvas.CanvasParameter;
import com.onemoonscientific.swank.canvas.IntegerParameter;
import com.onemoonscientific.swank.canvas.SwkImageCanvas;
import com.onemoonscientific.swank.canvas.SwkShape;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;

class SplineParameter extends IntegerParameter {

	private static String name = "spline";

	SplineParameter() {
		CanvasParameter.addParameter(this);
	}

	public String getName() {
		return name;
	}

	public int getValue(SwkShape swkShape) {
		int value = 0;
		XYItemRenderer renderer = ((XYPlotShape) swkShape).renderer;
		if (renderer instanceof XYSplineRenderer) {
			XYSplineRenderer splineRenderer = (XYSplineRenderer) renderer;
			value = splineRenderer.getPrecision();
		}
		return value;
	}

	public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
		XYItemRenderer renderer = ((XYPlotShape) swkShape).renderer;
		int newValue = getNewValue();
		if (newValue <= 0) {
			if (renderer instanceof XYSplineRenderer) {
				XYPlotShape plotShape = (XYPlotShape) swkShape;
                                plotShape.setRenderer();
			}
		} else if (renderer instanceof XYSplineRenderer) {
			XYSplineRenderer splineRenderer = (XYSplineRenderer) renderer;
			splineRenderer.setPrecision(newValue);
		} else {
 		       XYPlotShape plotShape = (XYPlotShape) swkShape;
                       plotShape.setSplineRenderer(newValue);
		}
	}
}
