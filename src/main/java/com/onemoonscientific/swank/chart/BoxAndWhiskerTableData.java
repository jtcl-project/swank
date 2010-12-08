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

import tcl.lang.*;

import java.util.*;
import java.awt.EventQueue;

public class BoxAndWhiskerTableData extends BoxAndWhiskerData {

    int xColumn = -1;
    int gColumn = -1;
    int yColumn = -1;
    int[] yColumns = new int[0];

    ;
    SwkTableModel tableModel = null;
    Map seriesMap = new TreeMap();
    List seriesNames = new ArrayList();
    List seriesLists = new ArrayList();

    public BoxAndWhiskerTableData() {
        name = "xyData" + id;
        datasetMap.put(name, (Object) this);
        id++;
    }

    public BoxAndWhiskerTableData(String name) {
        this.name = name;
        datasetMap.put(name, (Object) this);
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

        void exec(SwkTableModel model) {
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
        yColumns = new int[1];
        yColumns[0] = -1;
    }

    public void setXColumn(int column) {
        xColumn = column;
    }

    public void setGColumn(int column) {
        gColumn = column;
    }

    public void setYColumn(int column) {
        yColumns = new int[1];
        yColumns[0] = -1;
    }

    public void setYColumns(Interp interp, TclObject columnArgs) throws TclException {
        TclObject[] columnObjects = TclList.getElements(interp, columnArgs);
        yColumns = new int[columnObjects.length];
        for (int i = 0; i < columnObjects.length; i++) {
            int column = TclInteger.get(interp, columnObjects[i]);
            yColumns[i] = column;
        }
    }

    public void setYColumns(int[] columns) {
        yColumns = columns;
    }

    public void getSeries() {
        int nRows = tableModel.getNRows();
        String yColumnName = "Data";
        for (int j = 0; j < yColumns.length; j++) {
            seriesMap.clear();
            seriesLists.clear();
            seriesNames.clear();
            int yColumn = yColumns[j];
            int nSeries = 0;
            if ((yColumn >= 0) && (yColumn < tableModel.getNCols())) {
                yColumnName = tableModel.getColumnName(yColumn);
                for (int i = 0; i < nRows; i++) {
                    Object gObject = tableModel.getValueAt(i, gColumn);
                    Number y = null;
                    Object yObject = tableModel.getValueAt(i, yColumn);
                    if (yObject instanceof Number) {
                        y = (Number) yObject;
                    }

                    Integer seriesIndex = (Integer) seriesMap.get(gObject);
                    ArrayList arrayList = null;
                    if (seriesIndex == null) {
                        seriesNames.add(gObject);
                        seriesMap.put(gObject, Integer.valueOf(nSeries));
                        arrayList = new ArrayList();
                        seriesLists.add(arrayList);
                        nSeries++;
                    } else {
                        int index = seriesIndex.intValue();
                        arrayList = (ArrayList) seriesLists.get(index);
                    }
                    if (y != null) {
                        arrayList.add(y);
                    }
                }
            }
            for (int i = 0; i < nSeries; i++) {
                ArrayList arrayList = (ArrayList) seriesLists.get(i);
                Object seriesName = seriesNames.get(i);
                add(arrayList, (Comparable) seriesName, (Comparable) yColumnName);
            }
        }
    }
}

