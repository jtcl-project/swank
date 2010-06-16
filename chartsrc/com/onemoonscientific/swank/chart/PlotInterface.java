/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.onemoonscientific.swank.chart;

import tcl.lang.Interp;
import tcl.lang.TclException;
import tcl.lang.TclObject;

/**
 *
 * @author brucejohnson
 */
public interface PlotInterface {
    public String getLegendLoc();
    public void setLegendLoc(String loc);
    public boolean getLegendState();
    public void setLegendState(boolean state);
 
}
