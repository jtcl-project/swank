package com.onemoonscientific.swank.chart;

import com.onemoonscientific.swank.canvas.BooleanParameter;
import com.onemoonscientific.swank.canvas.CanvasParameter;
import com.onemoonscientific.swank.canvas.SwkImageCanvas;
import com.onemoonscientific.swank.canvas.SwkShape;
import org.jfree.chart.axis.NumberAxis;

class DAutoParameter extends BooleanParameter {

	private static String name = "dauto";

	DAutoParameter() {
		CanvasParameter.addParameter(this);
	}

	public String getName() {
		return name;
	}

	public boolean getValue(SwkShape swkShape) {
		NumberAxis axis = (NumberAxis) ((NumberDomain) swkShape).getDomainAxis();
		return axis.isAutoRange();
	}

	public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
		NumberAxis axis = (NumberAxis) ((NumberDomain) swkShape).getDomainAxis();
		axis.setAutoRange(getNewValue());
	}
}
