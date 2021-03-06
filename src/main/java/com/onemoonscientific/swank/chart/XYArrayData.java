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


/**
 *
 * @author brucejohnson
 */
public class XYArrayData extends XYData   {
    static int id=0;
    String name = "";
    DatasetGroup dGroup = new DatasetGroup();
    /**
     *
     */
    public XYArrayData() {
         name  = "xyData"+id;
         datasetMap.put(name,this);
         id++;
    }

    public XYArrayData(String name) {
        this.name = name;
        datasetMap.put(name,this);
    }
    public static void remove(String name) {
          datasetMap.remove(name);
    }
    public void remove() {
          datasetMap.remove(name);
    }
    public static XYData get(String name) {
        return (XYArrayData) datasetMap.get(name);
    }
    public String getName() {
         return name;
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
    @Override
    public int getItemCount(int series) {
        int count = 0;
        if ((series >= 0) && (series < ySeries.size())) {
            ArrayList<Double> yData = (ArrayList<Double>) ySeries.get(series);
            if (yData != null) {
                count = yData.size();
            }
        }
        return count;
    }
    /**
     *
     * @return
     */
    @Override
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
    @Override
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
        ArrayList<Double> xData = (ArrayList<Double>) xSeries.get(series);
        if (xData != null)  {
            return xData.get(item);
        } else {
            return 0.0;
        }
    }

    public Number getX(int series, int item) {
        ArrayList<Double> xData = (ArrayList<Double>) xSeries.get(series);
        if (xData != null)  {
            return new Double(xData.get(item));
        } else {
            return null;
        }
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
        ArrayList<Double> yData = (ArrayList<Double>) ySeries.get(series);
        if (yData != null)  {
            return yData.get(item);
        } else {
            return 0.0;
        }
    }

    /**
     *
     * @param series
     * @param item
     * @return
     */
    public Number getY(int series, int item) {
        ArrayList<Double> yData = (ArrayList<Double>) ySeries.get(series);
        if (yData != null)  {
            return new Double(yData.get(item));
        } else {
            return null;
        }
    }
}
