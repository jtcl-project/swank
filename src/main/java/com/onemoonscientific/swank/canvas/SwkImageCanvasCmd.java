/*
 * Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file "LICENSE" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 *
 */
package com.onemoonscientific.swank.canvas;


import tcl.lang.*;
import java.awt.*;
import javax.swing.*;

/** This class implements the SwkImageCanvas command. */
public class SwkImageCanvasCmd implements Command {

    /** Method creates the new widget object and calls configuration methods to process
     * command arguments.
     * @param interp The interpreter in which to create the command.
     * @param argv Argument array provides name of widget and command to be created in the first array element
     * and configuration options in the subsequent elements.
     * @throws TclException Exception thrown if an error occurs in creating the widget and the corresponding
     * Jacl command.
     */
    public void cmdProc(Interp interp, TclObject[] argv)
            throws TclException {
        if (false && !EventQueue.isDispatchThread()) {
            CmdProc cmdProc = new CmdProc(interp, argv);

            try {
                SwingUtilities.invokeAndWait(cmdProc);
            } catch (InterruptedException iE) {
                throw new TclException(interp, iE.toString());
            } catch (Exception e) {
                throw new TclException(interp, e.toString());
            }
        } else {
            cmdProcET(interp, argv);
        }
    }

    /**
     *
     * @param interp
     * @param argv
     * @throws TclException
     */
    public void cmdProcET(Interp interp, TclObject[] argv)
            throws TclException {
        int i;

        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv, "pathName ?options?");
        }

        if (!argv[1].toString().startsWith(".")) {
            throw new TclException(interp,
                    "bad window path name \"" + argv[1].toString() + "\"");
        }


        /*if (Widgets.exists(interp,argv[1].toString())) {
        swkcanvas = (SwkImageCanvas) Widgets.get(interp, argv[1].toString());

        if (swkcanvas.isCreated()) {
        throw new TclException(interp,
        "window name \"" + argv[1].toString() +
        "\" already exists in parent");
        }

        swkcanvas.className = className.intern();
        } else {
        if ((argv[1].toString().length() > 1) &&
        Character.isUpperCase(argv[1].toString().charAt(1))) {
        throw new TclException(interp,
        "window name starts with an upper-case letter: \"" +
        argv[1].toString().charAt(1) + "\"");
        }

        swkcanvas = new SwkImageCanvas(interp, argv[1].toString(), className);
        swkcanvas.className = className.intern();

        LinkedList children = null;
        interp.createCommand(argv[1].toString(), new SwkImageCanvasWidgetCmd());

        TclObject tObj = ReflectObject.newInstance(interp, SwkImageCanvas.class, swkcanvas);
        tObj.preserve();
        //Widgets.addNewWidget(interp, argv[1].toString(), tObj);
        }
         */
        interp.setResult(argv[1].toString());
    }

    private class CmdProc implements Runnable {

        Interp interp;
        TclObject[] argv = null;

        CmdProc(Interp interp, TclObject[] argv) {
            this.interp = interp;
            this.argv = new TclObject[argv.length];

            for (int i = 0; i < argv.length; i++) {
                this.argv[i] = argv[i].duplicate();
            }
        }

        public void run() {
            try {
                cmdProcET(interp, argv);
            } catch (TclException tclE) {
                interp.backgroundError();
            } finally {
                for (int i = 0; i < argv.length; i++) {
                    argv[i].release();
                }
            }
        }
    }
}
