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

import java.io.*;

import java.lang.*;

import java.util.*;

import javax.swing.*;


public interface SwkWidget {
    public static final String NORMAL = "normal";
    public static final String DISABLED = "disabled";
    public static final String ACTIVE = "active";
    public static final String LEFT = "left";
    public static final String RIGHT = "right";
    public static final String CENTER = "center";

    public LinkedList getChildrenList();

    public void initChildrenList();

    public String getClassName();

    public String getName();

    public String getRelief();

    public int getMouseX();

    public int getMouseY();

    public boolean isCreated();

    public void setCreated(boolean state);

    public int getBorderWidth();

    public Insets getEmptyBorderInsets();

    public Vector getTagList();

    public void setTagList(Interp interp, TclObject tagListObj)
        throws TclException;

    public SwkMouseListener getMouseListener();

    public void setMouseListener(SwkMouseListener mouseListener);

    public SwkFocusListener getFocusListener();

    public void setFocusListener(SwkFocusListener focusListener);

    public SwkComponentListener getComponentListener();

    public void setComponentListener(SwkComponentListener componentListener);

    public SwkChangeListener getChangeListener();

    public void setChangeListener(SwkChangeListener changeListener);

    public SwkKeyListener getKeyListener();

    public void setKeyListener(SwkKeyListener keyListener);

    public SwkMouseMotionListener getMouseMotionListener();

    public void setMouseListener(SwkMouseMotionListener mouseMotionListener);

    public void close() throws TclException;

    public Vector getVirtualBindings();

    public void setVirtualBindings(Vector bindings);

    public void setValues(Setter setter, int iOpt);

    public void configure(Interp interp, TclObject[] argv, int start)
        throws TclException;
}
