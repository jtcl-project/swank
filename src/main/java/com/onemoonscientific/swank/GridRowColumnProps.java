/*
 * GridRowColumnProps.java
 *
 * Created on February 15, 2004, 1:12 PM
 */
package com.onemoonscientific.swank;

/**
 *
 * @author  bruce
 */
public class GridRowColumnProps implements Cloneable {

    int minSize = 0;
    int weight = 0;
    int pad = 0;
    String uniform = "";

    /** Creates a new instance of GridRowColumnProps */
    public GridRowColumnProps() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        GridRowColumnProps newObject = new GridRowColumnProps();
        newObject.minSize = minSize;
        newObject.weight = weight;
        newObject.pad = pad;
        newObject.uniform = uniform;

        return newObject;
    }
}
