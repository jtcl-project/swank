package com.onemoonscientific.swank.chart;

import com.onemoonscientific.swank.canvas.CanvasParameter;
import com.onemoonscientific.swank.canvas.StringParameter;
import com.onemoonscientific.swank.canvas.SwkImageCanvas;
import com.onemoonscientific.swank.canvas.SwkShape;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import tcl.lang.Interp;
import tcl.lang.TclException;
import tcl.lang.TclList;
import tcl.lang.TclObject;
import tcl.lang.TclBoolean;

class LinesvisibleParameter extends CanvasParameter {
       boolean[] visible = new boolean[0];
        private static final String name = "linesvisible";

	LinesvisibleParameter() {
		CanvasParameter.addParameter(this);
	}

	public String getName() {
		return name;
	}
       public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg) throws TclException {
                TclObject[] visibleList = TclList.getElements(interp, arg);
                if (visibleList.length == 0) {
                        throw new TclException(interp, "bad visible value, must be \"value1 value2 ...\"");
                }
                boolean[] visibleTemp = new boolean[visibleList.length];
                for (int i = 0; i < visibleList.length; i++) {
                        visibleTemp[i] = TclBoolean.get(interp, visibleList[i]);
                }
                visible = visibleTemp;
        }

	public TclObject getValue(Interp interp, SwkShape swkShape) throws TclException {
		XYLineAndShapeComplete xyShape = (XYLineAndShapeComplete) swkShape;
		XYPlot plot = xyShape.plot;
                TclObject result = TclList.newInstance();
		int nDatasets = plot.getDatasetCount();
		int j = 0;
                boolean[] linesVisible = xyShape.getLinesVisible();
                for (boolean bValue:linesVisible) {
                    TclList.append(interp,result,TclBoolean.newInstance(bValue));
                }
		for (int iData = 0; iData < nDatasets; iData++) {
			XYItemRenderer renderer = ((XYPlotShape) swkShape).plot.getRenderer(iData);
			if (renderer == null) {
				renderer = ((XYPlotShape) swkShape).plot.getRenderer();
			}
			if (renderer instanceof XYLineAndShapeRenderer) {
				XYData data = (XYData) plot.getDataset(iData);
                                if (data != null) {
				int nSeries = data.getSeriesCount();
				for (int i = 0; i < nSeries; i++) {
					Boolean sVisible = ((XYLineAndShapeRenderer) renderer).getSeriesLinesVisible(i);
					boolean bValue = true;
					if (sVisible != null) {
						bValue = sVisible.booleanValue();
					}
                                        TclList.append(interp,result,TclBoolean.newInstance(bValue));
					j++;
				}
				}
			} else {
                                TclList.append(interp,result,TclBoolean.newInstance(false));
			}
		}
		return result;
	}

	public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
		if (swkShape instanceof XYLineAndShapeComplete)  {
		    XYLineAndShapeComplete xyShape = (XYLineAndShapeComplete) swkShape;
                    xyShape.setLinesVisible(visible);
                }
	}
}
