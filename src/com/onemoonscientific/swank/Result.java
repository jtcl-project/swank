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
    public Object o = null;
    String msg = "";
    boolean b = false;
    private boolean hasError = false;

    /** Creates a new instance of Result */
    public Result() {
    }

    public void setValue(boolean value) {
        b = value;
    }

    public boolean hasError() {
        return hasError;
    }

    public int getInt() {
        return i;
    }

    public float getFloat() {
        return f;
    }

    public double getDouble() {
        return d;
    }

    public String getString() {
        return s;
    }

    public Object getObject() {
        return o;
    }

    public String getErrorMsg() {
        return msg;
    }

    public void setError(String s) {
        hasError = true;
        msg = s;
    }

    public void checkError(Interp interp) throws TclException {
        if (hasError) {
            throw new TclException(interp, msg);
        }
    }
}
