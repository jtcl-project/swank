/*
 * ImageSelection.java
 *
 * Created on July 9, 2002, 7:50 PM
 */
package com.onemoonscientific.swank;

import com.onemoonscientific.swank.canvas.*;

import java.awt.*;
import java.awt.datatransfer.*;

import java.io.*;

import javax.swing.*;


/**
 *
 * @author  johnbruc
 * @version
 */
public class ImageSelection extends TransferHandler implements Transferable {
    private static DataFlavor[] flavors = { DataFlavor.imageFlavor };
    private Image image;

    /** Creates new ImageSelection */
    public ImageSelection() {
    }

    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
    }

    public boolean canImport(JComponent comp, DataFlavor[] flavor) {
        return false;
    }

    public Transferable createTransferable(JComponent comp) {
        image = null;

        if (comp instanceof SwkCanvas) {
            image = ((SwkCanvas) comp).paintImage();

            if (image == null) {
            }

            return this;
        } else {
            return null;
        }
    }

    public boolean isDataFlavorSupported(
        java.awt.datatransfer.DataFlavor dataFlavor) {
        return dataFlavor.equals(flavors[0]);
    }

    public java.lang.Object getTransferData(
        java.awt.datatransfer.DataFlavor dataFlavor)
        throws java.awt.datatransfer.UnsupportedFlavorException, 
            java.io.IOException {
        if (isDataFlavorSupported(dataFlavor)) {
            return image;
        }

        return null;
    }

    public java.awt.datatransfer.DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }
}
