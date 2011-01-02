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
import tcl.pkg.java.ReflectObject;

import java.awt.*;
import java.awt.datatransfer.*;

import java.io.*;

import java.lang.*;

import java.util.*;

import javax.swing.*;
import javax.swing.text.*;

public class SelectionCmd implements Command {

    static protected String getCommand = null;
    static protected Hashtable hasSelection = new Hashtable();
    static private JTextComponent selectionComponent = null;
    public void cmdProc(Interp interp, TclObject[] argv)
            throws TclException {
        int i;

        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv,
                    "option ?arg arg ...?");
        }

        if (argv[1].toString().equals("own")) {
            if (argv.length == 2) {
                if (selectionComponent != null) {
                    interp.setResult(selectionComponent.getName());
                }
            } else {
                Object windowObject = Widgets.get(interp,argv[2].toString()); 
                if (windowObject instanceof JTextComponent) {
                   selectionComponent = (JTextComponent) windowObject;
                }
            }
            return;
        } else if (argv[1].toString().equals("get")) {
            interp.setResult(getSelection(interp));
/*
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemSelection();
            System.out.println("got clip");

            if (clipboard == null) {
                System.out.println("clip null");
                interp.setResult("");

                return;
            }

            Transferable transferable = clipboard.getContents(null);

            if (transferable == null) {
                System.out.println("transf null");
                interp.setResult("");

                return;
            } else {
                System.out.println("getting flavors");

                DataFlavor[] dataFlavors = transferable.getTransferDataFlavors();
                System.out.println("gotting flavors");

                for (i = 0; i < dataFlavors.length; i++) {
                    System.out.println(dataFlavors[i].getMimeType());
                    System.out.println(dataFlavors[i].getHumanPresentableName());
                    interp.setResult("");
                }
            }
*/
        }

    }

    static void addSelectionWindow(String window) {
        if ((window != null) && (hasSelection.get(window) == null)) {
            hasSelection.put(window, window);
        }
    }
    static void setSelectionWindow(Object object) {
           if (object instanceof JTextComponent) {
               selectionComponent = (JTextComponent) object;
           }
    }
    String getSelection(Interp interp) throws TclException {
        String focusWindowName = FocusCmd.getFocusWindow();
        String selection = "";
        JTextComponent textComp  = null;
        if ((focusWindowName != null) && (!focusWindowName.equals(""))) {
                 Object focusObject = Widgets.get(interp, focusWindowName);
                 if (focusObject instanceof JTextComponent) {
                    textComp  = (JTextComponent) focusObject;
                 }
        }
        if (textComp == null) {
             textComp = selectionComponent;
        }
        if (textComp != null) {
            selection = textComp.getSelectedText();
        }
        return selection;
    }

}
