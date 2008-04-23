package com.onemoonscientific.swank;

import java.awt.*;
import java.awt.event.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;


/**
 * TableSorter is a decorator for TableModels; adding sorting
 * functionality to a supplied TableModel. TableSorter does
 * not store or copy the data in its TableModel; instead it maintains
 * a map from the row indexes of the view to the row indexes of the
 * model. As requests are made of the sorter (like getValueAt(row, col))
 * they are passed to the underlying model after the row numbers
 * have been translated via the internal mapping array. This way,
 * the TableSorter appears to hold another copy of the table
 * with the rows in a different order.
 * <p/>
 * TableSorter registers itself as a listener to the underlying model,
 * just as the JTable itself would. Events recieved from the model
 * are examined, sometimes manipulated (typically widened), and then
 * passed on to the TableSorter's listeners (typically the JTable).
 * If a change to the model has invalidated the order of TableSorter's
 * rows, a note of this is made and the sorter will resort the
 * rows the next time a value is requested.
 * <p/>
 * When the tableHeader property is set, either by using the
 * setTableHeader() method or the two argument constructor, the
 * table header may be used as a complete UI for TableSorter.
 * The default renderer of the tableHeader is decorated with a renderer
 * that indicates the sorting status of each column. In addition,
 * a mouse listener is installed with the following behavior:
 * <ul>
 * <li>
 * Mouse-click: Clears the sorting status of all other columns
 * and advances the sorting status of that column through three
 * values: {NOT_SORTED, ASCENDING, DESCENDING} (then back to
 * NOT_SORTED again).
 * <li>
 * SHIFT-mouse-click: Clears the sorting status of all other columns
 * and cycles the sorting status of the column through the same
 * three values, in the opposite order: {NOT_SORTED, DESCENDING, ASCENDING}.
 * <li>
 * CONTROL-mouse-click and CONTROL-SHIFT-mouse-click: as above except
 * that the changes to the column do not cancel the statuses of columns
 * that are already sorting - giving a way to initiate a compound
 * sort.
 * </ul>
 * <p/>
 * This is a long overdue rewrite of a class of the same name that
 * first appeared in the swing table demos in 1997.
 *
 * @author Philip Milne
 * @author Brendon McLean
 * @author Dan van Enckevort
 * @author Parwinder Sekhon
 * @version 2.0 02/27/04
 */
public class TableSorter extends AbstractTableModel {
    public static final int DESCENDING = -1;
    public static final int NOT_SORTED = 0;
    public static final int ASCENDING = 1;
    private int lastCompareType = 0;
    private static Directive EMPTY_DIRECTIVE = new Directive(-1, NOT_SORTED);
    public static final Comparator COMPARABLE_COMPARATOR = new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) o1).compareTo(o2);
            }
        };
