/*
 * SwkShapeRunnable.java
 *
 * Created on November 26, 2005, 10:33 AM
 */
package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.SwkException;

import java.util.Vector;

import javax.swing.SwingUtilities;


/**
 *
 * @author brucejohnson
 */
public class SwkShapeRunnable implements Runnable {
    String tag = null;
    CanvasParameter[] parameters = null;
    SwkImageCanvas swkCanvas = null;
    SwkShape swkShape = null;

    /** Creates a new instance of SwkShapeRunnable */
    public SwkShapeRunnable(final SwkImageCanvas swkCanvas, final String tag,
        final CanvasParameter[] parameters) {
        this.swkCanvas = swkCanvas;
        this.tag = tag;
        this.parameters = parameters;
    }

    public SwkShapeRunnable(final SwkImageCanvas swkCanvas, SwkShape swkShape,
        final CanvasParameter[] parameters) {
        this.swkCanvas = swkCanvas;
        this.tag = null;
        this.swkShape = swkShape;
        this.parameters = parameters;
    }

    public void exec() {
        try {
            SwingUtilities.invokeAndWait(this);
        } catch (Exception iE) {
        }
    }

    public void run() {
        if (parameters != null) {
            if (swkShape != null) {
                configShape(swkShape);
            } else {
                Vector shapeList = null;

                try {
                    shapeList = swkCanvas.getShapesWithTags(tag);
                } catch (SwkException swkE) {
                }

                if (shapeList != null) {
                    for (int i = 0; i < shapeList.size(); i++) {
                        SwkShape swkShape = (SwkShape) shapeList.elementAt(i);
                        configShape(swkShape);
                    }

                    if (shapeList.size() > 0) {
                        swkCanvas.repaint();
                    }
                }
            }
        }
    }

    public void configShape(SwkShape swkShape) {
        swkShape.newStroke = false;
        swkShape.newTransform = false;
        for (int j = 0; j < parameters.length; j++) {
            if (parameters[j] != null) {
                try {
                parameters[j].exec(swkCanvas, swkShape);
                } catch (Exception e) {
                   e.printStackTrace();
                }
            }
        }

        swkShape.updateStroke();
        swkShape.applyCoordinates();
    }
}
