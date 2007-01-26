package com.onemoonscientific.swank.print;

import tcl.lang.*;


public class PrintExt extends Extension {
    /*
     * Create all the commands in the Simple package.
     */
    public void init(Interp interp) {
        interp.createCommand("print", new PrintCmd());
    }
}
