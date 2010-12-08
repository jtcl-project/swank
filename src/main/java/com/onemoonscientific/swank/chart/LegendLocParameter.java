package com.onemoonscientific.swank.chart;

import com.onemoonscientific.swank.canvas.CanvasParameter;
import com.onemoonscientific.swank.canvas.StringParameter;
import com.onemoonscientific.swank.canvas.SwkImageCanvas;
import com.onemoonscientific.swank.canvas.SwkShape;

class LegendLocParameter extends StringParameter {

	private static String name = "legendloc";
	LegendLocParameter() {
		CanvasParameter.addParameter(this);
	}

	public String getName() {
		return name;
	}

	public String getValue(SwkShape swkShape) {
		return ((PlotInterface) swkShape).getLegendLoc();
	}

	public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
                ((PlotInterface) swkShape).setLegendLoc(getNewValue());
	}
}
