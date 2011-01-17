package com.onemoonscientific.swank.print;

import tcl.lang.*;

/**
 * 
 * @author brucejohnson
 */
public class PrintExt extends Extension {
    /*
     * Create all the commands in the Simple package.
     */

    /**
     *
     * @param interp
     */
    public void init(Interp interp) {
        interp.createCommand("print", new PrintCmd());
    }
}
