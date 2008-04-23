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
import org.jfree.data.general.*;
import org.jfree.data.category.*;

import tcl.lang.*;

import java.util.*;
import java.awt.EventQueue;


public class XYTableCategoryData extends AbstractDataset
                                    implements CategoryDataset {
   static HashMap datasetMap = new HashMap();
    static int id=0;
    String name = "";
    DatasetGroup dGroup = new DatasetGroup();
    ArrayList ySeries = new ArrayList();
    ArrayList ySeriesNames = new ArrayList();
    double deltaX = 0.9;
    double deltaY = 0.15;

    int xColumn = -1;
    int[] yColumns = new int[0];;
    SwkTableModel tableModel = null;
    public XYTableCategoryData() {
         name  = "xyData"+id;
         datasetMap.put(name,this);
         id++;
    }

    public XYTableCategoryData(String name) {
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


    public int getColumnCount() {
          return yColumns.length;
    }

    public List getColumnKeys() {
          return null;
    }
    public int getColumnIndex(Comparable key) {
          return 0;
    }

    public Comparable getColumnKey(int  column) {
          return null;
    }

    public int getRowCount() {
        int nRows = 0;
        if (tableModel != null) {
            nRows = tableModel.getNRows();
        }
        return nRows;
    }
    public List getRowKeys() {
          return null;
    }
    public int getRowIndex(Comparable key) {
          return 0;
    }

    public Comparable getRowKey(int  column) {
          return null;
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

    /**
     * Returns a value from the table.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The value (possibly <code>null</code>).
     */
    public Number getValue(int row, int column) {
        Number x = null;
        if ((column < 0) || (column >= tableModel.getNCols()))  {
              x = new Integer(row);
        } else {
            Object xObject = tableModel.getValueAt(row, column);
            if (xObject instanceof Number) {
                 x = (Number) xObject;
            }
        }
        return x;
    }
   /**
     * Returns a value from the table.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The value (possibly <code>null</code>).
     */
    public Number getValue(Comparable row, Comparable column) {
        Number x = null;
        return x;
    }

    public double getEnd(int row, int column) {
        double xValue = ((Double) getValue(row,column)).doubleValue();
        return xValue+deltaX/2;
    }

    public Number getEndValue(int row, int column) {
        double xValue = getEnd(row,column);
        return new Double(xValue);
    }
    public Number getEndValue(Comparable row, Comparable column) {
         return null;
    }
    public double getStart(int row, int column) {
        double xValue = ((Double) getValue(row,column)).doubleValue();
        return xValue-deltaX/2.0;
    }

    public Number getStartValue(int row, int column) {
        double xValue = getStart(row,column);
        return new Double(xValue);
   }
    public Number getStartValue(Comparable row, Comparable column) {
         return null;
    }


}
