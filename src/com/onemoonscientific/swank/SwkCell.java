/*
* Copyright (c) 2000 One Moon Scientfic, Inc. Inc., Westfield, N.J., USA
*
* See the file "LICENSE" for information on usage and redistribution
* of this file, and for a DISCLAIMER OF ALL WARRANTIES.
*
*/
package com.onemoonscientific.swank;

import tcl.lang.*;
import tcl.pkg.java.ReflectObject;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

import java.io.IOException;

import java.lang.*;

import java.net.URL;

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.tree.*;


public class SwkCell implements TableCellRenderer {
    static Hashtable resourceDB = null;
    String command = null;
    String className = null;
    int active = 0;
    TclObject tclObject = null;
    Interp interp;

    public SwkCell(Interp interp) {
        this.command = null;
        this.interp = interp;
    }

    public SwkCell(Interp interp, String command) {
        this.command = command;
        this.interp = interp;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
        TclObject tObj = null;
        String evalString = null;
        String strValue = value.toString();

        if (command != null) {
            evalString = command;
        } else {
            if (strValue.startsWith("[")) {
                int len = strValue.length();
                evalString = strValue.substring(1, len - 1);
            }
        }

        if (evalString != null) {
            try {
                doCommand(interp, table, hasFocus, isSelected, row, column,
                    strValue, evalString);
                tObj = (TclObject) Widgets.getWidget(interp,interp.getResult()
                                                                .toString());
            } catch (TclException tclE) {
                System.out.println(tclE.toString());
            }
        } else {
            tObj = (TclObject) Widgets.getWidget(interp,strValue.toString());
        }

        if (tObj == null) {
            System.out.println("no widget " + strValue.toString());

            return null;
        }

        try {
            SwkWidget swkWidget = (SwkWidget) ReflectObject.get(interp, tObj);

            return ((Component) swkWidget);
        } catch (TclException tclE) {
        }

        return (null);
    }

    // FIXME
    public void doCommand(Interp interp, JTable table, boolean hasFocus,
        boolean isSelected, int row, int col, String strValue, String command)
        throws TclException {
        int i;
        char type;
        int mCol = table.convertColumnIndexToModel(col);
        Rectangle rect = table.getCellRect(row, col, false);
        StringBuffer sbuf = new StringBuffer();

        for (i = 0; i < command.length(); i++) {
            if (command.charAt(i) != '%') {
                sbuf.append(command.charAt(i));

                continue;
            } else {
                i++;
                type = command.charAt(i);

                switch (type) {
                case 'w':
                    sbuf.append(Integer.toString(rect.width));

                    break;

                case 'h':
                    sbuf.append(Integer.toString(rect.height));

                    break;

                case 'c':
                    sbuf.append(Integer.toString(mCol));

                    break;

                case 'r':
                    sbuf.append(Integer.toString(row));

                    break;

                case 'C':
                    sbuf.append(Integer.toString(row));
                    sbuf.append(",");
                    sbuf.append(Integer.toString(mCol));

                    break;

                case 'f':

                    if (hasFocus) {
                        sbuf.append("1");
                    } else {
                        sbuf.append("0");
                    }

                    break;

                case 'S':

                    if (isSelected) {
                        sbuf.append("1");
                    } else {
                        sbuf.append("0");
                    }

                    break;

                case 'W':
                    sbuf.append(((SwkJTable) table).getName());

                    break;

                case 's':

                    if (strValue != null) {
                        TclObject list = TclList.newInstance();
                        TclList.append(interp, list,
                            TclString.newInstance(strValue));
                        sbuf.append(list.toString());
                    } else {
                        sbuf.append("{}");
                    }

                    break;
                }
            }
        }

        interp.eval(sbuf.toString());
    }
}
