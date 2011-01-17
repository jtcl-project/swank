package com.onemoonscientific.swank;

import tcl.lang.*;

/**
 *
 * @author brucejohnson
 */
public class SetDoubleVarEvent extends SetVarEvent {

    double value = 0;

    /**
     *
     * @param interp
     * @param var1
     * @param var2
     * @param value
     */
    public SetDoubleVarEvent(Interp interp, String var1, String var2,
            double value) {
        this.interp = interp;
        this.var1 = var1;
        this.var2 = var2;
        this.value = value;
    }

    @Override
    void setObject() {
        tObj = TclDouble.newInstance(value);
    }
}
