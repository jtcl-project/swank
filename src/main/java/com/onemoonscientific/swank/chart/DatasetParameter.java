package com.onemoonscientific.swank.chart;

import com.onemoonscientific.swank.canvas.CanvasParameter;
import com.onemoonscientific.swank.canvas.SwkImageCanvas;
import com.onemoonscientific.swank.canvas.SwkShape;
import tcl.lang.Interp;
import tcl.lang.TclException;
import tcl.lang.TclList;
import tcl.lang.TclObject;

class DatasetParameter extends CanvasParameter {

    private static String name = "dataset";
    String[] datasetNames = new String[0];

    DatasetParameter() {
        CanvasParameter.addParameter(this);
    }

    public String getName() {
        return name;
    }

    public String getDefault() {
        return "";
    }

    public TclObject getValue(Interp interp, SwkShape swkShape) throws TclException {
        return ((DatasetShape) swkShape).getDatasets(interp);
    }

    @Override
    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg) throws TclException {
        TclObject[] datasetNameList = TclList.getElements(interp, arg);
        if (datasetNameList.length == 0) {
            throw new TclException(interp, "bad dataset value, must be \"dataset1 dataset2 ...\"");
        }
        String[] datasetNamesTmp = new String[datasetNameList.length];
        for (int i = 0; i < datasetNameList.length; i++) {
            datasetNamesTmp[i] = datasetNameList[i].toString();
        }
        datasetNames = datasetNamesTmp;
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        ((DatasetShape) swkShape).updateDatasets(datasetNames);
    }
}
