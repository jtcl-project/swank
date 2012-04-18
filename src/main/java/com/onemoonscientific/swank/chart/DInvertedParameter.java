package com.onemoonscientific.swank.chart;

import com.onemoonscientific.swank.canvas.BooleanParameter;
import com.onemoonscientific.swank.canvas.CanvasParameter;
import com.onemoonscientific.swank.canvas.SwkImageCanvas;
import com.onemoonscientific.swank.canvas.SwkShape;
import org.jfree.chart.axis.NumberAxis;

class DInvertedParameter extends BooleanParameter {

    private static String name = "dinverted";

    DInvertedParameter() {
        CanvasParameter.addParameter(this);
    }

    public String getName() {
        return name;
    }

    @Override
    public String getDefault() {
        return "";
    }

    @Override
    public boolean getValue(SwkShape swkShape) {
		NumberAxis axis = (NumberAxis) ((NumberDomain) swkShape).getDomainAxis();
        return axis.isInverted();
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
		NumberAxis axis = (NumberAxis) ((NumberDomain) swkShape).getDomainAxis();
        axis.setInverted(getNewValue());
    }
}