/*
    public static final Comparator DICTIONARY_COMPARATOR = new Comparator() {
            public int compare(Object o1, Object o2) {
                return doDictionary(o1.toString(), o2.toString());
            }
        };
*/
   public static final Comparator LEXICAL_COMPARATOR = new Comparator() {
        public int compare(Object o1, Object o2) {
            return o1.toString().compareTo(o2.toString());
        }
    };


    protected TableModel tableModel;
    private SimpleDateFormat dateFormat = null;
    private Row[] viewToModel;
    private int[] modelToView;
    private JTableHeader tableHeader;
    private MouseListener mouseListener;
    private TableModelListener tableModelListener;
    private Map columnComparators = new HashMap();
    private List sortingColumns = new ArrayList();

    public TableSorter() {
        this.mouseListener = new MouseHandler();
        this.tableModelListener = new TableModelHandler();
    }

    public TableSorter(TableModel tableModel) {
        this();
        setTableModel(tableModel);
    }

    public TableSorter(TableModel tableModel, JTableHeader tableHeader) {
        this();
        setTableHeader(tableHeader);
        setTableModel(tableModel);
    }

    private void clearSortingState() {
        viewToModel = null;
        modelToView = null;
    }

    public TableModel getTableModel() {
        return tableModel;
    }

    public void setTableModel(TableModel tableModel) {
        if (this.tableModel != null) {
            this.tableModel.removeTableModelListener(tableModelListener);
        }

        this.tableModel = tableModel;

        if (this.tableModel != null) {
            this.tableModel.addTableModelListener(tableModelListener);
        }

        clearSortingState();
        fireTableStructureChanged();
    }

    public JTableHeader getTableHeader() {
        return tableHeader;
    }

    public void setTableHeader(JTableHeader tableHeader) {
        if (this.tableHeader != null) {
            this.tableHeader.removeMouseListener(mouseListener);

            TableCellRenderer defaultRenderer = this.tableHeader.getDefaultRenderer();

            if (defaultRenderer instanceof SortableHeaderRenderer) {
                this.tableHeader.setDefaultRenderer(((SortableHeaderRenderer) defaultRenderer).tableCellRenderer);
            }
        }

        this.tableHeader = tableHeader;

        if (this.tableHeader != null) {
            this.tableHeader.addMouseListener(mouseListener);
            this.tableHeader.setDefaultRenderer(new SortableHeaderRenderer(
                    this.tableHeader.getDefaultRenderer()));
        }
    }

    public boolean isSorting() {
        return sortingColumns.size() != 0;
    }

    private Directive getDirective(int column) {
        for (int i = 0; i < sortingColumns.size(); i++) {
            Directive directive = (Directive) sortingColumns.get(i);

            if (directive.column == column) {
                return directive;
            }
        }

        return EMPTY_DIRECTIVE;
    }

    public int getSortingStatus(int column) {
        return getDirective(column).direction;
    }

    private void sortingStatusChanged() {
        clearSortingState();
        fireTableDataChanged();

        if (tableHeader != null) {
            tableHeader.repaint();
        }
    }

    public void setSortingStatus(int column, int status) {
        Directive directive = getDirective(column);

        if (directive != EMPTY_DIRECTIVE) {
            sortingColumns.remove(directive);
        }

        if (status != NOT_SORTED) {
            sortingColumns.add(new Directive(column, status));
        }

        sortingStatusChanged();
    }

    protected Icon getHeaderRendererIcon(int column, int size) {
        Directive directive = getDirective(column);

        if (directive == EMPTY_DIRECTIVE) {
            return null;
        }

        return new Arrow(directive.direction == DESCENDING, size,
            sortingColumns.indexOf(directive));
    }

    private void cancelSorting() {
        sortingColumns.clear();
        sortingStatusChanged();
    }

    public void setColumnComparator(Class type, Comparator comparator) {
        if (comparator == null) {
            columnComparators.remove(type);
        } else {
            columnComparators.put(type, comparator);
        }
    }

    protected Comparator getComparator(int column) {
        Class columnType = tableModel.getColumnClass(column);
        Comparator comparator = (Comparator) columnComparators.get(columnType);

        if (comparator != null) {
            return comparator;
        }

        if (Comparable.class.isAssignableFrom(columnType)) {
            return COMPARABLE_COMPARATOR;
        }

        return LEXICAL_COMPARATOR;
        //return DICTIONARY_COMPARATOR;
    }

    private Row[] getViewToModel() {
        if (viewToModel == null || viewToModel.length != tableModel.getRowCount()) {
            int tableModelRowCount = tableModel.getRowCount();
            viewToModel = new Row[tableModelRowCount];

            for (int row = 0; row < tableModelRowCount; row++) {
                viewToModel[row] = new Row(row);
            }

            if (isSorting()) {
                Arrays.sort(viewToModel);
            }
        }

        return viewToModel;
    }

    public int modelIndex(int viewIndex) {
        
        return getViewToModel()[viewIndex].modelIndex;
    }

    private int[] getModelToView() {
        if (modelToView == null) {
            int n = getViewToModel().length;
            modelToView = new int[n];

            for (int i = 0; i < n; i++) {
                modelToView[modelIndex(i)] = i;
            }
        }

        return modelToView;
    }

    public int viewIndex(int modelIndex) {
        return getModelToView()[modelIndex];
    }

    // TableModel interface methods
    public int getRowCount() {
        return (tableModel == null) ? 0 : tableModel.getRowCount();
    }

    public int getColumnCount() {
        return (tableModel == null) ? 0 : tableModel.getColumnCount();
    }

    public String getColumnName(int column) {
        return tableModel.getColumnName(column);
    }

    public Class getColumnClass(int column) {
        return tableModel.getColumnClass(column);
    }

    public boolean isCellEditable(int row, int column) {
        return tableModel.isCellEditable(modelIndex(row), column);
    }

    public Object getValueAt(int row, int column) {
  //      System.out.print("row: " + row + "column: " + column);
  //      System.out.print(" tableModel: row "+tableModel.getRowCount()+"Column :"+ tableModel.getColumnCount());
    //int k = modelIndex(row);
  //  System.out.println(" modelIndex(row)" +k);
        return tableModel.getValueAt(modelIndex(row), column);
    }

    public void setValueAt(Object aValue, int row, int column) {
        tableModel.setValueAt(aValue, modelIndex(row), column);
    }

    // FIXME, add mode that is case insensitive?

    /**
     * Compares the order of two strings in "dictionary" order.
     *  Copied from Qsort.java of tcljava
     *
     * @param str1 first item.
     * @param str2 second item.
     * @return 0 if they are equal, 1 if obj1 > obj2, -1 otherwise.
     */
    private final int doDictionary(String str1, String str2) {
        int diff = 0;
        int zeros;
        int secondaryDiff = 0;
        str1 = str1.toUpperCase();
        str2 = str2.toUpperCase();
        boolean cont = true;
        int i1 = 0;
        int i2 = 0;
        int len1 = str1.length();
        int len2 = str2.length();
        if ((len1 == 0) && (len2 == 0)) {
            lastCompareType = 121;
             return 0;
        } else if (len1 ==0) {
            lastCompareType = 122;
             return -1;
        } else if (len2 ==0) {
            lastCompareType = 123;
             return 1;
        }
        while (cont) {
            if ((i1 >= len1) || (i2 >= len2)) {
                break;
            }

            if (Character.isDigit(str2.charAt(i2)) &&
                    Character.isDigit(str1.charAt(i1))) {
                // There are decimal numbers embedded in the two
                // strings.  Compare them as numbers, rather than
                // strings.  If one number has more leading zeros than
                // the other, the number with more leading zeros sorts
                // later, but only as a secondary choice.
                zeros = 0;

                while ((i2 < (len2 - 1)) && (str2.charAt(i2) == '0')) {
                    i2++;
                    zeros--;
                }

                while ((i1 < (len1 - 1)) && (str1.charAt(i1) == '0')) {
                    i1++;
                    zeros++;
                }

                if (secondaryDiff == 0) {
                    secondaryDiff = zeros;
                }

                // The code below compares the numbers in the two
                // strings without ever converting them to integers.  It
                // does this by first comparing the lengths of the
                // numbers and then comparing the digit values.
                diff = 0;

                while (true) {
                    if ((i1 >= len1) || (i2 >= len2)) {
                        cont = false;

                        break;
                    }

                    if (diff == 0) {
                        diff = str1.charAt(i1) - str2.charAt(i2);
                    }

                    i1++;
                    i2++;

                    if ((i1 >= len1) || (i2 >= len2)) {
                        cont = false;

                        break;
                    }

                    if (!Character.isDigit(str2.charAt(i2))) {
                        if (Character.isDigit(str1.charAt(i1))) {
            lastCompareType = 124;
                            return 1;
                        } else {
                            if (diff != 0) {
            lastCompareType = 125;
                                return diff;
                            }

                            break;
                        }
                    } else if (!Character.isDigit(str1.charAt(i1))) {
            lastCompareType = 126;
                        return -1;
                    }
                }

                continue;
            }

            diff = str1.charAt(i1) - str2.charAt(i2);

            if (diff != 0) {
                if (Character.isUpperCase(str1.charAt(i1)) &&
                        Character.isLowerCase(str2.charAt(i2))) {
                    diff = Character.toLowerCase(str1.charAt(i1)) -
                        str2.charAt(i2);

                    if (diff != 0) {
            lastCompareType = 127;
                        return diff;
                    } else if (secondaryDiff == 0) {
                        secondaryDiff = -1;
                    }
                } else if (Character.isUpperCase(str2.charAt(i2)) &&
                        Character.isLowerCase(str1.charAt(i1))) {
                    diff = str1.charAt(i1) -
                        Character.toLowerCase(str2.charAt(i2));

                    if (diff != 0) {
            lastCompareType = 128;
                        return diff;
                    } else if (secondaryDiff == 0) {
                        secondaryDiff = 1;
                    }
                } else {
            lastCompareType = 129;
                    return diff;
                }
            }

            i1++;
            i2++;
        }

        if ((i1 >= len1) && (i2 < len2)) {
            if (!Character.isDigit(str2.charAt(i2))) {
            lastCompareType = 130;
                return 1;
            } else {
            lastCompareType = 131;
                return -1;
            }
        } else if ((i2 >= len2) && (i1 < len1)) {
            if (!Character.isDigit(str1.charAt(i1))) {
            lastCompareType = 132;
                return -1;
            } else {
            lastCompareType = 133;
                return 1;
            }
        }

        if (diff == 0) {
            diff = secondaryDiff;
        }

            lastCompareType = 134;
        return diff;
    }

    public int compareColumnObjects(int column, Object o1, Object o2) {
        if (getColumnName(column).equalsIgnoreCase("date")) {

            if (dateFormat == null) {
                if (o1.toString().indexOf(':') != -1) {
                    dateFormat = new SimpleDateFormat("d MMM yyyy HH:mm:ss");
                } else {
                    dateFormat = new SimpleDateFormat("d MMM yyyy");
                }
            }

            try {
                Date date1 = dateFormat.parse(o1.toString());
                Date date2 = dateFormat.parse(o2.toString());

                lastCompareType = 1;
                return (date1.compareTo(date2));
            } catch (ParseException parseE) {
                System.out.println(parseE.getMessage());
            }
        }

        Class type = getColumnClass(column);

        // Check for nulls.
        // If both values are null, return 0.
        if ((o1 == null) && (o2 == null)) {
            lastCompareType = 2;
            return 0;
        } else if (o1 == null) { // Define null less than everything.
            lastCompareType = 3;

            return -1;
        } else if (o2 == null) {
            lastCompareType = 4;
            return 1;
        }

        /*
         * We copy all returned values from the getValue call in case
         * an optimised model is reusing one object to return many
         * values.  The Number subclasses in the JDK are immutable and
         * so will not be used in this way but other subclasses of
         * Number might want to do this to save space and avoid
         * unnecessary heap allocation.
         */
        Object v1 = o1;
        Object v2 = o2;

        if ((type == java.lang.Number.class) ||
                (type.getSuperclass() == java.lang.Number.class)) {
            double d1;
            double d2;
            String s1 = null;
            String s2 = null;
            boolean isS1 = false;
            boolean isS2 = false;

            if (!(v1 instanceof Number)) {
                s1 = v1.toString();

                try {
                    d1 = Double.parseDouble(s1);
                } catch (NumberFormatException nfE) {
                    isS1 = true;
                    d1 = Double.NEGATIVE_INFINITY;
                }
            } else {
                Number n1 = (Number) v1;
                d1 = n1.doubleValue();
            }

            if (!(v2 instanceof Number)) {
                s2 = v2.toString();

                try {
                    d2 = Double.parseDouble(s2);
                } catch (NumberFormatException nfE) {
                    isS2 = true;
                    d2 = Double.NEGATIVE_INFINITY;
                }
            } else {
                Number n2 = (Number) v2;
                d2 = n2.doubleValue();
            }

            if (isS1 && isS2) {
                lastCompareType = 5;
                return (doDictionary(s1, s2));
            }

            if (d1 < d2) {
                lastCompareType = 6;
                return -1;
            } else if (d1 > d2) {
                lastCompareType = 7;
                return 1;
            } else {
                lastCompareType = 8;
                return 0;
            }
        } else if (type == java.util.Date.class) {
            if (!(v1 instanceof java.util.Date) ||
                    !(v2 instanceof java.util.Date)) {
                String s1 = v1.toString();
                String s2 = v2.toString();

                lastCompareType = 9;
                return (doDictionary(s1, s2));
            }

            Date d1 = (Date) v1;
            long n1 = d1.getTime();
            Date d2 = (Date) v2;
            long n2 = d2.getTime();

                lastCompareType = 10;
            if (n1 < n2) {
                return -1;
            } else if (n1 > n2) {
                return 1;
            } else {
                return 0;
            }
        } else if (type == String.class) {
            if (!(v1 instanceof java.lang.String) ||
                    !(v2 instanceof java.lang.String)) {
                String s1 = v1.toString();
                String s2 = v2.toString();

                lastCompareType = 11;
                return (doDictionary(s1, s2));
            } else {
                String s1 = (String) v1;
                String s2 = (String) v2;

                //return (doDictionary(s1, s2));
                return (doDictionary(v1.toString(), v2.toString()));
            }
        } else if (type == Boolean.class) {
            boolean b1;
            boolean b2;

            if (!(v1 instanceof java.lang.Boolean) ||
                    !(v2 instanceof java.lang.Boolean)) {
                String s1 = v1.toString();
                String s2 = v2.toString();

                Boolean bool1 = getBoolean(s1);
                Boolean bool2 = getBoolean(s1);

                if ((bool1 == null) || (bool2 == null)) {
                lastCompareType = 13;
                    return (doDictionary(s1, s2));
                }

                b1 = bool1.booleanValue();
                b2 = bool2.booleanValue();
            } else {
                Boolean bool1 = (Boolean) v1;
                b1 = bool1.booleanValue();

                Boolean bool2 = (Boolean) v2;
                b2 = bool2.booleanValue();
            }

                lastCompareType = 14;
            if (b1 == b2) {
                return 0;
            } else if (b1) { // Define false < true

                return 1;
            } else {
                return -1;
            }
        } else {
            String s1 = v1.toString();
            String s2 = v2.toString();
                lastCompareType = 15;
            return doDictionary(s1,s2);
/*
            int result = s1.compareTo(s2);

            if (result < 0) {
                return -1;
            } else if (result > 0) {
                return 1;
            } else {
                return 0;
            }
*/
        }
    }

    /*
     *----------------------------------------------------------------------
     *
     * getBoolean --
     *
     *      Given a string, return a boolean value corresponding
     *      to the string. From jacl Util.java
     *
     * Results:
     *
     *
     * Side effects:
     *      None.
     *
     *----------------------------------------------------------------------
     */
    private static Boolean getBoolean(String string) // The string representation of the boolean.
     {
        String s = string.toLowerCase();

        // The length of 's' needs to be > 1 if it begins with 'o',
        // in order to compare between "on" and "off".
        if (s.length() > 0) {
            if ("yes".startsWith(s)) {
                return Boolean.TRUE;
            } else if ("no".startsWith(s)) {
                return Boolean.FALSE;
            } else if ("true".startsWith(s)) {
                return Boolean.TRUE;
            } else if ("false".startsWith(s)) {
                return Boolean.FALSE;
            } else if ("on".startsWith(s) && (s.length() > 1)) {
                return Boolean.TRUE;
            } else if ("off".startsWith(s) && (s.length() > 1)) {
                return Boolean.FALSE;
            } else if (s.equals("0")) {
                return Boolean.FALSE;
            } else if (s.equals("1")) {
                return Boolean.TRUE;
            }
        }

        return null;
    }

    // Helper classes
    private class Row implements Comparable {
        private int modelIndex;

        public Row(int index) {
            this.modelIndex = index;
        }

        public int compareTo(Object o) {
            int row1 = modelIndex;
            int row2 = ((Row) o).modelIndex;

            for (Iterator it = sortingColumns.iterator(); it.hasNext();) {
                Directive directive = (Directive) it.next();
                int column = directive.column;
                Object o1 = tableModel.getValueAt(row1, column);
                Object o2 = tableModel.getValueAt(row2, column);

                int comparison = 0;
                int comparison1 = 0;

                // Define null less than everything, except null.
                if ((o1 == null) && (o2 == null)) {
                    comparison = 0;
                } else if (o1 == null) {
                    comparison = -1;
                } else if (o2 == null) {
                    comparison = 1;
                } else {
                    comparison = compareColumnObjects(column, o1, o2);
                    //int lastCompareType1 = lastCompareType;
                    //comparison = getComparator(column).compare(o1, o2);
                    //comparison1 = doDictionary(o1.toString(),o2.toString());
                    //if ((comparison*comparison1) < 0) {
                            //System.out.println(column+" "+lastCompareType+" "+lastCompareType1+" "+comparison+" "+comparison1+" o1>"+o1.toString()+"<o2>"+o2.toString()+"<");
                    //}
                }

                if (comparison != 0) {
                    return (directive.direction == DESCENDING) ? (-comparison)
                                                               : comparison;
                }
            }

            return 0;
        }
    }

    private class TableModelHandler implements TableModelListener {
        public void tableChanged(TableModelEvent e) {
            // If we're not sorting by anything, just pass the event along.
            if (!isSorting()) {
                clearSortingState();
                fireTableChanged(e);

                return;
            }

            // If the table structure has changed, cancel the sorting; the
            // sorting columns may have been either moved or deleted from
            // the model.
            if (e.getFirstRow() == TableModelEvent.HEADER_ROW) {
                cancelSorting();
                fireTableChanged(e);

                return;
            }

            // We can map a cell event through to the view without widening
            // when the following conditions apply:
            //
            // a) all the changes are on one row (e.getFirstRow() == e.getLastRow()) and,
            // b) all the changes are in one column (column != TableModelEvent.ALL_COLUMNS) and,
            // c) we are not sorting on that column (getSortingStatus(column) == NOT_SORTED) and,
            // d) a reverse lookup will not trigger a sort (modelToView != null)
            //
            // Note: INSERT and DELETE events fail this test as they have column == ALL_COLUMNS.
            //
            // The last check, for (modelToView != null) is to see if modelToView
            // is already allocated. If we don't do this check; sorting can become
            // a performance bottleneck for applications where cells
            // change rapidly in different parts of the table. If cells
            // change alternately in the sorting column and then outside of
            // it this class can end up re-sorting on alternate cell updates -
            // which can be a performance problem for large tables. The last
            // clause avoids this problem.
            int column = e.getColumn();

            if ((e.getFirstRow() == e.getLastRow()) &&
                    (column != TableModelEvent.ALL_COLUMNS) &&
                    (getSortingStatus(column) == NOT_SORTED) &&
                    (modelToView != null)) {
                int viewIndex = getModelToView()[e.getFirstRow()];
                fireTableChanged(new TableModelEvent(TableSorter.this,
                        viewIndex, viewIndex, column, e.getType()));

                return;
            }

            // Something has happened to the data that may have invalidated the row order.
            clearSortingState();
            fireTableDataChanged();

            return;
        }
    }

    private class MouseHandler extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            JTableHeader h = (JTableHeader) e.getSource();
            TableColumnModel columnModel = h.getColumnModel();
            int viewColumn = columnModel.getColumnIndexAtX(e.getX());
            int column = columnModel.getColumn(viewColumn).getModelIndex();

            if (column != -1) {
                int status = getSortingStatus(column);

                if (!e.isControlDown()) {
                    cancelSorting();
                }

                // Cycle the sorting states through {NOT_SORTED, ASCENDING, DESCENDING} or
                // {NOT_SORTED, DESCENDING, ASCENDING} depending on whether shift is pressed.
                status = status + (e.isShiftDown() ? (-1) : 1);
                status = ((status + 4) % 3) - 1; // signed mod, returning {-1, 0, 1}
                setSortingStatus(column, status);
            }
        }
    }

    private static class Arrow implements Icon {
        private boolean descending;
        private int size;
        private int priority;

        public Arrow(boolean descending, int size, int priority) {
            this.descending = descending;
            this.size = size;
            this.priority = priority;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Color color = (c == null) ? Color.GRAY : c.getBackground();

            // In a compound sort, make each succesive triangle 20%
            // smaller than the previous one.
            int dx = (int) ((1.5 * size) / 2 * Math.pow(0.8, priority));
            int dy = descending ? dx : (-dx);

            // Align icon (roughly) with font baseline.
            y = y + ((5 * size) / 6) + (descending ? (-dy) : 0);

            int shift = descending ? 1 : (-1);
            g.translate(x, y);

            int[] ix = new int[3];
            int[] iy = new int[3];
            ix[0] = dx / 2;
            iy[0] = dy;
            ix[1] = 0;
            iy[1] = 0;
            ix[2] = dx;
            iy[2] = 0;
            g.setColor(color.darker());
            g.fillPolygon(ix, iy, 3);

            // Right diagonal.
            g.setColor(color.darker().darker());
            g.drawLine(dx / 2, dy, 0, 0);
            g.drawLine(dx / 2, dy + shift, 0, shift);

            // Left diagonal.
            g.setColor(color.brighter());
            g.drawLine(dx / 2, dy, dx, 0);
            g.drawLine(dx / 2, dy + shift, dx, shift);

            // Horizontal line.
            if (descending) {
                g.setColor(color.darker().darker().darker());
            } else {
                g.setColor(color.brighter().brighter());
            }

            g.drawLine(dx, 0, 0, 0);

            g.setColor(color);
            g.translate(-x, -y);
        }

        public int getIconWidth() {
            return size;
        }

        public int getIconHeight() {
            return size;
        }
    }

    private class SortableHeaderRenderer implements TableCellRenderer {
        private TableCellRenderer tableCellRenderer;

        public SortableHeaderRenderer(TableCellRenderer tableCellRenderer) {
            this.tableCellRenderer = tableCellRenderer;
        }

        public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {
            Component c = tableCellRenderer.getTableCellRendererComponent(table,
                    value, isSelected, hasFocus, row, column);

            if (c instanceof JLabel) {
                JLabel l = (JLabel) c;
                l.setHorizontalTextPosition(JLabel.LEFT);

                int modelColumn = table.convertColumnIndexToModel(column);
                l.setIcon(getHeaderRendererIcon(modelColumn,
                        l.getFont().getSize()));
            }

            return c;
        }
    }

    private static class Directive {
        private int column;
        private int direction;

        public Directive(int column, int direction) {
            this.column = column;
            this.direction = direction;
        }
    }
}
