package com.onemoonscientific.swank;

import tcl.lang.*;

import java.awt.*;
import java.awt.event.*;

import java.util.*;


public class SetDoubleVarEvent extends SetVarEvent {
    double value = 0;

    public SetDoubleVarEvent(Interp interp, String var1, String var2,
        double value) {
        this.interp = interp;
        this.var1 = var1;
        this.var2 = var2;
        this.value = value;
    }

    void setObject() {
        tObj = TclDouble.newInstance(value);
    }
}
