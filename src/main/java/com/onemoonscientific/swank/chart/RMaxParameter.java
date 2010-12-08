package com.onemoonscientific.swank.chart;

import com.onemoonscientific.swank.canvas.CanvasParameter;
import com.onemoonscientific.swank.canvas.DoubleParameter;
import com.onemoonscientific.swank.canvas.SwkImageCanvas;
import com.onemoonscientific.swank.canvas.SwkShape;
import org.jfree.chart.axis.NumberAxis;

class RMaxParameter extends DoubleParameter {

	private static String name = "rmax";

	RMaxParameter() {
		CanvasParameter.addParameter(this);
	}

	public String getName() {
		return name;
	}

	public double getValue(SwkShape swkShape) {
		NumberAxis axis = (NumberAxis) ((NumberRange) swkShape).getRangeAxis();
		return axis.getUpperBound();
	}

	public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
		NumberAxis axis = (NumberAxis) ((NumberRange) swkShape).getRangeAxis();
		axis.setUpperBound(getNewValue());
	}
}
