/*

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
package com.onemoonscientific.swank;

import tcl.lang.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class SwkTableModel extends AbstractTableModel
        implements TableModelListener {

    static private final int DEFAULT = 0;
    static private final int DATE = 1;
    static private final int INT = 2;
    static private final int ROWS = 0;
    static private final int COLUMNS = 1;
    static private final int HEADER = 2;
    static private final int GET = 3;
    static private final int SET = 4;
    String[] modes = {"rows", "columns", "header", "get", "set"};
    Interp interp = null;
    public String command = null;
    public String variable = null;
    public boolean useCommand = false;
    public boolean tableEditable = true;
    Hashtable colTable = new Hashtable();
    SimpleDateFormat dateFormat = null;
    private int nRows = 0;
    private int nCols = 0;
    Vector sortingColumns = new Vector();
    boolean ascending = true;
    int compares;
    private int sortMode;
    protected SwkTableModel model;
    ArrayList columnArrays = new ArrayList();
    ArrayList headers = new ArrayList();

    public SwkTableModel() {
    }

    public SwkTableModel(Interp interp) {
        this.interp = interp;
        this.command = "";

        for (int i = 0; i < getNCols(); i++) {
            ArrayList colList = new ArrayList();
            colList.ensureCapacity(getNRows());
            columnArrays.add(colList);
            headers.add(String.valueOf(i));
        }

        //addTableModelListener(this);
    }

    synchronized public int getNRows() {
        return nRows;
    }

    synchronized public void setNRows(int nRows) {
        this.nRows = nRows;
    }

    synchronized public int getNCols() {
        return nCols;
    }

    synchronized public void setNCols(int nCols) {
        this.nCols = nCols;
    }

    public void setInterp(Interp interp) {
        this.interp = interp;
    }

    public Interp getInterp() {
        return interp;
    }

    public SwkTableModel getModel() {
        return model;
    }

    public void setModel(SwkTableModel model) {
        this.model = model;
    }

    public void setCommand(String command) {
        this.command = command.intern();
        useCommand = true;
    }

    String getCommand() {
        return (command);
    }

    void setUseCommand(boolean useCommand) {
        this.command = command.intern();
        this.useCommand = useCommand;
    }

    boolean getUseCommand() {
        return (useCommand);
    }

    void setVariable(String variable) {
        this.variable = variable.intern();
        useCommand = false;
    }

    String getVariable() {
        return (variable);
    }

    void setEditable(boolean state) {
        this.tableEditable = state;
    }

    boolean isEditable() {
        return (tableEditable);
    }

    public void setColumnClass(Interp interp, int c, Class colClass) {
        colTable.put(Integer.valueOf(c), colClass);
        tableChanged(new TableModelEvent(this));
    }

    public Class getColumnClass(int c) {
        Class colClass = (Class) colTable.get(Integer.valueOf(c));

        if (colClass != null) {
            return (colClass);
        } else {
            return ("".getClass());
        }
    }

    public int getRowCount() {
        if (useCommand) {
            if ((command != null) && !command.equals("")) {
                doCommand(interp, command, ROWS, 0, 0, null);
            }
        }

        return getNRows();
    }

    public int getColumnCount() {
        if (useCommand) {
            if ((command != null) && !command.equals("")) {
                doCommand(interp, command, COLUMNS, 0, 0, null);
            }
        }

        return getNCols();
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return tableEditable;
    }

    /*    public int getIndexForRow(int row) {
    checkModel();
    int index = -1;
    if ((row >= 0) && (row < indexes.length)) {
    index = indexes[row];
    }
    return index;
    }

    public int getRowForIndex(int index) {
    checkModel();
    int row = -1;
    if ((index >= 0) && (index < rindexes.length)) {
    row = rindexes[index];
    }
    return row;
    }
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (useCommand) {
            if ((command == null) || command.equals("")) {
                return;
            }

            doCommand(interp, command, SET, row, col, value);
        } else {
            if ((variable != null) && !variable.equals("")) {
                TclObject tobj = null;

                if (value instanceof TclObject) {
                    tobj = (TclObject) value;
                } else {
                    tobj = TclString.newInstance(value.toString());
                }

                try {
                    interp.setVar(variable, (row + "," + col), tobj,
                            TCL.GLOBAL_ONLY);
                    fireTableCellUpdatedET(row, col);
                } catch (TclException tclE) {
                }
            }
        }
    }

    public Object getValueAt(int row, int col) {
        if (useCommand) {
            if ((command == null) || command.equals("")) {
                return "";
            }

            // FIXME Synchronize arraylists
            ArrayList cList = (ArrayList) columnArrays.get(col);
            Object value = "nu";

            if (cList.size() > row) {
                value = cList.get(row);
            }

            doCommand(interp, command, GET, row, col, null);

            return value;

            // return interp.getResult().toString();
        } else {
            if ((variable != null) && !variable.equals("")) {
                try {
                    TclObject tobj;
                    tobj = interp.getVar(variable, (row + "," + col),
                            TCL.GLOBAL_ONLY);

                    return (tobj.toString());
                } catch (TclException tclE) {
                    return ("");
                }
            } else {
                return ("");
            }
        }
    }

    void checkColumns() {
        int n = getNCols();

        for (int i = columnArrays.size(); i <= n; i++) {
            columnArrays.add(new ArrayList());
        }

        for (int i = headers.size(); i <= n; i++) {
            headers.add(String.valueOf(i));
        }
    }

    @Override
    public String getColumnName(int col) {
        if (useCommand) {
            if ((command == null) || command.equals("")) {
                return Integer.toString(col);
            }

            checkColumns();
            doCommand(interp, command, HEADER, 0, col, null);

            return (String) headers.get(col);
        } else {
            if ((variable != null) && !variable.equals("")) {
                try {
                    TclObject tobj;
                    tobj = interp.getVar(variable, ("-1," + col),
                            TCL.GLOBAL_ONLY);

                    return (tobj.toString());
                } catch (TclException tclE) {
                    return Integer.toString(col);
                }
            } else {
                return Integer.toString(col);
            }
        }
    }

    public void doCommand(Interp interp, String command, int mode, int row,
            int col, Object value) {
        int i;
        char type;
        StringBuffer sbuf = new StringBuffer();

        for (i = 0; i < command.length(); i++) {
            if (command.charAt(i) != '%') {
                sbuf.append(command.charAt(i));

                continue;
            } else {
                i++;
                type = command.charAt(i);

                switch (type) {
                    case 'c':
                        sbuf.append(Integer.toString(col));

                        break;

                    case 'r':
                        sbuf.append(Integer.toString(row));

                        break;

                    case 'C':
                        sbuf.append(Integer.toString(row));
                        sbuf.append(",");
                        sbuf.append(Integer.toString(col));

                        break;

                    case 'i':
                        sbuf.append(Integer.toString(mode));

                        break;

                    case 'm':
                        sbuf.append(modes[mode]);

                        break;

                    case 's':

                        if (value != null) {
                            sbuf.append(value.toString());
                        } else {
                            sbuf.append("{}");
                        }

                        break;
                }
            }
        }

        if (mode < 2) {
            //   interp.eval(sbuf.toString());
        } else {
        }

        SwkTableEvent bEvent = new SwkTableEvent(interp, mode, row, col,
                sbuf.toString());
        bEvent.invokeLater();
    }

    //
    // Implementation of the TableModelListener interface,
    //
    // By default forward all events to all the listeners.
    public void tableChanged(TableModelEvent e) {
        fireTableChanged(e);
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
    static boolean getBoolean(Interp interp, // The current interpreter.
            String string) // The string representation of the boolean.
            throws TclException // For malformed boolean values.
    {
        String s = string.toLowerCase();

        // The length of 's' needs to be > 1 if it begins with 'o',
        // in order to compare between "on" and "off".
        if (s.length() > 0) {
            if ("yes".startsWith(s)) {
                return true;
            } else if ("no".startsWith(s)) {
                return false;
            } else if ("true".startsWith(s)) {
                return true;
            } else if ("false".startsWith(s)) {
                return false;
            } else if ("on".startsWith(s) && (s.length() > 1)) {
                return true;
            } else if ("off".startsWith(s) && (s.length() > 1)) {
                return false;
            } else if (s.equals("0")) {
                return false;
            } else if (s.equals("1")) {
                return true;
            }
        }

        throw new TclException(interp,
                "expected boolean value but got \"" + string + "\"");
    }

    void fireTableStructureChangedET() {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                fireTableStructureChanged();
            }
        });
    }

    void fireTableCellUpdatedET(final int row, final int col) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                fireTableCellUpdated(row, col);
            }
        });
    }

    class SwkTableEvent extends TclEvent {

        Interp interp = null;
        String cmd = null;
        int mode = 0;
        int row = 0;
        int col = 0;

        public SwkTableEvent(Interp interp, int mode, int row, int col,
                String cmd) {
            this.interp = interp;
            this.row = row;
            this.col = col;
            this.mode = mode;
            this.cmd = cmd.intern();
        }

        public int processEvent(int flags) {
            if ((interp != null) && (cmd != null)) {
                TclObject tObj = null;

                try {
                    interp.eval(cmd);
                    tObj = interp.getResult();

                    if (mode == ROWS) {
                        int n = TclInteger.get(interp, tObj);

                        if (getNRows() != n) {
                            setNRows(n);
                            fireTableStructureChangedET();
                        }
                    } else if (mode == COLUMNS) {
                        int n = TclInteger.get(interp, tObj);

                        if (getNCols() != n) {
                            setNCols(n);
                            fireTableStructureChangedET();
                        }
                    } else if (mode == HEADER) {
                        String headerVal = tObj.toString();

                        if (!((String) headers.get(col)).equals(headerVal)) {
                            headers.set(col, headerVal);
                            fireTableStructureChangedET();
                        }
                    } else if (mode == GET) {
                        if (row >= 0) {
                            ArrayList cList = (ArrayList) columnArrays.get(col);

                            for (int i = cList.size(); i <= row; i++) {
                                cList.add("duck");
                            }

                            if (!tObj.toString().equals((String) cList.get(row))) {
                                cList.set(row, tObj.toString());
                                fireTableCellUpdatedET(row, col);
                            }
                        }
                    }
                } catch (TclException tE) {
                    interp.backgroundError();
                    System.out.println(tE.getMessage());
                }
            }

            return 1;
        }

        public void invokeLater() {
            interp.getNotifier().queueEvent(this, TCL.QUEUE_TAIL);
        }

        // using following can result in a deadlock
        public void invokeAndWait() {
            interp.getNotifier().queueEvent(this, TCL.QUEUE_TAIL);
            sync();
        }
    }
}
