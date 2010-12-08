package com.onemoonscientific.swank.chart;

import com.onemoonscientific.swank.canvas.BooleanParameter;
import com.onemoonscientific.swank.canvas.CanvasParameter;
import com.onemoonscientific.swank.canvas.SwkImageCanvas;
import com.onemoonscientific.swank.canvas.SwkShape;

class LegendStateParameter extends BooleanParameter {

	private static String name = "legendstate";
	LegendStateParameter() {
		CanvasParameter.addParameter(this);
	}

	public String getName() {
		return name;
	}

	public boolean getValue(SwkShape swkShape) {
		return ((PlotInterface) swkShape).getLegendState();
	}

	public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
                ((PlotInterface) swkShape).setLegendState(getNewValue());
	}
}
