package com.onemoonscientific.swank.chart;

import com.onemoonscientific.swank.canvas.CanvasParameter;
import com.onemoonscientific.swank.canvas.DoubleParameter;
import com.onemoonscientific.swank.canvas.SwkImageCanvas;
import com.onemoonscientific.swank.canvas.SwkShape;
import org.jfree.chart.axis.NumberAxis;

class DMinParameter extends DoubleParameter {

	private static String name = "dmin";

	DMinParameter() {
		CanvasParameter.addParameter(this);
	}

	public String getName() {
		return name;
	}

	public double getValue(SwkShape swkShape) {
		NumberAxis axis = (NumberAxis) ((NumberDomain) swkShape).getDomainAxis();
		return axis.getLowerBound();
	}

	public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
		NumberAxis axis = (NumberAxis) ((NumberDomain) swkShape).getDomainAxis();
		axis.setLowerBound(getNewValue());
	}
}
