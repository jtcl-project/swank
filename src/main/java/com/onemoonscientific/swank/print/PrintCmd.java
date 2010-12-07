package com.onemoonscientific.swank.print;

import com.onemoonscientific.swank.*;

import tcl.lang.*;
import tcl.pkg.java.ReflectObject;

import java.awt.*;
import java.awt.print.*;

import java.io.*;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.lang.*;

import java.util.*;

import javax.swing.*;

class PrintCmd implements Command {

    static Hashtable images = new Hashtable();

    public void cmdProc(Interp interp, TclObject[] argv)
            throws TclException {
        int i;

        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv,
                    "?-landscape|-portrait -margin marginValue? windowName");
        }

        TclObject tObj = (TclObject) Widgets.getWidget(interp, argv[argv.length
                - 1].toString());

        if (tObj == null) {
            throw new TclException(interp,
                    "bad window path name \"" + argv[argv.length - 1].toString()
                    + "\"");
        }

        int orientation = PageFormat.PORTRAIT;
        double margin = 36;
        boolean silent = false;
        boolean sizeToFit = false;
        boolean autoSelect = false;
        boolean createPaper = false;

        for (i = 1; i < (argv.length - 1); i++) {
            if ("-landscape".startsWith(argv[i].toString())) {
                orientation = PageFormat.LANDSCAPE;
                createPaper = true;
            } else if ("-portrait".startsWith(argv[i].toString())) {
                orientation = PageFormat.PORTRAIT;
                createPaper = true;
            } else if ("-silent".startsWith(argv[i].toString())) {
                silent = true;
            } else if ("-sizetofit".startsWith(argv[i].toString())) {
                sizeToFit = true;
                createPaper = true;
            } else if ("-autoselect".startsWith(argv[i].toString())) {
                autoSelect = true;
                createPaper = true;
            } else if ("-margin".startsWith(argv[i].toString())) {
                createPaper = true;
                i++;

                if (i >= (argv.length - 1)) {
                    throw new TclException(interp, "no value for margin");
                }

                margin = TclDouble.get(interp, argv[i]) * 72;
            }
        }

        Object obj = ReflectObject.get(interp, tObj);
        PrinterJob pj = PrinterJob.getPrinterJob();
        PageFormat pf = pj.defaultPage();
        if (!createPaper && !silent) {
            pf = pj.pageDialog(pf);
        } else {
            Paper paper = new Paper();
            double cWidth = ((Component) obj).getWidth();
            double cHeight = ((Component) obj).getHeight();

            if (autoSelect) {
                if (cWidth > cHeight) {
                    orientation = PageFormat.LANDSCAPE;
                } else {
                    orientation = PageFormat.PORTRAIT;
                }
            }

            if (sizeToFit) {
                if (orientation == PageFormat.LANDSCAPE) {
                    if (cHeight > cWidth) {
                        paper.setSize(cHeight + 2 * margin, cHeight + 2 * margin);
                        paper.setImageableArea(margin, margin, cHeight + margin, cHeight + margin);
                    } else {
                        paper.setSize(cHeight + 2 * margin, cWidth + 2 * margin);
                        paper.setImageableArea(margin, margin, cHeight + margin, cWidth + margin);
                    }
                } else {
                    if (cWidth > cHeight) {
                        paper.setSize(cWidth + 2 * margin, cWidth + 2 * margin);
                        paper.setImageableArea(margin, margin, cWidth + margin, cWidth + margin);
                    } else {
                        paper.setSize(cWidth + 2 * margin, cHeight + 2 * margin);
                        paper.setImageableArea(margin, margin, cWidth + margin, cHeight + margin);
                    }
                }
            } else {
                paper.setImageableArea(margin, margin,
                        paper.getWidth() - (margin * 2),
                        paper.getHeight() - (margin * 2));
            }

            pf.setPaper(paper);
            pf.setOrientation(orientation);
        }
        pj.setPrintable((Printable) obj, pf);

        if (silent) {
            try {
                pj.print();
            } catch (PrinterException e) {
                System.out.println(e);
            }
        } else {
            if (pj.printDialog()) {
                try {
                    disableDoubleBuffering((Component) obj);
                    pj.print();
                } catch (PrinterException e) {
                    System.out.println(e);
                } finally {
                    enableDoubleBuffering((Component) obj);
                }
            }
        }
    }

    public static void disableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(false);
    }

    public static void enableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(true);
    }
}
