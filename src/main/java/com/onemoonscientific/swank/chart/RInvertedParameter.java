package com.onemoonscientific.swank.chart;

import com.onemoonscientific.swank.canvas.BooleanParameter;
import com.onemoonscientific.swank.canvas.CanvasParameter;
import com.onemoonscientific.swank.canvas.SwkImageCanvas;
import com.onemoonscientific.swank.canvas.SwkShape;
import org.jfree.chart.axis.NumberAxis;

class RInvertedParameter extends BooleanParameter {

    private static String name = "rinverted";

    RInvertedParameter() {
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
        NumberAxis axis = (NumberAxis) ((NumberRange) swkShape).getRangeAxis();
        return axis.isInverted();
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        NumberAxis axis = (NumberAxis) ((NumberRange) swkShape).getRangeAxis();
        axis.setInverted(getNewValue());
    }
}
