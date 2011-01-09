package com.onemoonscientific.swank.chart;

import com.onemoonscientific.swank.canvas.CanvasParameter;
import com.onemoonscientific.swank.canvas.DoubleParameter;
import com.onemoonscientific.swank.canvas.SwkImageCanvas;
import com.onemoonscientific.swank.canvas.SwkShape;
import org.jfree.chart.axis.NumberAxis;

class DMaxParameter extends DoubleParameter {

	private static String name = "dmax";

	DMaxParameter() {
		CanvasParameter.addParameter(this);
	}

	public String getName() {
		return name;
	}
    public String getDefault() {
        return "";
    }

	public double getValue(SwkShape swkShape) {
		NumberAxis axis = (NumberAxis) ((NumberDomain) swkShape).getDomainAxis();
		return axis.getUpperBound();
	}

	public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
		NumberAxis axis = (NumberAxis) ((NumberDomain) swkShape).getDomainAxis();
		axis.setUpperBound(getNewValue());
	}
}
