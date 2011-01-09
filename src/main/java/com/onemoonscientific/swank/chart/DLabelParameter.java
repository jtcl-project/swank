package com.onemoonscientific.swank.chart;

import com.onemoonscientific.swank.canvas.CanvasParameter;
import com.onemoonscientific.swank.canvas.StringParameter;
import com.onemoonscientific.swank.canvas.SwkImageCanvas;
import com.onemoonscientific.swank.canvas.SwkShape;
import org.jfree.chart.axis.NumberAxis;

class DLabelParameter extends StringParameter {

	private static final String name = "dlabel";
    private static final String defValue = "";

	DLabelParameter() {
		CanvasParameter.addParameter(this);
	}

	public String getName() {
		return name;
	}

     public String getDefault() {
        return defValue;
    }

     public String getValue(SwkShape swkShape) {
		NumberAxis axis = (NumberAxis) ((NumberDomain) swkShape).getDomainAxis();
		return axis.getLabel();
	}

	public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
		NumberAxis axis = (NumberAxis) ((NumberDomain) swkShape).getDomainAxis();
		axis.setLabel(getNewValue());
	}
}
