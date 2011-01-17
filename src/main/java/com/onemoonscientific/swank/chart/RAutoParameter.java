package com.onemoonscientific.swank.chart;

import com.onemoonscientific.swank.canvas.BooleanParameter;
import com.onemoonscientific.swank.canvas.CanvasParameter;
import com.onemoonscientific.swank.canvas.SwkImageCanvas;
import com.onemoonscientific.swank.canvas.SwkShape;
import org.jfree.chart.axis.NumberAxis;

class RAutoParameter extends BooleanParameter {

	private static String name = "rauto";

	RAutoParameter() {
		CanvasParameter.addParameter(this);
	}

	public String getName() {
		return name;
	}

    @Override
	public boolean getValue(SwkShape swkShape) {
		NumberAxis axis = (NumberAxis) ((NumberRange) swkShape).getRangeAxis();
		return axis.isAutoRange();
	}

	public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
		NumberAxis axis = (NumberAxis) ((NumberRange) swkShape).getRangeAxis();
		axis.setAutoRange(getNewValue());
	}
}
