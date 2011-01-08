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

import com.onemoonscientific.swank.canvas.*;
import tcl.lang.*;
import java.awt.*;
import java.awt.datatransfer.*;

import javax.swing.*;

public class ClipboardCmd implements Command, ClipboardOwner {

    static final private String[] validCmds = {
        "append", "clear", "get", "pastewidget",};
    static final private int OPT_APPEND = 0;
    static final private int OPT_CLEAR = 1;
    static final private int OPT_GET = 2;
    static final private int OPT_PASTEWIDGET = 3;

    public void cmdProc(Interp interp, TclObject[] argv)
            throws TclException {
        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv,
                    "option ?arg arg ...?");
        }

        int opt = TclIndex.get(interp, argv[1], validCmds, "option", 0);

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        switch (opt) {
            case OPT_APPEND: {
                String current = getClipboardAsText(interp, clipboard);
                StringSelection stringSelection = new StringSelection(current
                        + argv[argv.length - 1].toString());
                clipboard.setContents(stringSelection, this);

                break;
            }

            case OPT_CLEAR: {
                interp.resetResult();

                StringSelection stringSelection = new StringSelection("");
                clipboard.setContents(stringSelection, this);

                break;
            }

            case OPT_GET: {
                interp.setResult(getClipboardAsText(interp, clipboard));

                break;
            }

            case OPT_PASTEWIDGET: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 1, argv, "widgetName");
                }

                Component comp = null;

                comp = (Component) Widgets.get(interp, argv[2].toString());

                if (comp == null) {
                    throw new TclException(interp,
                            "can't get component for font command");
                }

                if (comp instanceof SwkCanvas) {
                    SwkCanvas swkcanvas = (SwkCanvas) comp;
                    TransferHandler handler = swkcanvas.getTransferHandler();
                    System.out.println("exporting to clipboard");
                    handler.exportToClipboard((JComponent) comp, clipboard,
                            TransferHandler.COPY);
                } else {
                    throw new TclException(interp,
                            "this widget can't be copied to clipboard yet");
                }

                interp.setResult("");

                break;
            }
        }
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }

    String getClipboardAsText(Interp interp, Clipboard clipboard)
            throws TclException {
        Transferable transferable = clipboard.getContents(null);

        if (transferable == null) {
            return "";
        } else {
            DataFlavor[] dataFlavors = transferable.getTransferDataFlavors();

            /*
            for (int i=0;i<dataFlavors.length;i++) {
            System.out.println(dataFlavors[i].getMimeType());
            System.out.println(dataFlavors[i].getHumanPresentableName());
            interp.setResult("");
            }
             */
            try {
                return transferable.getTransferData(DataFlavor.selectBestTextFlavor(
                        dataFlavors)).toString();
            } catch (Exception exc) {
                throw new TclException(interp, exc.toString());
            }
        }
    }
}
