package com.onemoonscientific.swank;

import tcl.lang.*;

/**
 *
 * @author brucejohnson
 */
public class SetIntVarEvent extends SetVarEvent {

    int value = 0;

    /**
     *
     * @param interp
     * @param var1
     * @param var2
     * @param value
     */
    public SetIntVarEvent(Interp interp, String var1, String var2, int value) {
        this.interp = interp;
        this.var1 = var1;
        this.var2 = var2;
        this.value = value;
    }

    @Override
    void setObject() {
        tObj = TclInteger.newInstance(value);
    }
}
