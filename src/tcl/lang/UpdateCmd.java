/*
 * UpdateCmd.java --
 *
 *        Implements the "update" command.
 *
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 * RCS: @(#) $Id: UpdateCmd.java,v 1.4 2005/11/07 03:51:26 bruce_johnson Exp $
 *
 */
package tcl.lang;

import java.util.*;

import javax.swing.*;


/*
 * This class implements the built-in "update" command in Tcl.
 */
public class UpdateCmd implements Command {
    /*
     * Valid command options.
     */
    static final private String[] validOpts = { "idletasks", };
    static final int OPT_IDLETASKS = 0;

    /*
     *----------------------------------------------------------------------
     *
     * cmdProc --
     *
     *        This procedure is invoked as part of the Command interface to
     *        process the "update" Tcl command.  See the user documentation
     *        for details on what it does.
     *
     * Results:
     *        None.
     *
     * Side effects:
     *        See the user documentation.
     *
     *----------------------------------------------------------------------
     */
    private static HashSet repaintComponents = new HashSet();

    public void cmdProc(Interp interp, // Current interpreter.
        TclObject[] argv) // Argument list.
        throws TclException // A standard Tcl exception.
     {
        int flags;

        if (argv.length == 1) {
            flags = TCL.ALL_EVENTS | TCL.DONT_WAIT;
        } else if (argv.length == 2) {
            TclIndex.get(interp, argv[1], validOpts, "option", 0);

            /*
             * Since we just have one valid option, if the above call returns
             * without an exception, we've got "idletasks" (or abreviations).
             */
            flags = TCL.IDLE_EVENTS | TCL.DONT_WAIT;
        } else {
            throw new TclNumArgsException(interp, 1, argv, "?idletasks?");
        }

        while (interp.getNotifier().doOneEvent(flags) != 0) {
            /* Empty loop body */
        }

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                    }
                });
        } catch (Exception e) {
        }

        doRepaints();

        java.awt.Toolkit.getDefaultToolkit().sync();

        /*
         * Must clear the interpreter's result because event handlers could
         * have executed commands.
         */
        interp.resetResult();
    }

    synchronized public static void addRepaintRequest(JComponent jcomp) {
        repaintComponents.add(jcomp);
    }

    synchronized void doRepaints() {
        Iterator iter = repaintComponents.iterator();

        while (iter.hasNext()) {
            ((JComponent) iter.next()).repaint();
        }

        repaintComponents.clear();
    }
}


// end UpdateCmd
