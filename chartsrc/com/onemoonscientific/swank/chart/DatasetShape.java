/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.onemoonscientific.swank.chart;

import java.awt.Color;
import tcl.lang.Interp;
import tcl.lang.TclException;
import tcl.lang.TclObject;

/**
 *
 * @author brucejohnson
 */
public interface DatasetShape {

	public TclObject getDatasets(Interp interp) throws TclException;

	public void updateDatasets(String[] datasetNames);
 	public TclObject getColors(Interp interp) throws TclException;
       public void updateColors(Color[] colors);

}
