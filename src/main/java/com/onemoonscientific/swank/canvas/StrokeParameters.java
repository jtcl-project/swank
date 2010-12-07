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
    private int cap = BasicStroke.CAP_SQUARE;
    private int join = BasicStroke.JOIN_MITER;
    private float miterLimit = 10.0f;
    private String dashString = "";
    private float[] dash = {};
    private float dashPhase = 0.0f;
    private boolean dashIntPattern = false;

    static StrokeParameters getDefault() {
        return defaultPar;
    }

    BasicStroke getStroke(float width) {
        BasicStroke stroke = null;
        if (getDashString().equals("")) {
            stroke = new BasicStroke(width, getCap(), getJoin(), getMiterLimit());
        } else {
            if (isDashIntPattern() || (width == 1.0f) || (width == 0.0f)) {
                stroke = new BasicStroke(width, getCap(), getJoin(), getMiterLimit(), getDash(), getDashPhase());
            } else {
                float[] dashTemp = new float[getDash().length];
                for (int i = 0; i < getDash().length; i++) {
                    dashTemp[i] = getDash()[i] * width;
                }
                stroke = new BasicStroke(width, getCap(), getJoin(), getMiterLimit(), dashTemp, getDashPhase());
            }
        }
        return stroke;
    }

    /**
     * @return the cap
     */
    public int getCap() {
        return cap;
    }

    /**
     * @param cap the cap to set
     */
    public static StrokeParameters setCap(StrokeParameters strokePar, int newValue) {
        if (newValue != strokePar.cap) {
            if (strokePar == defaultPar) {
                strokePar = new StrokeParameters();
            }
            strokePar.cap = newValue;

        }
        return strokePar;
    }

    /**
     * @return the join
     */
    public int getJoin() {
        return join;
    }

    public static StrokeParameters setJoin(StrokeParameters strokePar, int newValue) {
        if (newValue != strokePar.join) {
            if (strokePar == defaultPar) {
                strokePar = new StrokeParameters();
            }
            strokePar.join = newValue;

        }
        return strokePar;
    }

    /**
     * @return the miterLimit
     */
    public float getMiterLimit() {
        return miterLimit;
    }

    /**
     * @param miterLimit the miterLimit to set
     */
    public static StrokeParameters setMiterLimit(StrokeParameters strokePar, float newValue) {
        if (newValue != strokePar.miterLimit) {
            if (strokePar == defaultPar) {
                strokePar = new StrokeParameters();
            }
            strokePar.miterLimit = newValue;

        }
        return strokePar;
    }

    /**
     * @return the dashString
     */
    public String getDashString() {
        return dashString;
    }

    /**
     * @param dashString the dashString to set
     */
    public static StrokeParameters setDashString(StrokeParameters strokePar, String newValue) {
        if (!newValue.equals(strokePar.dashString)) {
            if (strokePar == defaultPar) {
                strokePar = new StrokeParameters();
            }
            strokePar.dashString = newValue;

        }
        return strokePar;
    }

    /**
     * @return the dash
     */
    public float[] getDash() {
        return dash;
    }

    /**
     * @param dash the dash to set
     */
    public static StrokeParameters setDash(StrokeParameters strokePar, float[] newValue) {
        boolean change = false;
        if (newValue.length != strokePar.dash.length) {
            change = true;
        } else {
            for (int i = 0; i < newValue.length; i++) {
                if (newValue[i] != strokePar.dash[i]) {
                    change = true;
                    break;
                }
            }
        }
        if (change) {
            if (strokePar == defaultPar) {
                strokePar = new StrokeParameters();
            }
            strokePar.dash = newValue;

        }
        return strokePar;
    }

    /**
     * @return the dashPhase
     */
    public float getDashPhase() {
        return dashPhase;
    }

    /**
     * @param dashPhase the dashPhase to set
     */
    public static StrokeParameters setDashPhase(StrokeParameters strokePar, float newValue) {
        if (newValue != strokePar.dashPhase) {
            if (strokePar == defaultPar) {
                strokePar = new StrokeParameters();
            }
            strokePar.dashPhase = newValue;

        }
        return strokePar;
    }

    /**
     * @return the dashIntPattern
     */
    public boolean isDashIntPattern() {
        return dashIntPattern;
    }

    /**
     * @param dashIntPattern the dashIntPattern to set
     */
    public static StrokeParameters setDashIntPattern(StrokeParameters strokePar, boolean newValue) {
        if (newValue != strokePar.dashIntPattern) {
            if (strokePar == defaultPar) {
                strokePar = new StrokeParameters();
            }
            strokePar.dashIntPattern = newValue;

        }
        return strokePar;
    }
}
