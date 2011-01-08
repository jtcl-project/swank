package com.onemoonscientific.swank;

import tcl.lang.*;

public class SetBooleanVarEvent extends SetVarEvent {

    boolean value = false;

    public SetBooleanVarEvent(Interp interp, String var1, String var2,
            boolean value) {
        this.interp = interp;
        this.var1 = var1;
        this.var2 = var2;
        this.value = value;
    }

    @Override
    void setObject() {
        tObj = TclBoolean.newInstance(value);
    }
}
