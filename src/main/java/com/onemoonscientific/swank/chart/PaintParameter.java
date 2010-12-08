package com.onemoonscientific.swank.chart;

import com.onemoonscientific.swank.SwankUtil;
import com.onemoonscientific.swank.canvas.CanvasParameter;
import com.onemoonscientific.swank.canvas.SwkImageCanvas;
import com.onemoonscientific.swank.canvas.SwkShape;
import java.awt.Color;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import tcl.lang.Interp;
import tcl.lang.TclException;
import tcl.lang.TclList;
import tcl.lang.TclObject;
import tcl.lang.TclString;

class PaintParameter extends CanvasParameter {

	private static String name = "paint";
	Color[] colors = new Color[0];

	PaintParameter() {
		CanvasParameter.addParameter(this);
	}

	public String getName() {
		return name;
	}

	public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg) throws TclException {
		TclObject[] colorsList = TclList.getElements(interp, arg);
		if (colorsList.length == 0) {
			throw new TclException(interp, "bad color value, must be \"color1 color2 ...\"");
		}
		Color[] colorsTemp = new Color[colorsList.length];
		for (int i = 0; i < colorsList.length; i++) {
			colorsTemp[i] = SwankUtil.getColor(interp, colorsList[i]);
		}
		colors = colorsTemp;
	}

	public TclObject getValue(Interp interp, SwkShape swkShape) throws TclException {
                return ((DatasetShape) swkShape).getColors(interp);
	}

	public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
                ((DatasetShape) swkShape).updateColors(colors);
	}
}
