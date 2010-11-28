package com.onemoonscientific.swank;

import tcl.lang.*;

import java.awt.*;
import java.awt.event.*;

import java.util.*;


public class SetBooleanVarEvent extends SetVarEvent {
    boolean value = false;

    public SetBooleanVarEvent(Interp interp, String var1, String var2,
        boolean value) {
        this.interp = interp;
        this.var1 = var1;
        this.var2 = var2;
        this.value = value;
    }

    void setObject() {
        tObj = TclBoolean.newInstance(value);
    }
}
