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
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.TableXYDataset;

import tcl.lang.*;

import java.util.*;


public abstract class XYData implements XYDataset, IntervalXYDataset {
    static HashMap datasetMap = new HashMap();
    static int id=0;
    String name = "";
    DatasetGroup dGroup = new DatasetGroup();
    ArrayList xSeries = new ArrayList();
    ArrayList ySeries = new ArrayList();
    ArrayList ySeriesNames = new ArrayList();
    double deltaX = 0.9;
    double deltaY = 0.15;
//    double deltaX = 0.0;
 //   double deltaY = 0.0;
    public XYData() {
         name  = "xyData"+id;
         datasetMap.put(name,this);
         id++;
    }

    public XYData(String name) {
        this.name=name;
        datasetMap.put(name,this);
    }
    public static void remove(final String name) {
          datasetMap.remove(name);
    }
    void remove() {
          datasetMap.remove(name);
    }
    public static XYData get(String name) {
        return (XYData) datasetMap.get(name);
    }
    public String getName() {
         return name;
    }
    /**
     *
     * @param value
     */
    public void setDeltaX(double value) {
	    deltaX = value;
    }
    /**
     *
     * @param xValues
     * @param series
     */
    public void setXValues(ArrayList<Double> xValues,int series) {
        for (int i=xSeries.size();i<=series;i++) {
             xSeries.add(null);
        }
        xSeries.set(series,xValues);
    }


    public void setXValues(ArrayList<Double> xValues) {
        xSeries.clear();
        xSeries.add(xValues);
    }

    public void setYValues(ArrayList<Double> yValues,int series,String seriesName) {
        for (int i=ySeries.size();i<=series;i++) {
             ySeries.add(null);
             ySeriesNames.add("");
        }
        ySeries.set(series,yValues);
        ySeriesNames.set(series,seriesName);
    }

    public void setYValues(ArrayList<Double> yValues) {
        ySeries.clear();
        ySeries.add(yValues);
        ySeriesNames.clear();
        ySeriesNames.add("");
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
        return ySeries.size();
    }

    /**
     * Returns the name of a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return the name of the series.
     */
    public String getSeriesName(int series) {
        return (String) ySeriesNames.get(series);
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
        ArrayList<Double> yData = (ArrayList<Double>) ySeries.get(series);
        if (yData != null) {
            return yData.size();
        } else {
            return 0;
        }
    }
    public int getItemCount() {
        int count = 0;
        if (ySeries.size() > 0) {
            ArrayList<Double> yData = (ArrayList<Double>) ySeries.get(0);
            if (yData != null) {
                count = yData.size();
            }
        }
        return count;
    }
    /**
      * Returns the key for a series.
      *
      * @param series  the series (zero-based index).
      *
      * @return The key for the series.
      */
    public Comparable getSeriesKey(int series) {
        return getSeriesName(series);
    }

    /**
     * Returns the index of the named series, or -1.
     *
     * @param seriesKey  the series key.
     *
     * @return The index.
     */
    public int indexOf(Comparable seriesKey) {
        return ySeriesNames.indexOf(seriesKey);
    }

    public double getEndYValue(int series, int item) {
        double yValue =  getYValue(series,item);
        return yValue+deltaY/2;
    }

    public Number getEndY(int series, int item) {
        double yValue = getEndYValue(series,item);
        return new Double(yValue);
    }
    public double getStartYValue(int series, int item) {
        double yValue =  getYValue(series,item);
        return yValue-deltaY/2;
    }

    public Number getStartY(int series, int item) {
        double yValue = getStartYValue(series,item);
        return new Double(yValue);
   }

    public double getEndXValue(int series, int item) {
        double xValue = getXValue(series,item);
        return xValue+deltaX/2;
    }

    public Number getEndX(int series, int item) {
        double xValue = getEndXValue(series,item);
        return new Double(xValue);
    }
    public double getStartXValue(int series, int item) {
        double xValue = getXValue(series,item);
        return xValue-deltaX/2.0;
    }

    public Number getStartX(int series, int item) {
        double xValue = getStartXValue(series,item);
        return new Double(xValue);
   }


}
