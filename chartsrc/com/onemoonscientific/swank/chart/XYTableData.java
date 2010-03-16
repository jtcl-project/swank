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
import java.awt.EventQueue;
import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.TableXYDataset;

import tcl.lang.*;

import java.util.*;


public class XYTableData extends XYData  implements TableXYDataset {
    int xColumn = -1;
    int[] yColumns = new int[0];;
    SwkTableModel tableModel = null;
    public XYTableData() {
         name  = "xyData"+id;
         datasetMap.put(name,this);
         id++;
    }

    public XYTableData(String name) {
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
             yColumns = new int[0];
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

    public void setXColumn(int column) {
          xColumn = column;
    }

    public void setYColumns(Interp interp, TclObject columnArgs) throws TclException {
          TclObject[] columnObjects = TclList.getElements(interp,columnArgs);
          yColumns = new int[columnObjects.length];
          for (int i=0;i<columnObjects.length;i++) {
                int column = TclInteger.get(interp,columnObjects[i]);
                yColumns[i] = column;
          }
    }

    public void setYColumns(int[] columns) {
          yColumns = columns;
    }
    

    /**
     * Returns the number of series in the dataset.
     *
     * @return the series count.
     */
    public int getSeriesCount() {
        return yColumns.length;
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
        if ((series >= 0) && (series < yColumns.length)) {
            int yColumn = yColumns[series]; 
        return tableModel.getColumnName(yColumn);
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
        return -1;
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
            Number xNum = getNumber(xObject);
            if (xObject instanceof Number) {
                 x = ((Number) xObject).doubleValue();
            }
        }
        return x;
    }

    public Number getX(int series, int item) {
        Number x = null;
        if ((xColumn < 0) || (xColumn >= tableModel.getNCols()))  {
              x = new Integer(item);
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
        double y = 0.0;
        int yColumn = yColumns[series]; 
        if ((yColumn < 0) || (yColumn >= tableModel.getNCols()))  {
              y = item;
        } else {
            Object yObject = tableModel.getValueAt(item, yColumn);
            Number yNum = getNumber(yObject);
            if (yObject instanceof Number) {
                 y = ((Number) yObject).doubleValue();
            }
        }
        return y;
    }

    public Number getY(int series, int item) {
        Number y = null;
        int yColumn = yColumns[series]; 
        if ((yColumn >= 0) && (yColumn < tableModel.getNCols()))  {
            Object yObject = tableModel.getValueAt(item, yColumn);
            y = getNumber(yObject);
        }
        return y;

    }


}
