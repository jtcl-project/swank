/*
 * Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file "LICENSE" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 *
 */
package com.onemoonscientific.swank;

import tcl.lang.*;

import java.awt.*;

import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;


class SwkJTableWidgetCmd implements Command {
    static final private String[] validCmds = {
        "cget", "configure", "object", "jadd", "sort", "column", "rowforindex",
        "indexforrow", "update", "set", "get", "mset", "mget", "setmodel",
        "selection", "showrow", "columnatpoint", "rowatpoint",
        "convertcolumnindextomodel", "columnwidth", "columnresizable"
    };
    static final private int OPT_CGET = 0;
    static final private int OPT_CONFIGURE = 1;
    static final private int OPT_OBJECT = 2;
    static final private int OPT_JADD = 3;
    static final private int OPT_SORT = 4;
    static final private int OPT_COLUMN = 5;
    static final private int OPT_ROWFORINDEX = 6;
    static final private int OPT_INDEXFORROW = 7;
    static final private int OPT_UPDATE = 8;
    static final private int OPT_SET = 9;
    static final private int OPT_GET = 10;
    static final private int OPT_MSET = 11;
    static final private int OPT_MGET = 12;
    static final private int OPT_SETMODEL = 13;
    static final private int OPT_SELECTION = 14;
    static final private int OPT_SHOWROW = 15;
    static final private int OPT_COLUMNATPOINT = 16;
    static final private int OPT_ROWATPOINT = 17;
    static final private int OPT_CONVERTCOLUMNINDEXTOMODEL = 18;
    static final private int OPT_COLUMNWIDTH = 19;
    static final private int OPT_COLUMNRESIZABLE = 20;
    static boolean gotDefaults = false;
    int index;
    Interp interp = null;

    public static String[] getValidCmds() {
        return validCmds;
    }

