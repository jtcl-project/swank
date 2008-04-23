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


public class XYTableGroupData extends XYData {
    int xColumn = -1;
    int gColumn = -1;
    int yColumn = -1;
    SwkTableModel tableModel = null;
    Map seriesMap = new TreeMap();
    List seriesNames = new ArrayList();
    List seriesLists = new ArrayList();
    public XYTableGroupData() {
         name  = "xyData"+id;
         datasetMap.put(name,this);
         id++;
    }

    public XYTableGroupData(String name) {
        datasetMap.put(name,this);
    }
    public void setTableModel(SwkTableModel model) {
        if (EventQueue.isDispatchThread()) {
             setTableModelOnEventThread(model);
         } else {
           (new SetModel()).exec(model);
         }
    }
    class SetModel extends GetValueOnEventThread {
        SwkTableModel model;
        void  exec(SwkTableModel model) {
            this.model = model;
            execOnThread();
        }
        public void run() {
             setTableModelOnEventThread(model);
        }
    }


    public void setTableModelOnEventThread(SwkTableModel model) {
         tableModel = model;
         xColumn = -1;
         gColumn = -1;
         yColumn = -1;
    } 

    public void setXColumn(int column) {
          xColumn = column;
    }

    public void setGColumn(int column) {
          gColumn = column;
    }

    public void setYColumn(int column) {
          yColumn = column;
    }
    public void getSeries() { 
          seriesMap.clear();
          seriesLists.clear();
          seriesNames.clear();
          int nRows = tableModel.getNRows();
          int nSeries = 0;
          for (int i=0;i<nRows;i++) {
              Object gObject = tableModel.getValueAt(i, gColumn);
              Integer seriesIndex = (Integer) seriesMap.get(gObject);
              ArrayList arrayList = null;
              if (seriesIndex == null) {
                    seriesNames.add(gObject);
                    seriesMap.put(gObject,new Integer(nSeries));
                    arrayList = new ArrayList();
                    seriesLists.add(arrayList);
                    nSeries++;
              } else {
                   int index = seriesIndex.intValue();
                   arrayList = (ArrayList) seriesLists.get(index);
              }
              arrayList.add(new Integer(i));
          }
    }
    public int getTableRow(int series, int index) {
            ArrayList arrayList = (ArrayList) seriesLists.get(series);
            Integer rowInt = (Integer) arrayList.get(index);
            return rowInt.intValue();
    }
    /**
     * Returns the number of series in the dataset.
     *
     * @return the series count.
     */
    public int getSeriesCount() {
        return seriesLists.size();
    }

    /**
     * Returns the name of a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return the name of the series.
     */
    public String getSeriesName(int series) {
          // FIXME check if table column exists
        if ((series >= 0) && (series < seriesLists.size())) {
            Object gObject = seriesNames.get(series);
            return gObject.toString();
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
    public int getItemCount(int series) {
        int nRows = 0;
        if (tableModel != null) {
            ArrayList arrayList = (ArrayList) seriesLists.get(series);
            nRows = arrayList.size();
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
        return -1;
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
        int rowIndex = getTableRow(series,item);
        double x = 0.0;
        if ((xColumn < 0) || (xColumn >= tableModel.getNCols()))  {
              x = rowIndex;
        } else {
            Object xObject = tableModel.getValueAt(rowIndex, xColumn);
            if (xObject instanceof Number) {
                 x = ((Number) xObject).doubleValue();
            }
        }
        return x;
    }

    public Number getX(int series, int item) {
        Number x = null;
        int rowIndex = getTableRow(series,item);
        if ((xColumn < 0) || (xColumn >= tableModel.getNCols()))  {
              x = new Integer(rowIndex);
        } else {
            Object xObject = tableModel.getValueAt(rowIndex, xColumn);
            if (xObject instanceof Number) {
                 x = (Number) xObject;
            }
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
        double y = 0.0;
        int rowIndex = getTableRow(series,item);
        if ((yColumn < 0) || (yColumn >= tableModel.getNCols()))  {
              y = rowIndex;
        } else {
            Object yObject = tableModel.getValueAt(rowIndex, yColumn);
            if (yObject instanceof Number) {
                 y = ((Number) yObject).doubleValue();
            }
        }
        return y;
    }

    public Number getY(int series, int item) {
        Number y = null;
        if ((yColumn >= 0) && (yColumn < tableModel.getNCols()))  {
            Object yObject = tableModel.getValueAt(item, yColumn);
            if (yObject instanceof Number) {
                 y = (Number) yObject;
            }
        }
        return y;

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
