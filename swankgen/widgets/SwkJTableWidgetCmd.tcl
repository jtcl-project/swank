#
# 
# Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, NJ, USA
#
# See the file \"LICENSE\" for information on usage and redistribution
# of this file.
# IN NO EVENT SHALL THE AUTHORS OR DISTRIBUTORS BE LIABLE TO
# ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR
# CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OF THIS
# SOFTWARE, ITS DOCUMENTATION, OR ANY DERIVATIVES THEREOF,
# EVEN IF THE AUTHORS HAVE BEEN ADVISED OF THE POSSIBILITY OF
# SUCH DAMAGE.
#
# THE AUTHORS AND DISTRIBUTORS SPECIFICALLY DISCLAIM ANY
# WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
# WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
# PURPOSE, AND NON-INFRINGEMENT.  THIS SOFTWARE
# IS PROVIDED ON AN "AS IS" BASIS, AND THE AUTHORS AND
# DISTRIBUTORS HAVE NO OBLIGATION TO PROVIDE MAINTENANCE,
# SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
#
#

append specialVars {
   SwkTableModel swkTableModel = null;
   int swkwidth = 100;
    int swkheight = 50;
    
    SwkListSelectionListener selectionListener = null;
}

append specialImports {

import javax.swing.table.*;

}

append specialInits {

        swkTableModel = new SwkTableModel(interp);

        TableSorter sorter = new TableSorter(swkTableModel);
        setModel(sorter);
        sorter.setTableHeader(getTableHeader()); //ADDED THIS

        //Set up tool tips for column headers.
        getTableHeader().setToolTipText("Click to specify sorting; Control-Click to specify secondary sorting");
        setDefaultRenderer(Color.class, new ColorRenderer(true));
        setDefaultEditor(Color.class, new ColorEditor());
}
append specialMethods {
         public SwkListSelectionListener getListSelectionListener() {
             return(selectionListener);
         }
         public void setListSelectionListener(SwkListSelectionListener selectionListener) {
             this.selectionListener = selectionListener;
         }


    public void setRows(int rows) {
        int nRows = swkTableModel.getRowCount();

        if (nRows != rows) {
            swkTableModel.setNRows(rows);
            swkTableModel.fireTableStructureChanged();
        }
    }

    public int getRows() {
        return swkTableModel.getRowCount();
    }

    public void setCols(int cols) {
        int nCols = swkTableModel.getColumnCount();

        if (nCols != cols) {
            swkTableModel.setNCols(cols);
            swkTableModel.fireTableStructureChanged();
        }
    }

    public int getCols() {
        return swkTableModel.getColumnCount();
    }


                        public void setCommand(String command) {
                                swkTableModel.setCommand(command);
                        }
                        public String getCommand() {
                                return(swkTableModel.getCommand());
                        }
                        public void setVariable(String variable) {
                                swkTableModel.setVariable(variable);
                        }
                        public String getVariable() {
                                return(swkTableModel.getVariable());
                        }
                        public void setUseCommand(boolean useCommand) {
                                swkTableModel.setUseCommand(useCommand);
                        }
                        public boolean getUseCommand() {
                                return (swkTableModel.getUseCommand());
                        }
                       public void setEditable(boolean editable) {
                                swkTableModel.setEditable(editable);
                        }
                        public boolean getEditable() {
                                return (swkTableModel.isEditable());
                        }

                }
                lappend specialGets "setCommand java.lang.String command -command"
                lappend specialGets "setVariable java.lang.String variable -variable"
                lappend specialGets "setUseCommand boolean useCommand -usecommand"
                lappend specialGets "setRows int rows -rows"
                lappend specialGets "setCols int cols -cols"
                lappend specialGets "setEditable boolean editable -editable"

