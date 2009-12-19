/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.onemoonscientific.swank.canvas;

import java.awt.BasicStroke;

/**
 *
 * @author brucejohnson
 */
public class StrokeParameters {

    static private final StrokeParameters defaultPar = new StrokeParameters();
    int cap = BasicStroke.CAP_SQUARE;
    int join = BasicStroke.JOIN_MITER;
    float miterLimit = 10.0f;
    String dashString = "";
    float[] dash = {};
    float dashPhase = 0.0f;
    boolean dashIntPattern = false;

    static StrokeParameters getDefault() {
        return defaultPar;
    }

    BasicStroke getStroke(float width) {
        BasicStroke stroke = null;
        if (dashString.equals("")) {
            stroke = new BasicStroke(width, cap, join, miterLimit);
        } else {
            if (dashIntPattern || (width == 1.0f) || (width == 0.0f)) {
                stroke = new BasicStroke(width, cap, join, miterLimit,
                        dash, dashPhase);
            } else {
               float[] dashTemp = new float[dash.length];
                for (int i = 0; i < dash.length; i++) {
                    dashTemp[i] = dash[i] * width;
                }
                stroke = new BasicStroke(width, cap, join, miterLimit,
                        dashTemp, dashPhase);
            }
        }
        return stroke;
    }
}
