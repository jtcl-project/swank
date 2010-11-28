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
package com.onemoonscientific.swank;

import tcl.lang.*;

import java.awt.*;
import java.awt.event.*;

import java.lang.*;

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;


public class SwkTreeModel implements TreeModel {
    private Vector treeModelListeners = new Vector();
    private SwkTreeObject rootObject;
    protected String procBase = "";
    Interp interp = null;

    public SwkTreeModel(Interp interp, String procBase, String root) {
        rootObject = new SwkTreeObject(interp, root);
        this.interp = interp;
        this.procBase = procBase.intern();
    }

    /**
     * Used to toggle between show ancestors/show descendant and
     * to change the root of the tree.
     */
    public void showNewRoot(Object newRoot) {
        SwkTreeObject oldRoot = (SwkTreeObject) rootObject;

        if (newRoot != null) {
            rootObject = (SwkTreeObject) newRoot;
        }

        fireTreeStructureChanged(oldRoot);
    }

    //////////////// Fire events //////////////////////////////////////////////

    /**
     * The only event raised by this model is TreeStructureChanged with the
     * root as path, i.e. the whole tree has changed.
     */
    protected void fireTreeStructureChanged(Object oldRoot) {
        int len = treeModelListeners.size();
        TreeModelEvent e = new TreeModelEvent(this, new Object[] { oldRoot });

        for (int i = 0; i < len; i++) {
            ((TreeModelListener) treeModelListeners.elementAt(i)).treeStructureChanged(e);
        }
    }

    //////////////// TreeModel interface implementation ///////////////////////

    /**
     * Adds a listener for the TreeModelEvent posted after the tree changes.
     */
    public void addTreeModelListener(TreeModelListener l) {
        treeModelListeners.addElement(l);
    }

    /**
     * Returns the child of parent at index index in the parent's child array.
     */
    public Object getChild(Object parent, int index) {
        try {
            interp.eval(procBase + "getChild {" +
                ((SwkTreeObject) parent).string + "} " + index);

            return new SwkTreeObject(interp, interp.getResult().toString());
        } catch (TclException tclE) {
            System.out.println("getChild " + tclE.toString());
            System.out.println(interp.getResult().toString());
        }

        return (null);
    }

    /**
     * Returns the number of children of parent.
     */
    public int getChildCount(Object parent) {
        if ((parent == null) || (parent.toString().length() == 0)) {
            return 0;
        }

        try {
            interp.eval(procBase + "getChildCount {" +
                ((SwkTreeObject) parent).string + "}");

            return TclInteger.get(interp, interp.getResult());
        } catch (TclException tclE) {
            System.out.println("getChildCount " + tclE.toString());
            System.out.println(interp.getResult().toString());
        }

        return (0);
    }

    /**
     * Returns the index of child in parent.
     */
    public int getIndexOfChild(Object parent, Object child) {
        if ((parent == null) || (parent.toString().length() == 0)) {
            return -1;
        }

        if ((child == null) || (child.toString().length() == 0)) {
            return -1;
        }

        try {
            interp.eval(procBase + "getIndexOfChild {" +
                ((SwkTreeObject) parent).string + "} {" +
                ((SwkTreeObject) parent).string + "}");

            return TclInteger.get(interp, interp.getResult());
        } catch (TclException tclE) {
            System.out.println("getIndexOfChild " + tclE.toString());
            System.out.println(interp.getResult().toString());
        }

        return (-1);
    }

    /**
     * Returns the root of the tree.
     */
    public Object getRoot() {
        return rootObject;
    }

    /**
     * Returns true if node is a leaf.
     */
    public boolean isLeaf(Object node) {
        if ((node == null) || (node.toString().length() == 0)) {
            return true;
        }

        try {
            interp.eval(procBase + "getChildCount {" +
                ((SwkTreeObject) node).string + "}");

            return TclInteger.get(interp, interp.getResult()) == 0;
        } catch (TclException tclE) {
            System.out.println("getChildCount " + tclE.toString());
            System.out.println(interp.getResult().toString());
        }

        return (true);
    }

    /**
     * Removes a listener previously added with addTreeModelListener().
     */
    public void removeTreeModelListener(TreeModelListener l) {
        treeModelListeners.removeElement(l);
    }

    /**
     * Messaged when the user has altered the value for the item
     * identified by path to newValue.  Not used by this model.
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
        System.out.println("*** valueForPathChanged : " + path + " --> " +
            newValue);
    }
}
