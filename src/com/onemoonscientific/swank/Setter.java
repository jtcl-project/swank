package com.onemoonscientific.swank;

import javax.swing.SwingUtilities;


public class Setter extends UpdateOnEventThread {
    int iOpt = 0;
    public int iValue = 0;
    public boolean bValue = false;
    public float fValue = 0;
    public double dValue = 0;
    public Object oValue = null;
    public String sValue = null;
    public SwkWidget widget = null;
    boolean wait = false;

    public Setter(SwkWidget widget, int iOpt) {
        this.widget = widget;
        this.iOpt = iOpt;
    }

    public Setter(SwkWidget widget, int iOpt, boolean wait) {
        this.widget = widget;
        this.iOpt = iOpt;
        this.wait = wait;
    }

    public void exec(final int value) {
        this.iValue = value;
        execOnThread();

        if (wait) {
            doWait();
        }
    }

    public void exec(final float value) {
        this.fValue = value;
        execOnThread();

        if (wait) {
            doWait();
        }
    }

    public void exec(final double value) {
        this.dValue = value;
        execOnThread();

        if (wait) {
            doWait();
        }
    }

    public void exec(final boolean value) {
        this.bValue = value;
        execOnThread();

        if (wait) {
            doWait();
        }
    }

    public void exec(final String value) {
        this.sValue = value;
        execOnThread();

        if (wait) {
            doWait();
        }
    }

    public void exec(final Object value) {
        this.oValue = value;
        execOnThread();

        if (wait) {
            doWait();
        }
    }

    public void exec(final Object value, final String sValue) {
        this.oValue = value;
        this.sValue = sValue;
        execOnThread();

        if (wait) {
            doWait();
        }
    }

    // XXX what if widget has been destroyed before running?
    public void run() {
        widget.setValues(this, iOpt);
    }

    void doWait() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                    }
                });
        } catch (Exception e) {
        }
    }
}
