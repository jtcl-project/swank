/*
 * SwkShapeRunnable.java
 *
 * Created on November 26, 2005, 10:33 AM
 */
package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.SwkException;

import tcl.lang.Interp;
import tcl.lang.TclException;
import tcl.lang.TclList;
import tcl.lang.TclObject;
import tcl.lang.TclString;

import java.util.Vector;
import java.util.TreeMap;

import javax.swing.SwingUtilities;

/**
 *
 * @author brucejohnson
 */
public class SwkShapeItemGet implements Runnable {

    String tag = null;
    String parString = null;
    SwkImageCanvas swkCanvas = null;
    SwkShape swkShape = null;
    Interp interp = null;

    /** Creates a new instance of SwkShapeRunnable */
    public SwkShapeItemGet(final Interp interp, final SwkImageCanvas swkCanvas,
            final String tag, final String parString) {
        this.interp = interp;
        this.swkCanvas = swkCanvas;
        this.tag = tag;
        this.parString = parString;
    }

    public TclObject exec(boolean configStyle) throws TclException {
        try {
            SwingUtilities.invokeAndWait(this);
        } catch (Exception iE) {
        }

        if (swkShape == null) {
            throw new TclException(interp,
                    "Swank item  \"" + tag + "\" doesn't exist");
        }
        if (parString != null) {
            CanvasParameter par = swkShape.getPar(parString);

            return getParValue(par, configStyle);
        } else {
            TreeMap<String,CanvasParameter> parameterMap = swkShape.getParameterMap();
            if (parameterMap == null) {
                throw new TclException(interp,
                        "Canvas Parameter doesn't exist for Swank Item \"" + tag + "\"");
            }
            TclObject result = TclList.newInstance();
            for (CanvasParameter canvasPar:parameterMap.values()) {
                TclObject value = getParValue(canvasPar, configStyle);
                TclList.append(interp, result, value);
            }

            return result;
        }
    }

    public void run() {
        Vector shapeList = null;

        try {
            shapeList = swkCanvas.getShapesWithTags(tag);
        } catch (SwkException swkE) {
        }

        if (shapeList != null) {
            if (shapeList.size() > 0) {
                swkShape = (SwkShape) shapeList.elementAt(0);
            }
        }
    }

    TclObject getParValue(CanvasParameter par, boolean configStyle)
            throws TclException {
        if (par != null) {
            TclObject value = par.getValue(interp, swkShape);

            if (!configStyle) {
                return value;
            } else {
                TclObject list = TclList.newInstance();
                TclList.append(interp, list,
                        TclString.newInstance("-" + par.getName()));
                TclList.append(interp, list, TclString.newInstance(""));
                TclList.append(interp, list, TclString.newInstance(""));
                TclList.append(interp, list,
                        TclString.newInstance(par.getDefault()));
                TclList.append(interp, list, value);

                return (list);
            }
        } else {
            return TclString.newInstance("");
        }
    }
}
