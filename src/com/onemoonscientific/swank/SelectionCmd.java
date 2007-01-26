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
import java.awt.datatransfer.*;

import java.io.*;

import java.lang.*;

import java.util.*;

import javax.swing.*;
import javax.swing.text.*;


public class SelectionCmd implements Command {
    static protected String getCommand = null;
    static protected Hashtable hasSelection = new Hashtable();

    public void cmdProc(Interp interp, TclObject[] argv)
        throws TclException {
        int i;

        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv,
                "option ?arg arg ...?");
        }

        if (argv[1].toString().equals("own")) {
            Object windowObject = Widgets.get(interp, FocusCmd.getFocusWindow());
            interp.resetResult();

            if ((windowObject != null) &&
                    (windowObject instanceof JTextComponent)) {
                interp.setResult(FocusCmd.getFocusWindow());
            }

            return;
        } else if (argv[1].toString().equals("get")) {
            System.out.println("getting");

            Clipboard clipboard = Toolkit.getDefaultToolkit()
                                         .getSystemSelection();
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
        }

        /*
                    interp.resetResult();
                    if (FocusCmd.getFocusWindow() != null) {
                        Object windowObject = Widgets.get(interp, FocusCmd.getFocusWindow());
                        if ((windowObject != null) && (windowObject instanceof JTextComponent)) {
                            interp.setResult(((JTextComponent) windowObject).getSelectedText());
                            return;
                        }
                    }
                }
        */
    }

    static void addSelectionWindow(String window) {
        if ((window != null) && (hasSelection.get(window) == null)) {
            hasSelection.put(window, window);
        }
    }
}
