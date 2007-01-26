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


public class FocusCmd implements Command {
    static String focusWindow = null;

    public void cmdProc(Interp interp, TclObject[] argv)
        throws TclException {
        int i;

        if (argv.length == 1) {
            if (focusWindow != null) {
                interp.setResult(focusWindow);
            } else {
                interp.resetResult();
            }
        }
    }

    static public void setFocusWindow(String windowName) {
        focusWindow = windowName;
    }

    static public String getFocusWindow() {
        return focusWindow;
    }
}
