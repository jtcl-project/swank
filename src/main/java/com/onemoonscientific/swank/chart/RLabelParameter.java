package com.onemoonscientific.swank.chart;

import com.onemoonscientific.swank.canvas.CanvasParameter;
import com.onemoonscientific.swank.canvas.StringParameter;
import com.onemoonscientific.swank.canvas.SwkImageCanvas;
import com.onemoonscientific.swank.canvas.SwkShape;
import org.jfree.chart.axis.NumberAxis;

class RLabelParameter extends StringParameter {

	private static String name = "rlabel";

	RLabelParameter() {
		CanvasParameter.addParameter(this);
	}

	public String getName() {
		return name;
	}

	public String getValue(SwkShape swkShape) {
		NumberAxis axis = (NumberAxis) ((NumberRange) swkShape).getRangeAxis();
		return axis.getLabel();
	}

	public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
		NumberAxis axis = (NumberAxis) ((NumberRange) swkShape).getRangeAxis();
		axis.setLabel(getNewValue());
	}
}
