/*
 * SwkGridBagLayout.java
 *
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 * RCS: @(#) $Id: SwkGridBagLayout.java,v 1.2 2004/08/20 19:30:52 bruce_johnson Exp $
 *
 */
/**
 * SwkGridBagLayout is used to lay out widget components.
 *
 *
 */
package com.onemoonscientific.swank;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Hashtable;
import java.util.Iterator;


/**
 *
 * @author brucejohnson
 */
public class SwkGridBagLayout extends GridBagLayout {

    boolean propagate = true;
    Hashtable columnTable = new Hashtable();
    Hashtable rowTable = new Hashtable();
    GridRowColumnProps defaultColumnProps = new GridRowColumnProps();
    GridRowColumnProps defaultRowProps = new GridRowColumnProps();
    int lastRow = 0;

    /**
     * Constructs a new GridBagLayout Layout.
     */
    public SwkGridBagLayout() {
        propagate = true;
    }

    int getRCMax(boolean columnMode) {
        Iterator iter = null;

        if (columnMode) {
            iter = columnTable.keySet().iterator();
        } else {
            iter = rowTable.keySet().iterator();
        }

        int x = 0;

        while (iter.hasNext()) {
            String key = (String) iter.next();
            GridRowColumnProps rcProps = null;

            if (columnMode) {
                rcProps = (GridRowColumnProps) columnTable.get(key);
            } else {
                rcProps = (GridRowColumnProps) rowTable.get(key);
            }

            if ((rcProps.weight > 0) || (rcProps.minSize > 0)
                    || (rcProps.pad > 0)) {
                int iKey = Integer.parseInt(key);

                if (iKey > x) {
                    x = iKey;
                }
            }
        }

        return x;
    }

    GridRowColumnProps getProps(boolean columnMode, String key, int index) {
        if (columnMode) {
            return getColumnProps(key, index);
        } else {
            return getRowProps(key, index);
        }
    }

    GridRowColumnProps getColumnProps(String key, int column) {
        GridRowColumnProps columnProps = (GridRowColumnProps) columnTable.get(key);

        if (columnProps == null) {
            columnProps = defaultColumnProps;
        }

        columnProps.minSize = getColumnMinSize(column);

        return columnProps;
    }

    GridRowColumnProps getRowProps(String key, int row) {
        GridRowColumnProps rowProps = (GridRowColumnProps) rowTable.get(key);

        if (rowProps == null) {
            rowProps = defaultRowProps;
        }

        rowProps.minSize = getRowMinSize(row);

        return rowProps;
    }

    void putColumnProps(String key, GridRowColumnProps rcProps) {
        columnTable.put(key, rcProps);
    }

    void putRowProps(String key, GridRowColumnProps rcProps) {
        rowTable.put(key, rcProps);
    }

    void setMinSize(boolean columnMode, int index, int minSize) {
        if (columnMode) {
            setColumnMinSize(index, minSize);
        } else {
            setRowMinSize(index, minSize);
        }
    }

    void setColumnMinSize(int column, int minSize) {
        if ((columnWidths == null)) {
            columnWidths = new int[column + 2];
        } else if (columnWidths.length <= column) {
            int[] newWidths = new int[column + 2];

            for (int i = 0; i < columnWidths.length; i++) {
                newWidths[i] = columnWidths[i];
            }

            columnWidths = newWidths;
        }

        columnWidths[column] = minSize;
    }

    void setRowMinSize(int column, int minSize) {
        if ((rowHeights == null)) {
            rowHeights = new int[column + 2];
        } else if (rowHeights.length <= column) {
            int[] newHeights = new int[column + 2];

            for (int i = 0; i < rowHeights.length; i++) {
                newHeights[i] = rowHeights[i];
            }

            rowHeights = newHeights;
        }

        rowHeights[column] = minSize;
    }

    int getMinSize(boolean columnMode, int index) {
        if (columnMode) {
            return getColumnMinSize(index);
        } else {
            return getRowMinSize(index);
        }
    }

    int getColumnMinSize(int column) {
        if ((columnWidths == null)) {
            return 0;
        } else if (columnWidths.length <= column) {
            return 0;
        } else {
            return columnWidths[column];
        }
    }

    int getRowMinSize(int column) {
        if ((rowHeights == null)) {
            return 0;
        } else if (rowHeights.length <= column) {
            return 0;
        } else {
            return rowHeights[column];
        }
    }

    void setWeight(boolean columnMode, int index, int weight) {
        if (columnMode) {
            setColumnWeight(index, weight);
        } else {
            setRowWeight(index, weight);
        }
    }

    void setColumnWeight(int column, int weight) {
        if ((columnWeights == null)) {
            columnWeights = new double[column + 2];
        } else if (columnWeights.length <= column) {
            double[] newWeights = new double[column + 2];

            for (int i = 0; i < columnWeights.length; i++) {
                newWeights[i] = columnWeights[i];
            }

            columnWeights = newWeights;
        }

        columnWeights[column] = weight;
    }

    void setRowWeight(int row, int weight) {
        if ((rowWeights == null)) {
            rowWeights = new double[row + 2];
        } else if (rowWeights.length <= row) {
            double[] newWeights = new double[row + 2];

            for (int i = 0; i < rowWeights.length; i++) {
                newWeights[i] = rowWeights[i];
            }

            rowWeights = newWeights;
        }

        rowWeights[row] = weight;
    }

    @Override
    public Dimension maximumLayoutSize(Container target) {
        return super.preferredLayoutSize(target);
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
        return super.getLayoutAlignmentX(target);
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        return super.getLayoutAlignmentY(target);
    }

    @Override
    public void invalidateLayout(Container target) {
        super.invalidateLayout(target);
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        super.addLayoutComponent(comp, constraints);
    }

    /**
     *
     * @param comp
     * @param constraints
     */
    public void addLayoutComponent(Component comp, String constraints) {
        super.addLayoutComponent(comp, constraints);
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        GridBagConstraints gconstr = getConstraints(comp);

        if (gconstr != null) {
        }

        super.removeLayoutComponent(comp);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return (super.preferredLayoutSize(target));
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        int i;
        Dimension cdim = super.minimumLayoutSize(target);

        return cdim;
    }

    @Override
    public void layoutContainer(Container target) {
        super.layoutContainer(target);
    }

    @Override
    public String toString() {
        return getClass().getName();
    }
}