    public void cmdProc(final Interp interp, final TclObject[] argv)
        throws TclException {
        int i;

        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv,
                "option ?arg arg ...?");
        }

        final int opt = TclIndex.get(interp, argv[1], validCmds, "option", 0);
        final TclObject tObj = (TclObject) Widgets.theWidgets.get(argv[0].toString());
        this.interp = interp;

        if (tObj == null) {
            throw new TclException(interp,
                "bad window path name \"" + argv[0].toString() + "\"");
        }

        final SwkJTable swkjtable = (SwkJTable) ReflectObject.get(interp, tObj);

        switch (opt) {
        case OPT_CGET:

            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            interp.setResult(swkjtable.jget(interp, argv[2]));

            break;

        case OPT_CONFIGURE:

            if (!gotDefaults) {
                swkjtable.setResourceDefaults();
                gotDefaults = true;
            }

            if (argv.length == 2) {
                swkjtable.jgetAll(interp);
            } else if (argv.length == 3) {
                String result = swkjtable.jget(interp, argv[2]);
                ResourceObject ro = (ResourceObject) SwkJTable.resourceDB.get(argv[2].toString());
                TclObject list = TclList.newInstance();
                TclList.append(interp, list,
                    TclString.newInstance(argv[2].toString()));
                TclList.append(interp, list, TclString.newInstance(ro.resource));
                TclList.append(interp, list, TclString.newInstance(ro.className));
                TclList.append(interp, list,
                    TclString.newInstance(ro.defaultVal));
                TclList.append(interp, list, TclString.newInstance(result));
                interp.setResult(list);
            } else {
                swkjtable.configure(interp, argv, 2);
            }

            break;

        case OPT_OBJECT:
            interp.setResult(tObj);

            break;

        case OPT_JADD:

            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            swkjtable.jadd(interp, argv[2]);

            break;

        case OPT_SORT:
            sort(interp, swkjtable, argv);

            break;

        case OPT_COLUMN:
            column(interp, swkjtable, argv);

            break;

        case OPT_ROWFORINDEX:
            rowForIndex(interp, swkjtable, argv);

            break;

        case OPT_INDEXFORROW:
            indexForRow(interp, swkjtable, argv);

            break;

        case OPT_UPDATE:
            update(interp, swkjtable, argv);

            break;

        case OPT_SET:
            set(interp, swkjtable, argv, true);

            break;

        case OPT_MSET:
            set(interp, swkjtable, argv, false);

            break;

        case OPT_GET:
            get(interp, swkjtable, argv, true);

            break;

        case OPT_MGET:
            get(interp, swkjtable, argv, false);

            break;

        case OPT_SETMODEL:
            setModel(interp, swkjtable, argv);

            break;

        case OPT_SELECTION:
            selection(interp, swkjtable, argv);

            break;

        case OPT_SHOWROW:
            showRow(interp, swkjtable, argv);

            break;

        case OPT_COLUMNATPOINT:
            rowOrColumnAtPoint(interp, swkjtable, argv, true);

            break;

        case OPT_ROWATPOINT:
            rowOrColumnAtPoint(interp, swkjtable, argv, false);

            break;

        case OPT_CONVERTCOLUMNINDEXTOMODEL:
            convertColumnIndexToModel(interp, swkjtable, argv);

            break;

        case OPT_COLUMNWIDTH:
            columnWidth(interp, swkjtable, argv);

            break;

        case OPT_COLUMNRESIZABLE:
            columnResizable(interp, swkjtable, argv);

            break;

        default:
            throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    void sort(final Interp interp, final SwkJTable swkjtable,
        final TclObject[] argv) throws TclException {
        if (argv.length < 3) {
            throw new TclNumArgsException(interp, 2, argv,
                "?-descending|-ascending? ?-date? column");
        }

        int iCol = 0;
        int status = TableSorter.NOT_SORTED;
        String sortMode = null;
        iCol = TclInteger.get(interp, argv[argv.length - 1]);

        for (int iArg = 2; iArg < (argv.length - 1); iArg++) {
            if (argv[iArg].toString().startsWith("-desc")) {
                status = TableSorter.DESCENDING;
            } else if (argv[iArg].toString().startsWith("-asc")) {
                status = TableSorter.ASCENDING;
            } else if (argv[iArg].toString().startsWith("-date")) {
                sortMode = "date";
            } else {
                throw new TclException(interp, "sort: invalid option");
            }
        }

        (new Sort()).exec(swkjtable, iCol, status, sortMode);
    }

    void column(final Interp interp, final SwkJTable swkjtable,
        final TclObject[] argv) throws TclException {
        if (argv.length != 4) {
            throw new TclNumArgsException(interp, 2, argv, "option");
        }

        int iCol = TclInteger.get(interp, argv[2]);
        Object colClass = ReflectObject.get(interp, argv[3]);

        if (!(colClass instanceof Class)) {
            throw new TclException(interp, "must provide class");
        }

        (new Column()).exec(swkjtable, iCol, colClass);
    }

    void rowForIndex(final Interp interp, final SwkJTable swkjtable,
        final TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "option");
        }

        int index = TclInteger.get(interp, argv[2]);
        int row = (new Row()).exec(swkjtable, index, true);
        interp.setResult(row);
    }

    void indexForRow(final Interp interp, final SwkJTable swkjtable,
        final TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "option");
        }

        int row = TclInteger.get(interp, argv[2]);
        int index = (new Row()).exec(swkjtable, row, false);
        interp.setResult(index);
    }

    void update(final Interp interp, final SwkJTable swkjtable,
        final TclObject[] argv) throws TclException {
        if (argv.length > 3) {
            throw new TclNumArgsException(interp, 2, argv, "?data?");
        }

        if ((argv.length == 3) && (argv[2].toString().equals("data"))) {
            (new Update()).data(swkjtable);
        } else {
            (new Update()).structure(swkjtable);
        }
    }

    void set(final Interp interp, final SwkJTable swkjtable,
        final TclObject[] argv, final boolean useSorted)
        throws TclException {
        if (argv.length != 5) {
            throw new TclNumArgsException(interp, 2, argv, "option");
        }

        int iRow = TclInteger.get(interp, argv[2]);
        int iCol = TclInteger.get(interp, argv[3]);
        (new Set()).exec(swkjtable, iRow, iCol, useSorted, argv[4]);
    }

    void get(final Interp interp, final SwkJTable swkjtable,
        final TclObject[] argv, final boolean useSorted)
        throws TclException {
        if (argv.length != 4) {
            throw new TclNumArgsException(interp, 2, argv, "option");
        }

        int iRow = TclInteger.get(interp, argv[2]);
        int iCol = TclInteger.get(interp, argv[3]);
        Object tblObject = (new Get()).exec(swkjtable, iRow, iCol, useSorted);

        if (tblObject == null) {
            interp.setResult("");
        } else if (tblObject instanceof TclObject) {
            interp.setResult((TclObject) tblObject);
        } else {
            interp.setResult(tblObject.toString());
        }
    }

    void setModel(final Interp interp, final SwkJTable swkjtable,
        final TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "model");
        }

        SwkTableModel tableModel = (SwkTableModel) ReflectObject.get(interp,
                argv[2]);

        if (tableModel == null) {
            throw new TclException(interp, "in setmodel: tableModel null");
        }

        TableModel currentModel = swkjtable.getModel();

        if (currentModel instanceof TableSorter) {
            ((TableSorter) currentModel).setTableModel(tableModel);
        } else {
            swkjtable.setModel(tableModel);
        }

        swkjtable.swkTableModel = tableModel;
    }

    void selection(final Interp interp, final SwkJTable swkjtable,
        final TclObject[] argv) throws TclException {
        if ((argv.length < 3) || (argv.length > 5)) {
            throw new TclNumArgsException(interp, 2, argv, "");
        }

        if (argv[2].toString().equals("countrows")) {
            int nSelected = (new SelectionGet()).countRows(swkjtable);
            interp.setResult(nSelected);
        } else if (argv[2].toString().equals("clear")) {
            (new SelectionSet()).clear(swkjtable);
        } else if (argv[2].toString().equals("addrow")) {
            if ((argv.length != 4) && (argv.length != 5)) {
                throw new TclNumArgsException(interp, 2, argv, "first ? last ?");
            }

            int first = TclInteger.get(interp, argv[3]);
            int last = first;

            if (argv.length == 5) {
                last = TclInteger.get(interp, argv[4]);
            }

            (new SelectionSet()).set(swkjtable, first, last);
        } else if (argv[2].toString().equals("getrows")) {
            int[] srows = (new SelectionGet()).getRows(swkjtable);
            TclObject tList = TclList.newInstance();

            if (srows != null) {
                for (int i = 0; i < srows.length; i++) {
                    TclList.append(interp, tList,
                        TclInteger.newInstance(srows[i]));
                }
            }

            interp.setResult(tList);
        }
    }

    void showRow(final Interp interp, final SwkJTable swkjtable,
        final TclObject[] argv) throws TclException {
        if ((argv.length < 3) || (argv.length > 4)) {
            throw new TclNumArgsException(interp, 2, argv, "index1 ?index2?");
        }

        int row1 = TclInteger.get(interp, argv[2]);
        int row2 = row1;

        if (argv.length == 4) {
            row2 = TclInteger.get(interp, argv[3]);
        }

        (new ShowRow()).exec(swkjtable, row1, row2);
    }

    void rowOrColumnAtPoint(final Interp interp, final SwkJTable swkjtable,
        final TclObject[] argv, final boolean rowMode)
        throws TclException {
        if (argv.length != 4) {
            throw new TclNumArgsException(interp, 2, argv, "x y");
        }

        int x = TclInteger.get(interp, argv[2]);
        int y = TclInteger.get(interp, argv[3]);
        int value = (new RowOrColumnAtPoint()).exec(swkjtable, x, y, rowMode);
        interp.setResult(value);
    }

    void convertColumnIndexToModel(final Interp interp,
        final SwkJTable swkjtable, final TclObject[] argv)
        throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "column");
        }

        int column = TclInteger.get(interp, argv[2]);
        int value = (new ConvertColumn()).exec(swkjtable, column);
        interp.setResult(value);
    }

    void columnWidth(final Interp interp, final SwkJTable swkjtable,
        final TclObject[] argv) throws TclException {
        if ((argv.length != 3) && (argv.length != 4)) {
            throw new TclNumArgsException(interp, 2, argv, "column");
        }

        int column = TclInteger.get(interp, argv[2]);
        int width = 0;

        if (argv.length == 4) {
            width = TclInteger.get(interp, argv[3]);
            (new SetColumnWidth()).exec(swkjtable, column, width);
        } else {
            width = (new GetColumnWidth()).exec(swkjtable, column);
        }

        interp.setResult(width);
    }

    void columnResizable(final Interp interp, final SwkJTable swkjtable,
        final TclObject[] argv) throws TclException {
        if ((argv.length != 3) && (argv.length != 4)) {
            throw new TclNumArgsException(interp, 2, argv, "column");
        }

        int column = TclInteger.get(interp, argv[2]);
        boolean resizable = false;

        if (argv.length == 4) {
            resizable = TclBoolean.get(interp, argv[3]);
            (new SetColumnResizable()).exec(swkjtable, column, resizable);
        } else {
            resizable = (new GetColumnResizable()).exec(swkjtable, column);
        }

        interp.setResult(resizable);
    }

    class Sort extends UpdateOnEventThread {
        SwkJTable swkjtable = null;
        int iCol = 0;
        int status = TableSorter.NOT_SORTED;
        String sortMode = null;

        void exec(final SwkJTable swkjtable, final int iCol, final int status,
            final String sortMode) {
            this.swkjtable = swkjtable;
            this.iCol = iCol;
            this.status = status;
            this.sortMode = sortMode;
            execOnThread();
        }

        public void run() {
            ((TableSorter) swkjtable.getModel()).setSortingStatus(iCol, status);
        }
    }

    class Column extends UpdateOnEventThread {
        SwkJTable swkjtable = null;
        int iCol = 0;
        Object colClass = null;

        void exec(final SwkJTable swkjtable, final int iCol,
            final Object colClass) {
            this.swkjtable = swkjtable;
            this.iCol = iCol;
            this.colClass = colClass;
            execOnThread();
        }

        public void run() {
            swkjtable.swkTableModel.setColumnClass(interp, iCol,
                (Class) colClass);
        }
    }

    class Row extends GetValueOnEventThread {
        SwkJTable swkjtable = null;
        int inVal = 0;
        boolean rowForIndex = false;
        int result = 0;

        int exec(final SwkJTable swkjtable, final int inVal,
            final boolean rowForIndex) {
            this.inVal = inVal;
            this.rowForIndex = rowForIndex;
            this.swkjtable = swkjtable;
            execOnThread();

            return result;
        }

        public void run() {
            if (rowForIndex) {
                result = ((TableSorter) swkjtable.getModel()).modelIndex(inVal);
            } else {
                result = ((TableSorter) swkjtable.getModel()).viewIndex(inVal);
            }
        }
    }

    class Update extends UpdateOnEventThread {
        static final int DATA = 0;
        static final int STRUCTURE = 1;
        SwkJTable swkjtable = null;
        int mode = DATA;

        void structure(final SwkJTable swkjtable) {
            this.swkjtable = swkjtable;
            mode = STRUCTURE;
            execOnThread();
        }

        void data(final SwkJTable swkjtable) {
            this.swkjtable = swkjtable;
            mode = DATA;
            execOnThread();
        }

        public void run() {
            if (mode == DATA) {
                swkjtable.swkTableModel.fireTableChanged(new TableModelEvent(
                        swkjtable.swkTableModel));
            } else {
                swkjtable.swkTableModel.fireTableStructureChanged();
            }
        }
    }

    class Set extends UpdateOnEventThread {
        SwkJTable swkjtable = null;
        int iRow = 0;
        int iCol = 0;
        boolean useSorted = false;
        TclObject value = null;

        void exec(final SwkJTable swkjtable, final int iRow, final int iCol,
            final boolean useSorted, final TclObject value) {
            this.iRow = iRow;
            this.iCol = iCol;
            this.value = value.duplicate();
            this.useSorted = useSorted;
            this.swkjtable = swkjtable;
            execOnThread();
        }

        public void run() {
            if (useSorted) {
                swkjtable.getModel().setValueAt(value, iRow, iCol);
            } else {
                TableModel currentModel = swkjtable.getModel();
                TableModel nonSortModel = currentModel;

                if (currentModel instanceof TableSorter) {
                    nonSortModel = ((TableSorter) currentModel).getTableModel();
                }

                nonSortModel.setValueAt(value, iRow, iCol);
            }
        }
    }

    class Get extends GetValueOnEventThread {
        SwkJTable swkjtable = null;
        int iRow = 0;
        int iCol = 0;
        boolean useSorted = false;
        Object tblObject = null;

        Object exec(final SwkJTable swkjtable, final int iRow, final int iCol,
            final boolean useSorted) {
            this.iRow = iRow;
            this.iCol = iCol;
            this.useSorted = useSorted;
            this.swkjtable = swkjtable;
            execOnThread();

            return tblObject;
        }

        public void run() {
            if (useSorted) {
                tblObject = swkjtable.getModel().getValueAt(iRow, iCol);
            } else {
                TableModel currentModel = swkjtable.getModel();
                TableModel nonSortModel = currentModel;

                if (currentModel instanceof TableSorter) {
                    nonSortModel = ((TableSorter) currentModel).getTableModel();
                }

                tblObject = nonSortModel.getValueAt(iRow, iCol);
            }
        }
    }

    class SelectionSet extends UpdateOnEventThread {
        final static int CLEAR = 0;
        final static int SET = 1;
        SwkJTable swkjtable = null;
        int first = 0;
        int last = 0;
        int mode = 0;

        void set(final SwkJTable swkjtable, final int first, final int last) {
            this.swkjtable = swkjtable;
            this.first = first;
            this.last = last;
            this.mode = SET;
            execOnThread();
        }

        void clear(final SwkJTable swkjtable) {
            this.swkjtable = swkjtable;
            this.mode = CLEAR;
            execOnThread();
        }

        public void run() {
            int nRows = swkjtable.getRows();

            if (mode == SET) {
                if (first > last) {
                    int hold = first;
                    first = last;
                    last = hold;
                }

                if (nRows == 0) {
                    return;
                }

                if (last > nRows) {
                    last = nRows - 1;
                }

                if (first > nRows) {
                    first = nRows - 1;
                }

                if (last < 0) {
                    last = 0;
                }

                if (first < 0) {
                    first = 0;
                }

                swkjtable.addRowSelectionInterval(first, last);
            } else if (mode == CLEAR) {
                if (nRows == 0) {
                    return;
                }

                swkjtable.clearSelection();
            } else {
                // FIXME
                System.out.println("invalid option to SelectionSet");
            }
        }
    }

    class SelectionGet extends GetValueOnEventThread {
        final static int COUNT_ROWS = 0;
        final static int GET_ROWS = 1;
        SwkJTable swkjtable = null;
        int mode = 0;
        int nSelected = 0;
        int[] selectedRows = null;

        int countRows(final SwkJTable swkjtable) {
            this.swkjtable = swkjtable;
            this.mode = COUNT_ROWS;
            execOnThread();

            return nSelected;
        }

        int[] getRows(final SwkJTable swkjtable) {
            this.swkjtable = swkjtable;
            this.mode = GET_ROWS;
            execOnThread();

            return selectedRows;
        }

        public void run() {
            if (mode == COUNT_ROWS) {
                nSelected = swkjtable.getSelectedRowCount();
            } else if (mode == GET_ROWS) {
                selectedRows = swkjtable.getSelectedRows();
            } else {
                // FIXME
                System.out.println("invalid option to SelectionGet");
            }
        }
    }

    class ShowRow extends UpdateOnEventThread {
        SwkJTable swkjtable = null;
        int row1 = 0;
        int row2 = 0;

        void exec(final SwkJTable swkjtable, final int row1, final int row2) {
            this.swkjtable = swkjtable;
            this.row1 = row1;
            this.row2 = row2;
            execOnThread();
        }

        public void run() {
            Rectangle rect1 = swkjtable.getCellRect(row1, 0, true);
            Rectangle rect2 = swkjtable.getCellRect(row2, 0, true);
            rect1.add(rect2);
            swkjtable.scrollRectToVisible(rect1);
        }
    }

    class ConvertColumn extends GetValueOnEventThread {
        SwkJTable swkjtable = null;
        int column = 0;
        int result = 0;

        int exec(final SwkJTable swkjtable, final int column) {
            this.column = column;
            this.swkjtable = swkjtable;
            execOnThread();

            return result;
        }

        public void run() {
            result = swkjtable.convertColumnIndexToModel(column);
        }
    }

    class RowOrColumnAtPoint extends GetValueOnEventThread {
        SwkJTable swkjtable = null;
        int x = 0;
        int y = 0;
        boolean rowMode = false;
        int result = 0;

        int exec(final SwkJTable swkjtable, final int x, final int y,
            final boolean rowMode) {
            this.x = x;
            this.y = y;
            this.rowMode = rowMode;
            this.swkjtable = swkjtable;
            execOnThread();

            return result;
        }

        public void run() {
            if (rowMode) {
                result = swkjtable.rowAtPoint(new Point(x, y));
            } else {
                result = swkjtable.columnAtPoint(new Point(x, y));
                result = swkjtable.convertColumnIndexToModel(result);
            }
        }
    }

    class SetColumnWidth extends UpdateOnEventThread {
        SwkJTable swkjtable = null;
        int column = 0;
        int width = 0;

        void exec(final SwkJTable swkjtable, final int column, final int width) {
            this.column = column;
            this.width = width;
            this.swkjtable = swkjtable;
            execOnThread();
        }

        public void run() {
            TableColumnModel cM = swkjtable.getColumnModel();
            TableColumn tableColumn = cM.getColumn(column);
            tableColumn.setPreferredWidth(width);

            if (width == 0) {
                tableColumn.setMinWidth(width);
                tableColumn.setMaxWidth(width);
            }

            tableColumn.setWidth(width);
        }
    }

    class GetColumnWidth extends GetValueOnEventThread {
        SwkJTable swkjtable = null;
        int column = 0;
        int width = 0;

        int exec(final SwkJTable swkjtable, final int column) {
            this.column = column;
            this.swkjtable = swkjtable;
            execOnThread();

            return width;
        }

        public void run() {
            TableColumnModel cM = swkjtable.getColumnModel();
            TableColumn tableColumn = cM.getColumn(column);
            width = tableColumn.getWidth();
        }
    }

    class SetColumnResizable extends UpdateOnEventThread {
        SwkJTable swkjtable = null;
        int column = 0;
        boolean resizable = false;

        void exec(final SwkJTable swkjtable, final int column,
            final boolean resizable) {
            this.column = column;
            this.resizable = resizable;
            this.swkjtable = swkjtable;
            execOnThread();
        }

        public void run() {
            TableColumnModel cM = swkjtable.getColumnModel();
            TableColumn tableColumn = cM.getColumn(column);
            tableColumn.setResizable(resizable);
        }
    }

    class GetColumnResizable extends UpdateOnEventThread {
        SwkJTable swkjtable = null;
        int column = 0;
        boolean resizable = false;

        boolean exec(final SwkJTable swkjtable, final int column) {
            this.swkjtable = swkjtable;
            this.column = column;
            this.swkjtable = swkjtable;
            execOnThread();

            return resizable;
        }

        public void run() {
            TableColumnModel cM = swkjtable.getColumnModel();
            TableColumn tableColumn = cM.getColumn(column);
            resizable = tableColumn.getResizable();
        }
    }
}
