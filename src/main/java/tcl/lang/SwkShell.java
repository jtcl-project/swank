/*
 * Shell.java --
 *
 *        Implements the start up shell for Tcl.
 *
 * Copyright (c) 1997 Cornell University.
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 * RCS: @(#) $Id: SwkShell.java,v 1.4 2005/11/07 03:51:47 bruce_johnson Exp $
 */
package tcl.lang;

import java.io.*;

import java.util.*;


/**
 * The Shell class is similar to the Tclsh program: you can use it to
 * execute a Tcl script or enter Tcl command interactively at the
 * command prompt.
 */
public class SwkShell extends Shell {
    /*
     *----------------------------------------------------------------------
     *
     * main --
     *
     *        Main program for tclsh and most other Tcl-based applications.
     *
     * Results:
     *        None.
     *
     * Side effects:
     *        This procedure initializes the Tcl world and then starts
     *        interpreting commands; almost anything could happen, depending
     *        on the script being interpreted.
     *
     *----------------------------------------------------------------------
     */
    public static void main(String[] args) // Array of command-line argument strings.
     {
        String fileName = null;

        // Create the interpreter. This will also create the built-in
        // Tcl commands.
        Interp interp = new Interp();

        try {
            interp.evalResource("/com/onemoonscientific/swank/library/init.tcl");
        } catch (TclException e) {
            System.out.println(
                "could not open /com/onemoonscientific/swank/library/init.tcl");
            System.exit(1);
        }

        // Make command-line arguments available in the Tcl variables "argc"
        // and "argv".  If the first argument doesn't start with a "-" then
        // strip it off and use it as the name of a script file to process.
        // We also set the argv0 and tcl_interactive vars here.
        if ((args.length > 0) && !(args[0].startsWith("-"))) {
            fileName = args[0];
        }

        TclObject argv = TclList.newInstance();
        argv.preserve();

        try {
            int i = 0;
            int argc = args.length;

            if (fileName == null) {
                interp.setVar("argv0", TclString.newInstance("tcl.lang.Shell"),
                    TCL.GLOBAL_ONLY);
                interp.setVar("tcl_interactive", TclBoolean.newInstance(true),
                    TCL.GLOBAL_ONLY);
            } else {
                interp.setVar("argv0", TclString.newInstance(fileName),
                    TCL.GLOBAL_ONLY);
                interp.setVar("tcl_interactive", TclBoolean.newInstance(false),
                    TCL.GLOBAL_ONLY);
                i++;
                argc--;
            }

            for (; i < args.length; i++) {
                TclList.append(interp, argv, TclString.newInstance(args[i]));
            }

            interp.setVar("argv", argv, TCL.GLOBAL_ONLY);
            interp.setVar("argc", TclInteger.newInstance(argc), TCL.GLOBAL_ONLY);
        } catch (TclException e) {
            throw new TclRuntimeError("unexpected TclException: " + e);
        } finally {
            argv.release();
        }

        // Normally we would do application specific initialization here.
        // However, that feature is not currently supported.
        // If a script file was specified then just source that file
        // and quit.
        if (fileName != null) {
            try {
                if (fileName.startsWith("resource:")) {
                    interp.evalResource(fileName.substring(9));
                } else {
                    interp.evalFile(fileName);
                }
                Notifier notifier = interp.getNotifier();

                while (true) {
                // process events until "exit" is called.
                    notifier.doOneEvent(TCL.ALL_EVENTS);
                }
            } catch (TclException e) {
                int code = e.getCompletionCode();

                if (code == TCL.RETURN) {
                    code = interp.updateReturnInfo();

                    if (code != TCL.OK) {
                        System.err.println("command returned bad code: " +
                            code);
                    }
                } else if (code == TCL.ERROR) {
                    System.err.println(interp.getResult().toString());
                } else {
                    System.err.println("command returned bad code: " + code);
                }
            }

            // Note that if the above interp.evalFile() returns the main
            // thread will exit.  This may bring down the VM and stop
            // the execution of Tcl.
            //
            // If the script needs to handle events, it must call
            // vwait or do something similar.
            //
            // Note that the script can create AWT widgets. This will
            // start an AWT event handling thread and keep the VM up. However,
            // the interpreter thread (the same as the main thread) would
            // have exited and no Tcl scripts can be executed.
            interp.dispose();
            System.exit(0);
        }

        if (fileName == null) {
            try {
                interp.eval("::swank::cmdLineArgs");
            } catch (TclException e) {
                System.out.println("error "+e.getMessage());
            }
            // We are running in interactive mode. Start the ConsoleThread
            // that loops, grabbing stdin and passing it to the interp.
            ConsoleThread consoleThread = new ConsoleThread(interp);
            consoleThread.setDaemon(true);
            consoleThread.start();
            // Loop forever to handle user input events in the command line.
            Notifier notifier = interp.getNotifier();

            while (true) {
                // process events until "exit" is called.
                notifier.doOneEvent(TCL.ALL_EVENTS);
            }
        }
    }
}


// end class Shell
