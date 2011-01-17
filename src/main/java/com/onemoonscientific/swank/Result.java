/*
 * Result.java
 *
 * Created on October 19, 2005, 11:36 AM
 */
package com.onemoonscientific.swank;

import tcl.lang.*;

/**
 *
 * @author brucejohnson
 */
public class Result {

    int i = 0;
    float f = 0.0f;
    double d = 0.0;
    String s = "";
    /**
     *
     */
    public Object o = null;
    String msg = "";
    boolean b = false;
    private boolean hasError = false;

    /** Creates a new instance of Result */
    public Result() {
    }

    /**
     *
     * @param value
     */
    public void setValue(boolean value) {
        b = value;
    }

    /**
     *
     * @return
     */
    public boolean hasError() {
        return hasError;
    }

    /**
     *
     * @return
     */
    public int getInt() {
        return i;
    }
    /**
     *
     * @return
     */
    public boolean getBoolean() {
        return b;
    }

    /**
     *
     * @return
     */
    public float getFloat() {
        return f;
    }

    /**
     *
     * @return
     */
    public double getDouble() {
        return d;
    }

    /**
     *
     * @return
     */
    public String getString() {
        return s;
    }

    /**
     *
     * @return
     */
    public Object getObject() {
        return o;
    }

    /**
     *
     * @return
     */
    public String getErrorMsg() {
        return msg;
    }

    /**
     *
     * @param s
     */
    public void setError(String s) {
        hasError = true;
        msg = s;
    }

    /**
     *
     * @param interp
     * @throws TclException
     */
    public void checkError(Interp interp) throws TclException {
        if (hasError) {
            throw new TclException(interp, msg);
        }
    }
}
