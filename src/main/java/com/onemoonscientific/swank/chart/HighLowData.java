/*
 *
 *
 * Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file \"LICENSE\" for information on usage and redistribution
 * of this file.
 * IN NO EVENT SHALL THE AUTHORS OR DISTRIBUTORS BE LIABLE TO
 * ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR
 * CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OF THIS
 * SOFTWARE, ITS DOCUMENTATION, OR ANY DERIVATIVES THEREOF,
 * EVEN IF THE AUTHORS HAVE BEEN ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * THE AUTHORS AND DISTRIBUTORS SPECIFICALLY DISCLAIM ANY
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, AND NON-INFRINGEMENT.  THIS SOFTWARE
 * IS PROVIDED ON AN "AS IS" BASIS, AND THE AUTHORS AND
 * DISTRIBUTORS HAVE NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 *
 */

/**
 *
 * @author  JOHNBRUC
 * @version
 */
package com.onemoonscientific.swank.chart;

import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.OHLCDataset;

import tcl.lang.*;

import java.util.*;


public class HighLowData implements OHLCDataset {
    static HashMap datasetMap = new HashMap();
    static int id=0;
    String name = "";
    DatasetGroup dGroup = new DatasetGroup();
    ArrayList<Double> xValues = null;
    ArrayList<Double> lowValues = null;
    ArrayList<Double> yValues = null;
    ArrayList<Double> highValues = null;

    public HighLowData() {
         name  = "xyData"+id;
         datasetMap.put(name,this);
         id++;
    }

    /**
     *
     * @param name
     */
    public HighLowData(String name) {
        this.name = name;
        datasetMap.put(name,this);
    }
    public static void remove(String name) {
          datasetMap.remove(name);
    }
    void remove() {
          datasetMap.remove(name);
    }
    public static HighLowData get(String name) {
        return (HighLowData) datasetMap.get(name);
    }
    public String getName() {
         return name;
    }

    public void setXValues(ArrayList<Double> xValues) {
        this.xValues = xValues;
    }

    public void setYValues(ArrayList<Double> yValues) {
        this.yValues = yValues;
    }

    public void setLowValues(ArrayList<Double> lowValues) {
        this.lowValues = lowValues;
    }

    /**
     *
     * @param highValues
     */
    public void setHighValues(ArrayList<Double> highValues) {
        this.highValues = highValues;
    }
    
    /**
      * Registers an object for notification of changes to the dataset.
      *
      * @param listener  the object to register.
      */
    public void addChangeListener(DatasetChangeListener listener) {
    }

    /**
     * Deregisters an object for notification of changes to the dataset.
     *
     * @param listener  the object to deregister.
     */
    public void removeChangeListener(DatasetChangeListener listener) {
    }

    /**
     * Returns the dataset group.
     *
     * @return the dataset group.
     */
    public DatasetGroup getGroup() {
        return dGroup;
    }

    /**
     * Sets the dataset group.
     *
     * @param group  the dataset group.
     */
    public void setGroup(DatasetGroup group) {
        dGroup = group;
    }

    /**
     * Returns the number of series in the dataset.
     *
     * @return the series count.
     */
    public int getSeriesCount() {
        return 1;
    }

    /**
     * Returns the name of a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return the name of the series.
     */
    public String getSeriesName(int series) {
            return "TestSeries";
    }

    /**
     * Returns the order of the domain (or X) values returned by the dataset.
     *
     * @return The order (never <code>null</code>).
     */
    public DomainOrder getDomainOrder() {
        return DomainOrder.NONE;
    }

    /**
     * Returns the number of items in a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The item count.
     */
    /**
     * Returns the number of items in a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return the number of items within the series.
     */
    public int getItemCount(int series) {
        if (yValues != null) {
            return yValues.size();
        } else {
            return 0;
        }
    }

    /**
      * Returns the key for a series.
      *
      * @param series  the series (zero-based index).
      *
      * @return The key for the series.
      */
    public Comparable getSeriesKey(int series) {
        return "";
    }

    /**
     * Returns the index of the named series, or -1.
     *
     * @param seriesKey  the series key.
     *
     * @return The index.
     */
   public int indexOf(Comparable seriesKey) {
        return 0;
    }

    /**
     * Returns the x-value for an item within a series.
     * <P>
     * The implementation is responsible for ensuring that the x-values are
     * presented in ascending order.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the x-value.
     */
    public double getXValue(int series, int item) {
            double x = 0.0;
            if (xValues == null) {
                  x = item;
            } else {
                 x = xValues.get(item);
            }
           return x;
    }

    /**
     *
     * @param series
     * @param item
     * @return
     */
    public Number getX(int series, int item) {
            double x = 0.0;
            if (xValues == null) {
                  x = item;
            } else {
                 x = xValues.get(item);
            }
            return new Double(x);
    }

    /**
     * Returns the y-value for an item within a series.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the y-value.
     */
    public double getYValue(int series, int item) {
        if (yValues != null)  {
            return yValues.get(item);
        } else {
            return 0.0;
        }
    }

    public Number getY(int series, int item) {
        if (yValues != null)  {
            return new Double(yValues.get(item));
        } else {
            return null;
        }
    }
    public Number getHigh(int series, int item) {
        if (highValues != null)  {
            return new Double(highValues.get(item));
        } else {
            return null;
        }
    }
    public double getHighValue(int series, int item) {
        if (highValues != null)  { 
            return highValues.get(item);
        } else {
            return 0;
        }
    }

    public Number getLow(int series, int item) {
        if (lowValues != null)  {
            return new Double(lowValues.get(item));
        } else {
            return null;
        }
    }
    public double getLowValue(int series, int item) {
        if (lowValues != null)  {
            return lowValues.get(item);
        } else {
            return 0;
        }
    }
    /**
     *
     * @param series
     * @param item
     * @return
     */
    public double getVolumeValue(int series, int item) {
          return 0.0;
    }
    /**
     *
     * @param series
     * @param item
     * @return
     */
    public Number getVolume(int series, int item) {
            return new Double(0.0);
    }
    public double getCloseValue(int series, int item) {
        if (highValues != null)  {
            return highValues.get(item);
        } else {
            return 0.0;
        }
    }
    public Number getClose(int series, int item) {
        if (highValues != null)  {
            return new Double(highValues.get(item));
        } else {
            return null;
        }
    }
    public double getOpenValue(int series, int item) {
        if (lowValues != null)  {
            return lowValues.get(item);
        } else {
            return 0;
        }
    }
    public Number getOpen(int series, int item) {
        if (lowValues != null)  {
            return new Double(lowValues.get(item));
        } else {
            return null;
        }
    }
}
