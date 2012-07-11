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
import java.util.*;


/**
 *
 * @author brucejohnson
 */
public interface SwkWidget {

    /**
     *
     */
    public static final String NORMAL = "normal";
    /**
     *
     */
    public static final String READONLY = "readonly";
    /**
     *
     */
    public static final String DISABLED = "disabled";
    /**
     *
     */
    public static final String ACTIVE = "active";
    /**
     *
     */
    public static final String LEFT = "left";
    /**
     *
     */
    public static final String RIGHT = "right";
    /**
     *
     */
    public static final String CENTER = "center";

    /**
     *
     * @return
     */
    public LinkedList getChildrenList();

    /**
     *
     */
    public void initChildrenList();

    /**
     *
     * @return
     */
    public String getClassName();

    /**
     *
     * @return
     */
    public String getName();

    /**
     *
     * @return
     */
    public String getRelief();

    /**
     *
     * @return
     */
    public int getMouseX();

    /**
     *
     * @return
     */
    public int getMouseY();

    /**
     *
     * @return
     */
    public boolean isCreated();

    /**
     *
     * @param state
     */
    public void setCreated(boolean state);

    /**
     *
     * @return
     */
    public int getBorderWidth();

    /**
     *
     * @return
     */
    public Insets getEmptyBorderInsets();

    /**
     *
     * @return
     */
    public Vector getTagList();

    /**
     *
     * @param interp
     * @param tagListObj
     * @throws TclException
     */
    public void setTagList(Interp interp, TclObject tagListObj)
            throws TclException;

    /**
     *
     * @return
     */
    public SwkMouseListener getMouseListener();

    /**
     *
     * @param mouseListener
     */
    public void setMouseListener(SwkMouseListener mouseListener);
    /**
     *
     * @return
     */
    public SwkMouseWheelListener getMouseWheelListener();

    /**
     *
     * @param mouseListener
     */
    public void setMouseWheelListener(SwkMouseWheelListener mouseWheelListener);

    /**
     *
     * @return
     */
    public SwkFocusListener getFocusListener();

    /**
     *
     * @param focusListener
     */
    public void setFocusListener(SwkFocusListener focusListener);

    /**
     *
     * @return
     */
    public SwkComponentListener getComponentListener();

    /**
     *
     * @param componentListener
     */
    public void setComponentListener(SwkComponentListener componentListener);

    /**
     *
     * @return
     */
    public SwkChangeListener getChangeListener();

    /**
     *
     * @param changeListener
     */
    public void setChangeListener(SwkChangeListener changeListener);

    /**
     *
     * @return
     */
    public SwkKeyListener getKeyListener();

    /**
     *
     * @param keyListener
     */
    public void setKeyListener(SwkKeyListener keyListener);

    /**
     *
     * @return
     */
    public SwkMouseMotionListener getMouseMotionListener();

    /**
     *
     * @param mouseMotionListener
     */
    public void setMouseListener(SwkMouseMotionListener mouseMotionListener);

    /**
     *
     * @throws TclException
     */
    public void close() throws TclException;

    /**
     *
     * @return
     */
    public ArrayList<SwkBinding> getVirtualBindings();

    /**
     *
     * @param bindings
     */
    public void setVirtualBindings(ArrayList<SwkBinding> bindings);

    /**
     *
     * @param setter
     * @param iOpt
     */
    public void setValues(Setter setter, int iOpt);

    /**
     *
     * @param interp
     * @param argv
     * @param start
     * @throws TclException
     */
    public void configure(Interp interp, TclObject[] argv, int start)
            throws TclException;
}
