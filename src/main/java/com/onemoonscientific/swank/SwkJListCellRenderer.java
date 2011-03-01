/*
 * Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file "LICENSE" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 *
 */
package com.onemoonscientific.swank;

import java.awt.Component;
import java.awt.Color;
import javax.swing.ListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

class SwkJListCellRenderer  extends JLabel implements ListCellRenderer {

     // This is the only method defined by ListCellRenderer.
     // We just reconfigure the JLabel each time we're called.

     public Component getListCellRendererComponent(
       JList list,              // the list
       Object value,            // value to display
       int index,               // cell index
       boolean isSelected,      // is the cell selected
       boolean cellHasFocus)    // does the cell have focus
     {
         String s = value.toString();
         setText(s);
         if (isSelected) {
             Color background = list.getSelectionBackground();
             Color foreground = list.getSelectionForeground();
             if (value instanceof SwkJListWidgetCmd.ListItem) {
                  SwkJListWidgetCmd.ListItem listItem = (SwkJListWidgetCmd.ListItem) value;
                  if (listItem.getSelectBackground() != null) {
                      background = listItem.getSelectBackground();
                  }
                  if (listItem.getSelectForeground() != null) {
                      foreground = listItem.getSelectForeground();
                  }
            }
            setBackground(background);
            setForeground(foreground);
         } else {
             Color background = list.getBackground();
             Color foreground = list.getForeground();
             if (value instanceof SwkJListWidgetCmd.ListItem) {
                  SwkJListWidgetCmd.ListItem listItem = (SwkJListWidgetCmd.ListItem) value;
                  if (listItem.getBackground() != null) {
                      background = listItem.getBackground();
                  }
                  if (listItem.getForeground() != null) {
                      foreground = listItem.getForeground();
                  }
            }
            setBackground(background);
            setForeground(foreground);
         }
         setEnabled(list.isEnabled());
         setFont(list.getFont());
         setOpaque(true);
         return this;
     }
 }

