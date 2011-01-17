package com.onemoonscientific.swank;

import tcl.lang.*;

/**
 *
 * @author brucejohnson
 */
public class SetStringVarEvent extends SetVarEvent {

    String value = null;

    /**
     *
     * @param interp
     * @param tlComp
     * @param var1
     * @param var2
     * @param value
     */
    public SetStringVarEvent(Interp interp, TraceLock tlComp, String var1,
            String var2, String value) {
        this.interp = interp;
        this.var1 = var1;
        this.var2 = var2;
        this.value = value;
        comp = tlComp;
    }

    /**
     *
     * @param interp
     * @param var1
     * @param var2
     * @param value
     */
    public SetStringVarEvent(Interp interp, String var1, String var2,
            String value) {
        this.interp = interp;
        this.var1 = var1;
        this.var2 = var2;
        this.value = value;
        this.onlyIfVarNonExistant = false;
    }

    /**
     *
     * @param interp
     * @param var1
     * @param var2
     * @param value
     * @param onlyIfVarNonExistant
     */
    public SetStringVarEvent(Interp interp, String var1, String var2,
            String value, boolean onlyIfVarNonExistant) {
        this.interp = interp;
        this.var1 = var1;
        this.var2 = var2;
        this.value = value;
        this.onlyIfVarNonExistant = onlyIfVarNonExistant;
    }

    @Override
    void setObject() {
        if (value != null) {
            tObj = TclString.newInstance(value);
        }
    }
}
