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
import com.onemoonscientific.swank.SwkTableModel;
import com.onemoonscientific.swank.GetValueOnEventThread;
import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.TableXYDataset;

import tcl.lang.*;

import java.util.*;
import java.awt.EventQueue;


/**
 *
 * @author brucejohnson
 */
public class XYTableStatsData extends XYData {
    int xColumn = -1;
    int meanColumn = -1;
    int sDevColumn = -1;
    SwkTableModel tableModel = null;
    public XYTableStatsData() {
         name  = "xyData"+id;
         datasetMap.put(name,this);
         id++;
    }

    public XYTableStatsData(String name) {
        this.name = name;
        datasetMap.put(name,this);
    }
    public void setTableModel(SwkTableModel model) {
        if (EventQueue.isDispatchThread()) {
             setTableModelOnEventThread(model);
         } else {
           (new SetModel()).exec(model);
         }
    }
    public void setTableModelOnEventThread(SwkTableModel model) {
         tableModel = model;
         xColumn = -1;
         meanColumn = -1;
    }
    private class SetModel extends GetValueOnEventThread {
        SwkTableModel model;
        void  exec(SwkTableModel model) {
            this.model = model;
            execOnThread();
        }
        public void run() {
             setTableModelOnEventThread(model);
        }
    }

    /**
     *
     * @param column
     */
    public void setXColumn(int column) {
          xColumn = column;
    }

    public void setMeanColumn(int column) {
          meanColumn = column;
    }
    
    public void setSDevColumn(int column) {
          sDevColumn = column;
    }

    /**
     * Returns the number of series in the dataset.
     *
     * @return the series count.
     */
    public int getSeriesCount() {
       if (meanColumn != -1) {
            return 1;
       } else {
            return 0;
       }
    }

    /**
     * Returns the name of a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return the name of the series.
     */
    public String getSeriesName(int series) {
       if (meanColumn != -1) {
            return tableModel.getColumnName(meanColumn);
       } else {
            return null;
       }
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
        int nRows = 0;
        if (tableModel != null) {
            nRows = tableModel.getNRows();
        }
        return nRows;
    }
    public int getItemCount() {
        int nRows = 0;
        if (tableModel != null) {
            nRows = tableModel.getNRows();
        }
        return nRows;
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
    Number getNumber(Object obj) {
        Number result = null;
        if (obj instanceof Number) {
              result = (Number) obj;
        } else {
             try {
                 result = Double.valueOf(obj.toString());
             } catch (NumberFormatException nfE) {
                 result = null;
             }
        }
        return result;
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
        if ((xColumn < 0) || (xColumn >= tableModel.getNCols()))  {
              x = item;
        } else {
            Object xObject = tableModel.getValueAt(item, xColumn);
            if (xObject instanceof Number) {
                 x = ((Number) xObject).doubleValue();
            } else if (xObject instanceof String) {
                  try {
                      x = Double.parseDouble((String) xObject);
                  } catch (NumberFormatException nfE) {
                  }
            }
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
        Number x = null;
        if ((xColumn < 0) || (xColumn >= tableModel.getNCols()))  {
              x = Integer.valueOf(item);
        } else {
            Object xObject = tableModel.getValueAt(item, xColumn);
            x = getNumber(xObject);
        }
        return x;
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
        return getMeanValue(series,item);
    }

    public Number getY(int series, int item) {
        return getMean(series,item);
    }

    /**
     * Returns the mean-value for an item within a series.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the mean-value.
     */
    public double getMeanValue(int series, int item) {
        double y = 0.0;
        if ((meanColumn < 0) || (meanColumn >= tableModel.getNCols()))  {
              y = item;
        } else {
            Object yObject = tableModel.getValueAt(item, meanColumn);
            if (yObject instanceof Number) {
                 y = ((Number) yObject).doubleValue();
            }
        }
        return y;
    }

    public Number getMean(int series, int item) {
        Number y = null;
        if ((meanColumn >= 0) && (meanColumn < tableModel.getNCols()))  {
            Object yObject = tableModel.getValueAt(item, meanColumn);
            y = getNumber(yObject);
        }
        return y;

    }
    /**
     * Returns the sDev-value for an item within a series.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the sDev-value.
     */
    public double getSDevValue(int series, int item) {
        double y = 0.0;
        if ((sDevColumn < 0) || (sDevColumn >= tableModel.getNCols()))  {
              y = item;
        } else {
            Object yObject = tableModel.getValueAt(item, sDevColumn);
            if (yObject instanceof Number) {
                 y = ((Number) yObject).doubleValue();
            }
        }
        return y;
    }

    public Number getSDev(int series, int item) {
        Number y = null;
        if ((sDevColumn >= 0) && (sDevColumn < tableModel.getNCols()))  {
            Object yObject = tableModel.getValueAt(item, sDevColumn);
            y = getNumber(yObject);
        }
        if (y == null) {
            y = new Double(0.0);
        }
        return y;
    }


}
