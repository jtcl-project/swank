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
import java.util.HashSet;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.io.IOException;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import javax.swing.text.EditorKit;
import javax.swing.text.BadLocationException;

import javax.swing.*;

/**
 *
 * @author brucejohnson
 */
public class ClipboardCmd implements Command, ClipboardOwner {

    private static final String[] validCmds = {
        "append", "clear", "flavors","get", "pastewidget",};
    private static final int OPT_APPEND = 0;
    private static final int OPT_CLEAR = 1;
    private static final int OPT_FLAVORS = 2;
    private static final int OPT_GET = 3;
    private static final int OPT_PASTEWIDGET = 4;

    private static final int STRING = 0;
    private static final int RTF = 1;
    private static final int HTML = 2;
    private static final int IMAGE = 3;

    /**
     * 
     * @param interp
     * @param argv
     * @throws TclException
     */
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

            case OPT_FLAVORS: {
               Transferable transferable = clipboard.getContents(null);
                 TclObject resultList = TclList.newInstance();
                 if (transferable != null) {
                     DataFlavor[] dataFlavors = transferable.getTransferDataFlavors();
                     HashSet<String> flavorSet = new HashSet<String>();
                     for (int i=0;i<dataFlavors.length;i++) {
                         flavorSet.add(dataFlavors[i].getHumanPresentableName());
                     }
                     for (String flavorName:flavorSet) {
                         TclList.append(interp,resultList,TclString.newInstance(flavorName));
                     }
                }
                interp.setResult(resultList);
                break;
            }
            case OPT_GET: {
                int type = STRING;
                String imageName = null;
                for (int iArg=2;iArg<argv.length;iArg++) {
                   if (argv[iArg].toString().equals("-type")) {
                        if ((iArg+1) >= argv.length) {
                            throw new TclException(interp,"No value for \"-type\" arg");
                        }
                        if (argv[iArg+1].toString().equals("STRING")) {
                             type = STRING;
                        } else if (argv[iArg+1].toString().equals("IMAGE")) {
                             type = IMAGE;
                        } else if (argv[iArg+1].toString().equals("RTF")) {
                             type = RTF;
                        } else if (argv[iArg+1].toString().equals("HTML")) {
                             type = HTML;
                        } else {
                            throw new TclException(interp,"Invalid value \"" + argv[iArg+1].toString() + "\" for \"-type\" arg");
                        }
                   } else if (argv[iArg].toString().equals("-image")) {
                        if ((iArg+1) >= argv.length) {
                            throw new TclException(interp,"No value for \"-image\" arg");
                        }
                        imageName = argv[iArg+1].toString();
                   }
                }
                if (type == STRING) {
                    interp.setResult(getClipboardAsText(interp, clipboard));
                } else if (type == RTF) {
                    interp.setResult(getClipboardAsRTF(interp, clipboard));
                } else if (type == HTML) {
                    if (hasHTML(interp,clipboard)) {
                        interp.setResult(getClipboardAsHTML(interp, clipboard));
                    } else {
                        interp.setResult(rtfToHTML(interp,getClipboardAsRTF(interp, clipboard)));
                    }
                } else if (type == IMAGE) {
                    BufferedImage image = getClipboardAsImage(interp, clipboard);
                    if (image == null) {
                            throw new TclException(interp,"No image on clipboard");
                    }
                    if ((imageName == null) || imageName.equals("")) {
                        interp.setResult(ImageCmd.addImage(interp,image));
                    } else {
                        ImageCmd.addImage(interp,imageName,image);
                        interp.setResult(imageName);
                    }
                } else {
                }

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

    boolean hasHTML(Interp interp, Clipboard clipboard)
            throws TclException {
        Transferable transferable = clipboard.getContents(null);
        boolean hasHTML=false;
        if (transferable != null) {
            try {
                DataFlavor rtfFlavor = new DataFlavor("text/html;class=java.io.InputStream");
                hasHTML =  transferable.isDataFlavorSupported(rtfFlavor);
            } catch (Exception exc) {
                throw new TclException(interp, exc.toString());
            }
        }
        return hasHTML;
    }

    String getClipboardAsRTF(Interp interp, Clipboard clipboard)
            throws TclException {
        Transferable transferable = clipboard.getContents(null);

        if (transferable != null) {
            try {
                DataFlavor rtfFlavor = new DataFlavor("text/rtf;class=java.io.InputStream");
                if (transferable.isDataFlavorSupported(rtfFlavor)) {
                    StringBuilder sBuilder = new StringBuilder();
                    InputStream stream = (InputStream) transferable.getTransferData(rtfFlavor);
                    BufferedReader bufReader = new BufferedReader(new InputStreamReader(stream));
                    int b;
                    while ((b = bufReader.read()) != -1) {
                         sBuilder.append((char) b);
                    }
                    return sBuilder.toString();
                }
            } catch (Exception exc) {
                throw new TclException(interp, exc.toString());
            }
        }
        return "";
    }
    String getClipboardAsHTML(Interp interp, Clipboard clipboard)
            throws TclException {
        Transferable transferable = clipboard.getContents(null);

        if (transferable != null) {
            try {
                DataFlavor htmlFlavor = new DataFlavor("text/html;class=java.io.InputStream");
                if (transferable.isDataFlavorSupported(htmlFlavor)) {
                    StringBuilder sBuilder = new StringBuilder();
                    InputStream stream = (InputStream) transferable.getTransferData(htmlFlavor);
                    BufferedReader bufReader = new BufferedReader(new InputStreamReader(stream));
                    int b;
                    while ((b = bufReader.read()) != -1) {
                         sBuilder.append((char) b);
                    }
                    return sBuilder.toString();
                }
            } catch (Exception exc) {
                throw new TclException(interp, exc.toString());
            }
        }
        return "";
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
                return  transferable.getTransferData(DataFlavor.stringFlavor).toString();
                //return  transferable.getTransferData(DataFlavor.getTextPlainUnicodeFlavor()).toString();
                //return transferable.getTransferData(DataFlavor.selectBestTextFlavor(dataFlavors)).toString();
                //return transferable.getTransferData(DataFlavor.getTextPlainUnicodeFlavor(dataFlavors)).toString();
            } catch (Exception exc) {
                throw new TclException(interp, exc.toString());
            }
        }
    }
    BufferedImage getClipboardAsImage(Interp interp, Clipboard clipboard)
            throws TclException {
        Transferable transferable = clipboard.getContents(null);
        BufferedImage imageResult = null;
        if (transferable != null) {
            DataFlavor[] dataFlavors = transferable.getTransferDataFlavors();
            try {
                if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                    Object transferData =  transferable.getTransferData(DataFlavor.imageFlavor);
                    if (transferData instanceof BufferedImage) {
                       imageResult =  (BufferedImage) transferData;    
                    }
                }
            } catch (Exception exc) {
                throw new TclException(interp, exc.toString());
            }
        }
        return imageResult;
    }
    static String rtfToHTML (Interp interp,String string) throws TclException {
        Reader reader = new StringReader(string);
        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/rtf");
        EditorKit kitRtf = editorPane.getEditorKitForContentType("text/rtf");
        try {
            kitRtf.read(reader, editorPane.getDocument(), 0);
            kitRtf = null;
            EditorKit kitHtml = editorPane.getEditorKitForContentType("text/html");
            Writer writer = new StringWriter();
            kitHtml.write(writer, editorPane.getDocument(), 0, editorPane.getDocument().getLength());
            return writer.toString();
        } catch (BadLocationException e) {
            throw new TclException(interp,e.getMessage());
        } catch (IOException e) {
            throw new TclException(interp,e.getMessage());
        }
    }
}
